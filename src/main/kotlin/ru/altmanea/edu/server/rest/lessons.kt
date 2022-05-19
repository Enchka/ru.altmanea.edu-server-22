package ru.altmanea.edu.server.rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import ru.altmanea.edu.server.model.Config.Companion.lessonsPath
import ru.altmanea.edu.server.model.Lessons
import ru.altmanea.edu.server.repo.RepoItem
import ru.altmanea.edu.server.repo.lessonsRepo
import ru.altmanea.edu.server.repo.studentsRepo
import ru.altmanea.edu.server.repo.urlByUUID

fun Route.lesson() {
    route(lessonsPath) {
        get {
            if (!lessonsRepo.isEmpty()) {
                call.respond(lessonsRepo.findAll())
            } else {
                call.respondText("No lessons found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val lessonItem =
                lessonsRepo[id] ?: return@get call.respondText(
                    "No lesson with name $id",
                    status = HttpStatusCode.NotFound
                )
            call.response.etag(lessonItem.etag.toString())
            call.respond(lessonItem)
        }
        get("{id}/students") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val lessonItem =
                lessonsRepo[id] ?: return@get call.respondText(
                    "No lesson with name $id",
                    status = HttpStatusCode.NotFound
                )

            val students = studentsRepo.find { it.lessons == lessonItem.elem.name }

            call.respond(students)
        }
        post {
            val lesson = call.receive<Lessons>()
            lessonsRepo.create(lesson)
            call.respondText("Lesson stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (lessonsRepo.delete(id)) {
                call.respondText("Lesson removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
        put("{id}/name") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val lessonItem = lessonsRepo[id] ?: return@put call.respondText(
                "No lesson with id $id",
                status = HttpStatusCode.NotFound
            )
            val newName = call.receive<Lessons>().name
            val newLesson = lessonItem.elem.copy(name = newName)
            lessonsRepo.update(lessonItem.uuid, newLesson)
            call.respondText("Lesson name updates correctly", status = HttpStatusCode.Created)
        }
    }

    route("$lessonsPath{lessonId}/students/{studentId}") {
        post {
            when (val lsResult = lsParameters()) {
                is LSOk -> {
                    val oldElem = lsResult.lessonItem.elem
                    val newElem = oldElem.copy(students = oldElem.students + lsResult.studentLink)
                    lessonsRepo.update(lsResult.lessonItem.uuid, newElem)
                    call.respond(lessonsRepo[lsResult.lessonItem.uuid]!!)
                }
                is LSFail -> call.respondText(lsResult.text, status = lsResult.code)
            }
        }
        delete {
            when (val lsResult = lsParameters()) {
                is LSOk -> {
                    val oldElem = lsResult.lessonItem.elem
                    val newElem = oldElem.copy(students = oldElem.students - lsResult.studentLink)
                    lessonsRepo.update(lsResult.lessonItem.uuid, newElem)
                    call.respond(lessonsRepo[lsResult.lessonItem.uuid]!!)
                }
                is LSFail -> call.respondText(lsResult.text, status = lsResult.code)
            }
        }
    }
}

private sealed interface LSResult
private class LSOk(
    val lessonItem: RepoItem<Lessons>,
    val studentLink: String
) : LSResult

private class LSFail(
    val text: String,
    val code: HttpStatusCode
) : LSResult

private fun PipelineContext<Unit, ApplicationCall>.lsParameters(): LSResult {
    val lessonId = call.parameters["lessonId"]
        ?: return LSFail("LessonId wrong", HttpStatusCode.BadRequest)
    val studentId = call.parameters["studentId"]
        ?: return LSFail("StudentId wrong", HttpStatusCode.BadRequest)
    val lessonItem = lessonsRepo[lessonId]
        ?: return LSFail("No lesson with id $lessonId", HttpStatusCode.NotFound)
    val studentLink = studentsRepo.urlByUUID(studentId)
        ?: return LSFail("No student with id $studentId", HttpStatusCode.NotFound)
    return LSOk(lessonItem, studentLink)
}

