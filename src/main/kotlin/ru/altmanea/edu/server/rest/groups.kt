package ru.altmanea.edu.server.rest

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.altmanea.edu.server.model.Config.Companion.groupsPath
import ru.altmanea.edu.server.model.Group
import ru.altmanea.edu.server.repo.groupsRepo
import ru.altmanea.edu.server.repo.studentsRepo

fun Route.groups() =
    route(groupsPath) {
        get {
            if (!groupsRepo.isEmpty()) {
                call.respond(groupsRepo.findAll())
            } else {
                call.respondText("No groups found", status = HttpStatusCode.NotFound)
            }
        }
        get("group/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val groupItem =
                groupsRepo[id] ?: return@get call.respondText(
                    "No student with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.response.etag(groupItem.etag.toString())
            call.respond(groupItem)
        }
        get("group/{id}/students") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

            val groupItem =
                groupsRepo[id] ?: return@get call.respondText(
                    "No student with id $id",
                    status = HttpStatusCode.NotFound
                )

            val students = studentsRepo.find { it.group == groupItem.elem.name  }
            call.respond(students)

        }

        post {
            val groups = call.receive<Group>()
            groupsRepo.create(groups)
            call.respondText("groups stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (groupsRepo.delete(id)) {
                call.respondText("groups removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            groupsRepo[id] ?: return@put call.respondText(
                "No groups with id $id",
                status = HttpStatusCode.NotFound
            )
            val newGroups = call.receive<Group>()
            groupsRepo.update(id, newGroups)
            call.respondText("Groups updates correctly", status = HttpStatusCode.Created)
        }
    }

