# 📱 KMP Local Database App

A **Kotlin Multiplatform (KMP)** application targeting **Android** and **Web**, built with a clean **MVI architecture**, **multi-module project structure**, and **multi-module navigation**. The app demonstrates local database management, state-driven UI, and shared business logic across platforms.

---

## 🖼️ Screenshots

### 📱 Mobile (Android)

<table>
  <tr>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/1.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/2.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/3.png" width="180"/></td>
  </tr>
  <tr>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/4.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/5.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/6.png" width="180"/></td>
  </tr>
  <tr>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/7.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/8.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/9.png" width="180"/></td>
  </tr>
  <tr>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/10.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/11.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/12.png" width="180"/></td>
  </tr>
  <tr>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/13.png" width="180"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/14.png" width="180"/></td>
    <td align="center"></td>
  </tr>
</table>

### 🌐 Web

<table>
  <tr>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/web1.png" width="380"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/web2.png" width="380"/></td>
  </tr>
  <tr>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/web3.png" width="380"/></td>
    <td align="center"><img src="https://raw.githubusercontent.com/Krupal01/KMP_local_database/refs/heads/master/SS/web4.png" width="380"/></td>
  </tr>
</table>

---

## 🏗️ Architecture

### MVI (Model-View-Intent)

The app follows the **MVI** pattern across all modules:

```
Intent (User Action)
       ↓
  ViewModel
       ↓
   State Update
       ↓
  Composable UI (reacts to state)
```

- **Model** — Immutable UI state managed inside each ViewModel
- **View** — Compose UI that renders based on state
- **Intent** — Sealed classes representing user actions dispatched to the ViewModel

---

## 📦 Multi-Module Structure

```
root/
├── androidApp/              # Android entry point
├── composeApp/              # Shared Compose UI (Android + Web)
├── shared/
│   ├── core/                # Common utilities, base classes
│   ├── data/                # Repository implementations, SQLDelight, Ktor
│   ├── domain/              # Use cases, domain models
│   └── features/
│       ├── feature-a/       # Feature module (UI + ViewModel)
│       └── feature-b/       # Feature module (UI + ViewModel)
└── build-logic/             # Convention plugins / build config
```

Each feature module contains its own:
- Compose screens
- ViewModel with MVI state/intent
- Koin DI module
- Navigation graph entry point

---

## 🧭 Multi-Module Navigation

Navigation is handled using **Jetpack Navigation Compose** (`navigation-compose`) across modules. Each feature module exposes its own navigation graph, which is then composed into the root nav host.

```kotlin
// Root NavHost
NavHost(navController, startDestination = "feature_a") {
    featureANavGraph(navController)
    featureBNavGraph(navController)
}
```

---

## 🛠️ Tech Stack

| Category | Library | Version |
|---|---|---|
| **Language** | Kotlin Multiplatform | `2.3.0` |
| **UI** | Compose Multiplatform | `1.10.0` |
| **UI Components** | Material 3 | `1.10.0-alpha05` |
| **Navigation** | Navigation Compose | `2.9.2` |
| **Local DB** | SQLDelight | `2.2.1` |
| **Networking** | Ktor Client | `3.4.0` |
| **DI** | Koin | `4.1.1` |
| **Async** | Kotlinx Coroutines | `1.10.2` |
| **Date/Time** | Kotlinx Datetime | `0.7.1` |
| **Lifecycle** | AndroidX Lifecycle | `2.9.6` |
| **Push Notifications** | Firebase Messaging | `24.0.0` |
| **Testing** | MockK, Turbine, Robolectric | latest |

---

## 🌍 Platform Support

| Platform | Status |
|---|---|
| Android (minSdk 24+) | ✅ Supported |
| Web (Kotlin/Wasm or JS) | ✅ Supported |
| iOS | 🔜 Planned |

---

## 🗄️ Local Database — SQLDelight

SQLDelight powers the local persistence layer with a shared schema defined in the `commonMain` source set.

- **Android** uses `android-driver`
- **Web** uses `web-worker-driver`
- All queries are type-safe and generated at compile time

---

## 🌐 Networking — Ktor

Ktor is used for HTTP communication with platform-specific engines:

- **Android** → `ktor-client-android`
- **iOS** → `ktor-client-darwin`
- **Web** → `ktor-client-js`
- JSON serialization via `ktor-serialization-kotlinx-json`

---

## 💉 Dependency Injection — Koin

Koin is set up per module and composed at the app level:

```kotlin
startKoin {
    modules(coreModule, dataModule, featureAModule, featureBModule)
}
```

Compose integration uses `koin-compose-viewmodel` for ViewModel injection directly inside composables.

---

## 🧪 Testing

| Tool | Purpose |
|---|---|
| `MockK` | Mocking in unit tests |
| `Turbine` | Testing Kotlin Flows |
| `Robolectric` | Android unit tests without device |
| `kotlinx-coroutines-test` | Coroutine test utilities |
| `Hamcrest` | Assertion matchers |
| `Koin-test` | DI testing |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Meerkat or later
- JDK 17+
- Kotlin 2.3.0+
- Node.js (for web target)

### Run on Android

```bash
./gradlew :androidApp:installDebug
```

### Run on Web

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
# or for JS target
./gradlew :composeApp:jsBrowserDevelopmentRun
```

---

## 📄 License

```
MIT License — feel free to use, modify, and distribute.
```

---

> Built with ❤️ using Kotlin Multiplatform & Compose Multiplatform


This is a Kotlin Multiplatform project targeting Android, iOS, Web.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).