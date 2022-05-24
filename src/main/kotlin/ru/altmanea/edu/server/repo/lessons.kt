package ru.altmanea.edu.server.repo

import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Group
import ru.altmanea.edu.server.model.Lessons
import ru.altmanea.edu.server.model.Student

val lessonsRepo = ListRepo<Lessons>()

fun ListRepo<Lessons>.urlByUUID(uuid: String) =
    this[uuid]?.let {
        Config.lessonsURL + it.uuid
    }

/*fun ListRepo<Lessons>.urlByGroups(Lessons: String) =
    this.find { it.getsName == Lessons }.let {
        if (it.size == 1)
            Config.lessonsURL + it.first().uuid
        else
            null
    }*/

val lessonsRepoTestData = listOf(
    Lessons("Biology"),
    Lessons("History"),
    Lessons("English"),
    Lessons("Physics"),
)