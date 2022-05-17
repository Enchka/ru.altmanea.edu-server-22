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
//import ru.altmanea.edu.server.model.Groups
//import ru.altmanea.edu.server.model.Student
//import ru.altmanea.edu.server.repo.RepoItem
//import kotlin.test.assertEquals
//
//internal class GroupsKtTest {
//    @Test
//    fun testGroupsRoute() {
//        withTestApplication(Application::main) {
//
//            val groupsItems = handleRequest(HttpMethod.Get, Config.groupsPath).run {
//                assertEquals(HttpStatusCode.OK, response.status())
//                decodeBody<List<RepoItem<Groups>>>()
//            }
//            assertEquals(4, groupsItems.size)
//            val name = groupsItems.find { it.elem.groups == "29b" }
//            check(name != null)
//
//            handleRequest(HttpMethod.Get, Config.groupsPath + "group/" + name.uuid).run {
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("29b", decodeBody<RepoItem<Groups>>().elem.groups)
//            }
//            handleRequest(HttpMethod.Get, Config.groupsPath + "29b").run {
//                assertEquals(HttpStatusCode.NotFound, response.status())
//            }
//
//            handleRequest(HttpMethod.Post, Config.groupsPath) {
//                setBodyAndHeaders(
//                    Json.encodeToString(
//                        Groups("29b")
//                    )
//                )
//            }.apply {
//                assertEquals(HttpStatusCode.Created, response.status())
//            }
//            val groupsItemsWithRaj = handleRequest(HttpMethod.Get, Config.groupsPath).run {
//                decodeBody<List<RepoItem<Groups>>>()
//            }
//            assertEquals(5, groupsItemsWithRaj.size)
//            val number = groupsItemsWithRaj.find { it.elem.groups == "29a" }
//            check(number != null)
//            assertEquals("29a", number.elem.groups)
//
//            handleRequest(HttpMethod.Delete, Config.groupsPath + number.uuid).apply {
//                assertEquals(HttpStatusCode.Accepted, response.status())
//            }
//            handleRequest(HttpMethod.Delete, Config.groupsPath + number.uuid) .apply {
//                assertEquals(HttpStatusCode.NotFound, response.status())
//            }
//
//            val numbertwo = groupsItems.find { it.elem.groups == "29b" }
//            check(numbertwo != null)
//            handleRequest(HttpMethod.Put, Config.groupsPath + numbertwo.uuid) {
//                setBodyAndHeaders(
//                    Json.encodeToString(
//                        Groups("29c")
//                    )
//                )
//            }.apply {
//                assertEquals(HttpStatusCode.Created, response.status())
//            }
//        }
//    }
//}