package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.dom.Link
import react.router.useParams
import ru.altmanea.edu.server.model.Config
import ru.altmanea.edu.server.model.Groups
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Student
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import kotlin.js.json

external interface GroupsProps : Props {
    var groups: Item<Groups>
    var students: List<Item<Student>>
    var updateGroups: (String) -> Unit
}

fun fcGroup() = fc("Groups") { props: GroupsProps ->

    val groupsRef = useRef<INPUT>()

    val (groups, setgroups) = useState(props.groups.elem.name)

    fun onInputEdit(setter: StateSetter<String>, ref: MutableRefObject<INPUT>) =
        { _: Event ->
            setter(ref.current?.value ?: "ERROR!")
        }

    span {

        p {
            +"groups: "
            input {
                ref = groupsRef
                attrs.value = groups
                attrs.onChangeFunction = onInputEdit(setgroups, groupsRef)
            }
        }
        button {
            +"Update Groups"
            attrs.onClickFunction = {
                groupsRef.current?.value?.let { groups ->
                    props.updateGroups(groups)
                }
            }
        }
    }

    ol {
        props.students.mapIndexed{ index, studentItems ->
            li {
                val student =
                    Student(studentItems.elem.firstname, studentItems.elem.surname, studentItems.elem.group,studentItems.elem.lessons,studentItems.elem.score)
                Link {
                    attrs.to = "/student/${studentItems.uuid}"
                    +"${student.firstname} ${student.surname}"
                }
            }
        }
    }
}

class MutationsData(
    val oldGroups: Item<Groups>,
    val newGroups: Groups,
)

fun fcContainerGroups() = fc("ContainerGroups") { _: Props ->
    val groupsParam = useParams()
    val queryClient = useQueryClient()

    val groupsId = groupsParam["id"] ?: "Route param error"

    val query = useQuery<Any, QueryError, AxiosResponse<Item<Groups>>, Any>(
        groupsId,
        {
            axios<Array<Groups>>(jso {
                url = "${Config.groupsPath}group/$groupsId"
            })
        }
    )
    val queryS = useQuery<Any, QueryError, AxiosResponse<Array<Item<Student>>>, Any>(
        "Students",
        {
            axios<Array<Student>>(jso {
                url = "${Config.groupsPath}group/$groupsId/students"
            })
        }
    )

    val updateGroupsMutations = useMutation<Any, Any, MutationsData, Any>(
        { mutationsData ->
            axios<String>(jso {
                url = "${Config.groupsURL}/${mutationsData.oldGroups.uuid}"
                method = "Put"
                headers = json(
                    "Content-Type" to "application/json",
                )
                data = JSON.stringify(mutationsData.newGroups)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(groupsId)
            }
        }
    )

    if (query.isLoading or queryS.isLoading) div { +"Loading .." }
    else if (query.isError or queryS.isError) div { +"Error!" }
    else {
        val groupsItem = query.data?.data!!
        val studentItems = queryS.data?.data?.toList() ?: emptyList()
        child(fcGroup()) {
            attrs.groups = groupsItem
            attrs.students = studentItems
            attrs.updateGroups = { g ->
                updateGroupsMutations.mutate(MutationsData(groupsItem, Groups(g)), null)
            }
        }
    }
}
