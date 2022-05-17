package ru.altmanea.edu.server.model

import kotlinx.serialization.*

@Serializable
class Groups(
    val name : String
) {
    val getName: String
        get() = "$name "
}