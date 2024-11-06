package fi.espoo.vekkuli.controllers

enum class UserType(
    val path: String
) {
    CITIZEN("kuntalainen"),
    EMPLOYEE("virkailija");

    companion object {
        fun fromPath(path: String): UserType =
            entries.find { it.path == path }
                ?: throw IllegalArgumentException("Invalid user type")
    }
}

class Routes {
    companion object {
        const val USERTYPE = "{usertype:kuntalainen|virkailija}"
    }
}
