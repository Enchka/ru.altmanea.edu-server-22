package ru.altmanea.edu.server.repo

import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Groups
import ru.altmanea.edu.server.model.Student

val groupsRepo = ListRepo<Groups>()

fun ListRepo<Groups>.urlByUUID(uuid: String) =
    this[uuid]?.let {
        Config.groupsURL + it.uuid
    }

fun ListRepo<Groups>.urlByGroups(groups: String) =
    this.find { it.getName == groups }.let {
        if (it.size == 1)
            Config.groupsURL + it.first().uuid
        else
            null
    }

val groupsRepoTestData = listOf(
    Groups("29a"),
    Groups("29b"),
    Groups("29c"),
    Groups("29d"),
)