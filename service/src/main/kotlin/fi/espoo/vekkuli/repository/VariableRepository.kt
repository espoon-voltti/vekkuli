package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.Variable

interface VariableRepository {
    fun getVariable(id: String): Variable?

    fun setVariable(
        id: String,
        value: String
    ): Variable

    fun deleteVariable(id: String): Boolean
}
