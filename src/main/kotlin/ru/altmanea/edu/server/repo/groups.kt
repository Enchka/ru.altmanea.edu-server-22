package ru.altmanea.edu.server.repo

import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Group

val groupsRepo = ListRepo<Group>()

fun ListRepo<Group>.urlByUUID(uuid: String) =
    this[uuid]?.let {
        Config.groupsURL + it.uuid
    }

fun ListRepo<Group>.urlByGroups(groups: String) =
    this.find { it.getName == groups }.let {
        if (it.size == 1)
            Config.groupsURL + it.first().uuid
        else
            null
    }

val groupsRepoTestData = listOf(
    Group("29a"),
    Group("29b"),
    Group("29c"),
    Group("29d"),
)