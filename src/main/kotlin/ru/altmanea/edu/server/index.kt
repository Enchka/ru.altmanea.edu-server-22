package ru.altmanea.edu.server

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import kotlinx.html.*

fun Route.index() {
    get("/") {
        call.respondHtml(HttpStatusCode.OK) {
            head {
                meta {
                    attributes += "charset" to "UTF-8"
                }
                title {
                    +"Web App Client"
                }
            }
            body {
                div {
                    attributes += "id" to "root"
                }
                script ("text/javascript", "client.js") { }
            }
        }
    }
    static {
        resource("/client.js","client.js")
        resource("/client.js.map","client.js.map")
    }
}