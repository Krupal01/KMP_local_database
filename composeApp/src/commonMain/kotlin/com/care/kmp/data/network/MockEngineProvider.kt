package com.care.kmp.data.network

import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo as TodoModel
import com.care.kmp.domain.model.User
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock

object MockEngineProvider {

    private val json = Json { ignoreUnknownKeys = true }

    // ─── In-memory stores ─────────────────────────────────────────────────────

    private val users = mutableListOf(
        User(1, "Krupal"),
        User(2, "John")
    )
    private var currentUserId = users.maxOf { it.id }

    private val todos = mutableListOf(
        TodoModel(
            id = "1",
            title = "Buy groceries",
            description = "Milk, eggs, bread",
            isCompleted = false,
            priority = Priority.HIGH
        ),
        TodoModel(
            id = "2",
            title = "Read a book",
            description = "",
            isCompleted = true,
            priority = Priority.LOW
        )
    )

    // ─── Engine ───────────────────────────────────────────────────────────────

    val engine = MockEngine { request ->

        val path = request.url.encodedPath
        val method = request.method

        when {

            // ── User routes ───────────────────────────────────────────────────

            // GET /users
            method == HttpMethod.Get && path == "/users" -> {
                respond(
                    content = json.encodeToString(users),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            // POST /users
            method == HttpMethod.Post && path == "/users" -> {
                val body = json.decodeFromString<User>(request.body.toByteArray().decodeToString())
                currentUserId++
                val newUser = User(id = currentUserId, name = body.name)
                users.add(newUser)
                respond(
                    content = json.encodeToString(newUser),
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            // PUT /users/{id}
            method == HttpMethod.Put && path.startsWith("/users/") && !path.startsWith("/todos") -> {
                val id = path.substringAfter("/users/").toLongOrNull()
                    ?: return@MockEngine respondError(HttpStatusCode.BadRequest)
                val body = json.decodeFromString<User>(request.body.toByteArray().decodeToString())
                val index = users.indexOfFirst { it.id == id }
                if (index == -1) {
                    respondError(HttpStatusCode.NotFound)
                } else {
                    val updated = users[index].copy(name = body.name)
                    users[index] = updated
                    respond(
                        content = json.encodeToString(updated),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }

            // DELETE /users/{id}
            method == HttpMethod.Delete && path.startsWith("/users/") -> {
                val id = path.substringAfter("/users/").toLongOrNull()
                    ?: return@MockEngine respondError(HttpStatusCode.BadRequest)
                val removed = users.removeAll { it.id == id }
                if (!removed) respondError(HttpStatusCode.NotFound)
                else respond(content = "", status = HttpStatusCode.OK)
            }

            // ── Todo routes ───────────────────────────────────────────────────

            // GET /todos
            method == HttpMethod.Get && path == "/todos" -> {
                respond(
                    content = json.encodeToString(todos),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            // GET /todos/{id}
            method == HttpMethod.Get && path.startsWith("/todos/") -> {
                val id = path.substringAfter("/todos/")
                val todo = todos.find { it.id == id }
                if (todo == null) respondError(HttpStatusCode.NotFound)
                else respond(
                    content = json.encodeToString(todo),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            // POST /todos
            method == HttpMethod.Post && path == "/todos" -> {
                val body = json.decodeFromString<TodoModel>(request.body.toByteArray().decodeToString())
                val newTodo = body.copy(
                    id = Clock.System.now().toEpochMilliseconds().toString(),
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
                todos.add(newTodo)
                respond(
                    content = json.encodeToString(newTodo),
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            // PUT /todos/{id}
            method == HttpMethod.Put && path.startsWith("/todos/") -> {
                val id = path.substringAfter("/todos/")
                val body = json.decodeFromString<TodoModel>(request.body.toByteArray().decodeToString())
                val index = todos.indexOfFirst { it.id == id }
                if (index == -1) {
                    respondError(HttpStatusCode.NotFound)
                } else {
                    val updated = todos[index].copy(
                        title = body.title,
                        description = body.description,
                        priority = body.priority,
                        dueDate = body.dueDate
                    )
                    todos[index] = updated
                    respond(
                        content = json.encodeToString(updated),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }

            // PATCH /todos/{id}/toggle
            method == HttpMethod.Patch && path.matches(Regex("/todos/.+/toggle")) -> {
                val id = path.substringAfter("/todos/").substringBefore("/toggle")
                val index = todos.indexOfFirst { it.id == id }
                if (index == -1) {
                    respondError(HttpStatusCode.NotFound)
                } else {
                    val toggled = todos[index].copy(isCompleted = !todos[index].isCompleted)
                    todos[index] = toggled
                    respond(
                        content = json.encodeToString(toggled),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }

            // DELETE /todos/{id}
            method == HttpMethod.Delete && path.startsWith("/todos/") -> {
                val id = path.substringAfter("/todos/")
                val removed = todos.removeAll { it.id == id }
                if (!removed) respondError(HttpStatusCode.NotFound)
                else respond(content = "", status = HttpStatusCode.OK)
            }

            // DELETE /todos
            method == HttpMethod.Delete && path == "/todos" -> {
                todos.clear()
                respond(content = "", status = HttpStatusCode.OK)
            }

            else -> respondError(HttpStatusCode.NotFound)
        }
    }
}