package wrappers

import react.Props
import react.ReactElement
import react.fc

class ReactQueryDevToolsOption(val initialIsOpen: Boolean = true) // ???

interface QueryError{
    val message: String
}

@JsModule("react-query/devtools")
@JsNonModule
external val ReactQueryDevtools: dynamic

val reactQueryDevtools: (options: dynamic) -> ReactElement = ReactQueryDevtools.ReactQueryDevtools

fun cReactQueryDevtools(options: ReactQueryDevToolsOption = ReactQueryDevToolsOption()) = fc("ReactQueryDevtools") { _: Props ->
    child(reactQueryDevtools(options))
}