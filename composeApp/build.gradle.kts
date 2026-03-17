import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.google.services)
    jacoco
}
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}
val mapsApiKey = localProperties.getProperty("MAPS_API_KEY", "")
jacoco {
    toolVersion = "0.8.12"
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.care.kmp")
            generateAsync.set(true)
        }
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.android.driver)
            implementation(libs.ktor.client.android)
            implementation(libs.firebase.messaging)
            implementation(project.dependencies.platform(libs.firebase.bom))
//            implementation(libs.mockito.core)
//            implementation(libs.mockito.kotlin)
//            implementation(libs.robolectric)
//            implementation(libs.turbine)
//            implementation(libs.hamcrest)
            implementation(libs.maps.compose)
        }
        commonMain.dependencies {
            implementation(project(":settings"))
            implementation(project(":schedule"))

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.material.icons.extended)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.mock)

            implementation(project.dependencies.platform("io.insert-koin:koin-bom:4.1.1"))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.test)

            implementation(libs.navigation.compose)

            implementation(libs.kotlinx.datetime)

        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.mockk.common)
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
        }

        jsMain.dependencies {
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.2.1"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(libs.web.worker.driver)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(libs.ktor.client.js)
            implementation(npm("@types/google.maps", "3.55.0"))

        }

        webMain.dependencies {
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.2.1"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(libs.web.worker.driver)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
        }

        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.hamcrest)
            implementation(libs.mockk)
            implementation(libs.kotlin.testJunit)
        }

    }
}

android {
    namespace = "com.care.kmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.care.kmp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        // OR if using Google Maps SDK, also add to manifest:
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

// Make the JS compile task depend on this
tasks.named("jsProcessResources") {   // use "jsProcessResources" for JS target
    doLast {
        val htmlFile = layout.buildDirectory
            .file("processedResources/js/main/index.html")
            .get().asFile

        if (htmlFile.exists()) {
            val content = htmlFile.readText()
            val updated = content.replace("YOUR_API_KEY", mapsApiKey)
            htmlFile.writeText(updated)
            println("✅ Maps API key injected into index.html")
        } else {
            println("❌ index.html not found at: ${htmlFile.absolutePath}")
        }
    }
}
