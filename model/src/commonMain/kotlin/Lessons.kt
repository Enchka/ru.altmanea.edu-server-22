package ru.altmanea.edu.server.model

import kotlinx.serialization.Serializable

@Serializable
data class Lessons(
    val name: String,
    val students: Set<String> = emptySet()
)