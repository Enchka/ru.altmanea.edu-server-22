package ru.altmanea.edu.server.rest

import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal inline fun <reified T> TestApplicationCall.decodeBody() =
    Json.decodeFromString<T>(response.content ?: "")

internal fun TestApplicationRequest.setBodyAndHeaders(body: String) {
    setBody(body)
    addHeader("Content-Type", "application/json")
    addHeader("Accept", "application/json")
}