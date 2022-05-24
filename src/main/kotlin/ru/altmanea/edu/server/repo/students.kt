package ru.altmanea.edu.server.repo

import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Student

val studentsRepo = ListRepo<Student>()

fun ListRepo<Student>.urlByUUID(uuid: String) =
    this[uuid]?.let {
        Config.studentsURL + it.uuid
    }

fun ListRepo<Student>.urlByFirstname(firstname: String) =
    this.find { it.firstname == firstname }.let {
        if (it.size == 1)
            Config.studentsURL + it.first().uuid
        else
            null
    }

val studentsRepoTestData = listOf(
    Student("Sheldon", "Cooper", "29a"),
    Student("Leonard", "Hofstadter", "29b"),
    Student("Howard", "Wolowitz", "29c"),
    Student("Penny", "Hofstadter", "29d"),
)