package com.care.schedule.presentation.navigation

import android.util.Base64

actual object Base64Encoder {
    actual fun encode(input: String): String =
        Base64.encodeToString(
            input.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP
        )

    actual fun decode(input: String): String =
        Base64.decode(input, Base64.URL_SAFE or Base64.NO_WRAP)
            .toString(Charsets.UTF_8)
}