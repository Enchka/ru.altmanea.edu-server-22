//package ru.altmanea.edu.server.rest
//
//import io.ktor.application.*
//import io.ktor.http.*
//import io.ktor.server.testing.*
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import org.junit.Test
//import ru.altmanea.edu.server.main
//import ru.altmanea.edu.server.model.Config
//import ru.altmanea.edu.server.model.Student
//import ru.altmanea.edu.server.repo.RepoItem
//import kotlin.test.assertEquals
//
//internal class StudentsKtTest {
//    @Test
//    fun testStudentRoute() {
//        withTestApplication(Application::main) {
//
//            val studentItems = handleRequest(HttpMethod.Get, Config.studentsPath).run {
//                assertEquals(HttpStatusCode.OK, response.status())
//                decodeBody<List<RepoItem<Student>>>()
//            }
//            assertEquals(4, studentItems.size)
//            val sheldon = studentItems.find { it.elem.firstname == "Sheldon" }
//            check(sheldon != null)
//
//            handleRequest(HttpMethod.Get, Config.studentsPath + sheldon.uuid).run {
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("Sheldon", decodeBody<RepoItem<Student>>().elem.firstname)
//            }
//            handleRequest(HttpMethod.Get, Config.studentsPath + "Jack").run {
//                assertEquals(HttpStatusCode.NotFound, response.status())
//            }
//
//            handleRequest(HttpMethod.Post, Config.studentsPath) {
//                setBodyAndHeaders(
//                    Json.encodeToString(
//                        Student("Raj", "Koothrappali","29b")
//                    )
//                )
//            }.apply {
//                assertEquals(HttpStatusCode.Created, response.status())
//            }
//            val studentItemsWithRaj = handleRequest(HttpMethod.Get, Config.studentsPath).run {
//                decodeBody<List<RepoItem<Student>>>()
//            }
//            assertEquals(5, studentItemsWithRaj.size)
//            val raj = studentItemsWithRaj.find { it.elem.firstname == "Raj" }
//            check(raj != null)
//            assertEquals("Koothrappali", raj.elem.surname)
//
//            handleRequest(HttpMethod.Delete, Config.studentsPath + raj.uuid).apply {
//                assertEquals(HttpStatusCode.Accepted, response.status())
//            }
//            handleRequest(HttpMethod.Delete, Config.studentsPath + raj.uuid) .apply {
//                assertEquals(HttpStatusCode.NotFound, response.status())
//            }
//
//            val penny = studentItems.find { it.elem.firstname == "Penny" }
//            check(penny != null)
//            handleRequest(HttpMethod.Put, Config.studentsPath + penny.uuid) {
//                setBodyAndHeaders(
//                    Json.encodeToString(
//                        Student("Penny", "Waitress","29a")
//                    )
//                )
//            }.apply {
//                assertEquals(HttpStatusCode.Created, response.status())
//            }
//        }
//    }
//}