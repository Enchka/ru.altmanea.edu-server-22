package component

import kotlinext.js.jso
import kotlinx.html.INPUT
import kotlinx.html.js.onClickFunction
import react.Props
import react.dom.*
import react.fc
import react.query.useMutation
import react.query.useQuery
import react.query.useQueryClient
import react.router.dom.Link
import react.useRef
import ru.altmanea.edu.server.model.Config.Companion.groupsURL
import ru.altmanea.edu.server.model.Config.Companion.studentsURL
import ru.altmanea.edu.server.model.Groups
import ru.altmanea.edu.server.model.Item
import ru.altmanea.edu.server.model.Student
import wrappers.AxiosResponse
import wrappers.QueryError
import wrappers.axios
import kotlin.js.json

interface GroupsListProps : Props {
    var groups: List<Item<Groups>>
    var addGroups: (String) -> Unit
    var deleteGroups: (Int) -> Unit
}
fun fcGroupList() =fc("GroupList") { props: GroupsListProps ->

    val groupsRef = useRef<INPUT>()

    span {
        p {
            +"Groups: "
            input {
                ref = groupsRef
            }
        }
        button {
            +"Add group"
            attrs.onClickFunction = {
                groupsRef.current?.value?.let { groups ->
                    props.addGroups(groups)
                }
            }
        }
    }

    h3 {+"Group"}
    ol {
        props.groups.mapIndexed { index, groupsItem ->
            li {
                val groups =
                    Groups(groupsItem.elem.name)
                Link {
                    attrs.to = "/groups/${groupsItem.uuid}"
                    +"${groups.name}\t"
                }
                button {
                    +"X"
                    attrs.onClickFunction = {
                        props.deleteGroups(index)
                    }
                }
            }
        }
    }
}
fun fcContainerGroupsList() = fc("QueryGroupsList") { _: Props ->
    val queryClient = useQueryClient()

    val query = useQuery<Any, QueryError, AxiosResponse<Array<Item<Groups>>>, Any>(
        "GroupList",
        {
            axios<Array<Groups>>(jso {
                url = groupsURL
            })
        })
    val addGroupsMutation = useMutation<Any, Any, Any, Any>(
        { group: Groups ->
            axios<String>(jso {
                url = groupsURL
                method = "Post"
                headers = json(
                    "Content-Type" to "application/json"
                )
                data = JSON.stringify(group)
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("GroupList")
            }
        }
    )

    val deleteGroupsMutation = useMutation<Any, Any, Any, Any>(
        { groupsItem: Item<Groups> ->
            axios<String>(jso {
                url = "$groupsURL/${groupsItem.uuid}"
                method = "Delete"
            })
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>("GroupList")
            }
        }
    )
    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error .." }
    else {
        val items = query.data?.data?.toList() ?: emptyList()
        child(fcGroupList()) {
            attrs.groups = items
            attrs.addGroups = { g ->
                addGroupsMutation.mutate(Groups(g), null)
            }
            attrs.deleteGroups = {
                deleteGroupsMutation.mutate(items[it], null)
            }
        }
    }
}