import component.*
import kotlinx.browser.document
import react.createElement
import react.dom.render
import react.query.QueryClient
import react.query.QueryClientProvider
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.router.dom.Link
import wrappers.cReactQueryDevtools

val queryClient = QueryClient()

fun main() {
    render(document.getElementById("root")!!) {
        HashRouter {
            QueryClientProvider {
                attrs.client = queryClient
                Link {
                    attrs.to = "/"
                    +"Students"
                }
                +"    "
                Link {
                    attrs.to = "/groups"
                    +"Groups"
                }
                +"    "
                Link {
                    attrs.to = "/lessons"
                    +"Lessons"
                }
                +"    "
                Routes {
                    Route {
                        attrs.index = true
                        attrs.element =
                            createElement(fcContainerStudentList())
                    }
                    Route {
                        attrs.path = "/student/:id"
                        attrs.element =
                            createElement(fcContainerStudent())

                    }
                    Route {
                        attrs.path = "/groups"
                        attrs.element =
                            createElement(fcContainerGroupsList())
                    }
                    Route {
                        attrs.path = "/groups/:id"
                        attrs.element =
                            createElement(fcContainerGroups())

                    }
                    Route {
                        attrs.path = "/lessons"
                        attrs.element =
                            createElement(fcContainerLessonsList())
                    }
                    Route {
                        attrs.path = "/lessons/:id"
                        attrs.element =
                            createElement(fcContainerLessons())

                    }
                    Route {
                        attrs.path = "/score"
                        attrs.element =
                            createElement(fcContainerScoreList())
                    }
                    Route {
                        attrs.path = "/score/:id"
                        attrs.element =
                            createElement(fcContainerScore())

                    }
                }
                child(cReactQueryDevtools()) {}
            }
        }
    }
}

