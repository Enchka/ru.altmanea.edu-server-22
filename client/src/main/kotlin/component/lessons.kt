package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.dom.Link
import react.router.useParams
import ru.altmanea.edu.server.model.*
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json

external interface LessonsProps : Props {
    var lessons: Item<Lessons>
    var students: List<Item<Student>>
    var updateLessons: (String) -> Unit
}

fun fcLessons() = fc("Lessons") { props: LessonsProps ->

    val lessonsRef = useRef<INPUT>()

    val (lessons, setlessons) = useState(props.lessons.elem.name)

    fun onInputEdit(setter: StateSetter<String>, ref: MutableRefObject<INPUT>) =
        { _: Event ->
            setter(ref.current?.value ?: "ERROR!")
        }

    span {

        p {
            +"lessons: "
            input {
                ref = lessonsRef
                attrs.value = lessons
                attrs.onChangeFunction = onInputEdit(setlessons, lessonsRef)
            }
        }
        button {
            +"Update Lessons"
            attrs.onClickFunction = {
                lessonsRef.current?.value?.let { lessons ->
                    props.updateLessons(lessons)
                }
            }
        }
    }

    ol {
        props.students.mapIndexed{ index, studentItems ->
            li {
                val student =
                    Student(studentItems.elem.firstname, studentItems.elem.surname, studentItems.elem.group)
                Link {
                    attrs.to = "/student/${studentItems.uuid}"
                    +"${student.firstname} ${student.surname}"
                }
            }
        }
    }
}

class MutationssData(
    val oldLessons: Item<Lessons>,
    val newLessons: Lessons,
)

fun fcContainerLessons() = fc("ContainerLessons") { _: Props ->
    val lessonsParam = useParams()
    val queryClient = useQueryClient()

    val lessonsId = lessonsParam["id"] ?: "Route param error"

    val query = useQuery<Any, QueryError, String, Any>(
        lessonsId,
        {
            fetchText("${Config.lessonsPath}$lessonsId")
        }
    )
    val queryS = useQuery<Any, QueryError, String, Any>(
        "Students",
        {
            fetchText("${Config.lessonsPath}$lessonsId/students")
        }
    )

    val updateLessonsMutations = useMutation<Any, Any, MutationssData, Any>(
        { mutationssData ->
            axios<String>(jso {
                url = "${Config.lessonsURL}${mutationssData.oldLessons.uuid}/name"
                method = "Put"
                headers = json(
                    "Content-Type" to "application/json",
                )
                data = Json.encodeToString(mutationssData.newLessons)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(lessonsId)
            }
        }
    )

    if (query.isLoading or queryS.isLoading) div { +"Loading .." }
    else if (query.isError or queryS.isError) div { +"Error!" }
    else {
        val lessonsItem = Json.decodeFromString<ClientItemLesson>(query.data ?: "")
        val studentItems = Json.decodeFromString<List<ClientItemStudent>>(queryS.data ?: "")
        child(fcLessons()) {
            attrs.lessons = lessonsItem
            attrs.students = studentItems
            attrs.updateLessons = { l ->
                updateLessonsMutations.mutate(MutationssData(lessonsItem, Lessons(l)), null)
            }
        }
    }
}
