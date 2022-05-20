package ru.altmanea.edu.server.model

class Config {
    companion object {
        const val serverDomain = "localhost"
        const val serverPort = 8000
        const val serverApi = "1"
        const val serverUrl = "http://$serverDomain:$serverPort/"
        const val pathPrefix = "api$serverApi/"

        const val studentsPath = "${pathPrefix}students/"
        const val studentsURL = "$serverUrl$studentsPath"

        const val groupsPath = "${pathPrefix}groups/"
        const val groupsURL = "$serverUrl$groupsPath"

        const val lessonsPath = "${pathPrefix}lessons/"
        const val lessonsURL = "$serverUrl$lessonsPath"

        const val scorePath = "${pathPrefix}score/"
        const val scoreURL = "$serverUrl$lessonsPath"
    }
}