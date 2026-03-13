package com.care.schedule.presentation.navigation

expect object Base64Encoder {
    fun encode(input: String): String
    fun decode(input: String): String
}