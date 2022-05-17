package ru.altmanea.edu.server.model

import kotlinx.serialization.*

@Serializable
class Student(
    val firstname: String,
    val surname: String,
    val group: String,
    val lessons: String
) {
    val fullNameWithGroup: String
        get() = "$firstname $surname,$group, $lessons "
}