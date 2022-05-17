package ru.altmanea.edu.server.repo

import kotlinx.serialization.Serializable
import ru.altmanea.edu.server.model.Item
import java.util.*

@Serializable
class RepoItem<E>(
    override val elem: E,
    override val uuid: String = UUID.randomUUID().toString(),
    override val etag: Long = System.currentTimeMillis()
) : Item<E>