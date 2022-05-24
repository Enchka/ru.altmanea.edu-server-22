package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.Props
import react.dom.*
import react.fc
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.dom.Link
import react.useRef
import ru.altmanea.edu.server.model.Config.Companion.lessonsURL
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Lessons
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json

interface LessonsListProps : Props {
    var lessons: List<Item<Lessons>>
    var addLessons: (String) -> Unit
    var deleteLessons: (Int) -> Unit
}

interface MySelect {
    val value: String
}

fun fcLessonList() = fc("LessonList") { props: LessonsListProps ->

    val lessonsRef = useRef<INPUT>()

    span {
        p {
            +"lessons: "
            input {
                ref = lessonsRef
            }
        }
        button {
            +"Add lessons"
            attrs.onClickFunction = {
                lessonsRef.current?.value?.let { lessons ->
                    props.addLessons(lessons)
                }
            }
        }
    }

    h3 { +"Lesson" }
    ol {
        props.lessons.mapIndexed { index, lessonsItem ->
            li {
                val lessons =
                    Lessons(lessonsItem.elem.name)
                Link {
                    attrs.to = "/lessons/${lessonsItem.uuid}"
                    +"${lessons.name}\t"
                }
                button {
                    +"X"
                    attrs.onClickFunction = {
                        props.deleteLessons(index)
                    }
                }
            }
        }
    }
}

@Serializable
class ClientItemLesson(
    override val elem: Lessons,
    override val uuid: String,
    override val etag: Long
) : Item<Lessons>

fun fcContainerLessonsList() = fc("QueryLessonsList") { _: Props ->
    val queryClient = useQueryClient()

    val query = useQuery<Any, QueryError, String, Any>(
        "LessonList",
        {
            fetchText(lessonsURL)
        })
    val addLessonsMutation = useMutation<Any, Any, Any, Any>(
        { lesson: Lessons ->
            axios<String>(jso {
                url = lessonsURL
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json"
                )
                data = Json.encodeToString(lesson)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("LessonList")
            }
        }
    )

    val deleteLessonsMutation = useMutation<Any, Any, Any, Any>(
        { lessonsItem: Item<Lessons> ->
            axios<String>(jso {
                url = "$lessonsURL/${lessonsItem.uuid}"
                method = "Delete"
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("LessonList")
            }
        }
    )
    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error .." }
    else {
        val items = Json.decodeFromString<List<ClientItemLesson>>(query.data ?: "")
        child(fcLessonList()) {
            attrs.lessons = items
            attrs.addLessons = { l ->
                addLessonsMutation.mutate(Lessons(l), null)
            }
            attrs.deleteLessons = {
                deleteLessonsMutation.mutate(items[it], null)
            }
        }
    }
}