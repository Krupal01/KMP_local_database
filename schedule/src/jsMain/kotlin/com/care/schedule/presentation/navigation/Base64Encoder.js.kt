package com.care.schedule.presentation.navigation

import kotlinx.browser.window

actual object Base64Encoder {
    actual fun encode(input: String): String = window.btoa(input)
    actual fun decode(input: String): String = window.atob(input)
}