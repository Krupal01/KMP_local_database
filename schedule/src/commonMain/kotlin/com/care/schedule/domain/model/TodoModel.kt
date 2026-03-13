package com.care.schedule.domain.model

import com.care.schedule.presentation.navigation.Base64Encoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TodoModel(
    val id: String = "",
    val title: String,
    val description: String = "",
    val priority: String = ""
){

    fun encode(): String {
        val json = Json.encodeToString(this)
        return Base64Encoder.encode(json)
    }

    companion object{
        fun decode(encoded: String): TodoModel {
            val json = Base64Encoder.decode(encoded)
            return Json.decodeFromString(json)
        }
    }
}
