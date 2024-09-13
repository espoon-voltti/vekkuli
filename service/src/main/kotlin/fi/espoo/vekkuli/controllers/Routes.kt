package fi.espoo.vekkuli.controllers

enum class UserTypes(val path: String) {
    CITIZEN("kuntalainen"),
    EMPLOYEE("virkailija");

    companion object {
        fun fromPath(path: String): UserTypes {
            return entries.find { it.path == path }
                ?: throw IllegalArgumentException("Invalid user type")
        }
    }
}

class Routes {
    companion object {
        const val USERTYPE = "{usertype:kuntalainen|virkailija}"
    }
}
