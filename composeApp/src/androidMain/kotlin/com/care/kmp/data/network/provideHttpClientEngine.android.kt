package com.care.kmp.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.android.AndroidClientEngine

actual fun provideHttpClientEngine(): HttpClientEngine = Android.create()