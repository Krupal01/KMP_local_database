package com.care.kmp.data

import com.care.kmp.domain.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiService{

    val json = Json { ignoreUnknownKeys = true }

    // In-memory database
    val users = mutableListOf(
        User(1, "Krupal"),
        User(2, "John")
    )

    var currentId = users.maxOf { it.id }

    val mockEngine = MockEngine { request ->

        when (request.method to request.url.encodedPath) {

            // GET /users
            HttpMethod.Get to "/users" -> {
                respond(
                    content = json.encodeToString(users),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

            // POST /users
            HttpMethod.Post to "/users" -> {

                val bodyText = request.body.toByteArray().decodeToString()
                val requestUser = json.decodeFromString<User>(bodyText)

                currentId++

                val newUser = User(
                    id = currentId,
                    name = requestUser.name
                )

                users.add(newUser)

                respond(
                    content = json.encodeToString(newUser),
                    status = HttpStatusCode.Created,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

            else -> respondError(HttpStatusCode.NotFound)
        }
    }


    private val httpClient = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }
    suspend fun getData(): List<User> {
        return httpClient.get("https://mockapi.com/users").body()
    }

    suspend fun addUser(name: String): User {
        return httpClient.post("https://mockapi.com/users") {
            contentType(ContentType.Application.Json)
            setBody(User(0, name)) // ID ignored, auto-generated
        }.body()
    }
}