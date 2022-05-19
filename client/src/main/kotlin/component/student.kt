package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.SELECT
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.query.Query
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.dom.Link
import react.router.useParams
import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Lessons
import ru.altmanea.edu.server.model.Student
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import wrappers.fetchText
import kotlin.js.json

external interface StudentProps : Props {
    var students: Item<Student>
    var updateStudent: (String, String,String, String) -> Unit
    var addLessons: (String) -> Unit
    var lessons: List<Item<Lessons>>
    var lessonStudent: List<Item<Lessons>>
}


fun fcStudent() = fc("Student") { props: StudentProps ->
    val firstnameRef = useRef<INPUT>()
    val surnameRef = useRef<INPUT>()
    val groupsRef = useRef<INPUT>()
    val lessonsRef = useRef<INPUT>()
    val selectRef = useRef<SELECT>()

    val (firstname, setFirstname) = useState(props.students.elem.firstname)
    val (surname, setSurname) = useState(props.students.elem.surname)
    val (groups, setGroups) = useState(props.students.elem.group)
    val (lessons, setLessons) = useState(props.students.elem.lessons)

    fun onInputEdit(setter: StateSetter<String>, ref: MutableRefObject<INPUT>) =
        { _: Event ->
            setter(ref.current?.value ?: "ERROR!")
        }

    span {
        p {
            +"Firstname: "
            input {
                ref = firstnameRef
                attrs.value = firstname
                attrs.onChangeFunction = onInputEdit(setFirstname, firstnameRef)
            }
        }
        p {
            +"Surname: "
            input {
                ref = surnameRef
                attrs.value = surname
                attrs.onChangeFunction = onInputEdit(setSurname, surnameRef)
            }
            p {
                +"Groups: "
                input {
                    ref = groupsRef
                    attrs.value = groups
                    attrs.onChangeFunction = onInputEdit(setGroups, groupsRef)
                }
                /*p {
                    +"Lessons:"
                }

                select {
                    ref = selectRef
                    for(lesson in props.lessons) {
                        option {
                            +lesson.elem.name
                            attrs.value = lesson.uuid
                        }
                    }
                }
                button {
                    +"Add lessons"
                    attrs.onClickFunction = {
                        val value = selectRef.current.unsafeCast<MySelect>().value
                        props.addLessons(value)
                    }
                }

                ol {
                    for (lesson in props.lessonStudent) {
                        li {
                            Link {
                                attrs.to = "/lessons/${lesson.uuid}"
                                +"${lesson.elem.name}\t"
                            }
                            button {
                                +"X"
                                attrs.onClickFunction = {
                                    //props.deleteLessons(index)
                                }
                            }
                        }
                    }
                }*/

                p {
                    +"Lesson: ${props.students.elem.lessons}"
                }

                button {
                    +"Update student"
                    attrs.onClickFunction = {
                        firstnameRef.current?.value?.let { firstname ->
                            surnameRef.current?.value?.let { surname ->
                                groupsRef.current?.value?.let { groups ->
                                    props.updateStudent(firstname, surname, groups, props.students.elem.lessons)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class MutationData(
    val oldStudent: Item<Student>,
    val newStudent: Student,
)

fun fcContainerStudent() = fc("ContainerStudent") { _: Props ->
    val studentParams = useParams()
    val queryClient = useQueryClient()

    val studentId = studentParams["id"] ?: "Route param error"

    val query = useQuery<Any, QueryError,String, Any>(
        studentId,
        {
            fetchText(
                Config.studentsPath + studentId
            )
        })

    val querys = useQuery<Any, QueryError, String, Any>(
        "LessonList",
        {
            fetchText(
                Config.lessonsPath
            )
        })
    val Query = useQuery<Any, QueryError, String, Any>(
        "LessonStudent",
        {
            fetchText(
                "${Config.studentsPath}$studentId/lessons"
            )
        })


    val updateStudentMutation = useMutation<Any, Any, MutationData, Any>(
        { mutationData ->
            axios<String>(jso {
                url = "${Config.studentsURL}/${mutationData.oldStudent.uuid}"
                method = "Put"
                headers = json(
                    "Content-Type" to "application/json",
                )
                data = Json.encodeToString(mutationData.newStudent)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(studentId)
            }
        }
    )
    val addLessonsMutation = useMutation<Any, Any, String, Any>(
        { uuidLesson ->
            axios<String>(jso {
                url = "${Config.lessonsPath}$uuidLesson/students/$studentId"
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json"
                )
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(studentId)
            }
        }
    )

    if (query.isLoading or querys.isLoading or Query.isLoading) div { +"Loading .." }
    else if (query.isError or querys.isError or Query.isError) div { +"Error!" }
    else {
        val studentItem =
            Json.decodeFromString<ClientItemStudent>(query.data ?: "")
        val lessonsItem=
            Json.decodeFromString<List<ClientItemLesson>>(querys.data ?: "")
        val lessonsStudent=
            Json.decodeFromString<List<ClientItemLesson>>(Query.data ?: "")

        child(fcStudent()) {
            attrs.students = studentItem
            attrs.lessons = lessonsItem
            attrs.updateStudent = { f, s, g, l ->
                updateStudentMutation.mutate(MutationData(studentItem, Student(f, s, g, l)), null)
            }
            attrs.addLessons = { l ->
                addLessonsMutation.mutate(l, null)
            }
            attrs.lessonStudent = lessonsStudent
        }
    }
}
