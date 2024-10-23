package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.Variable
import fi.espoo.vekkuli.repository.VariableRepository
import org.springframework.stereotype.Service

@Service
class VariableService(
    private val variableRepository: VariableRepository
) {
    fun set(
        id: String,
        value: String
    ): Variable = variableRepository.setVariable(id, value)

    fun get(id: String): Variable? = variableRepository.getVariable(id)

    fun delete(id: String): Boolean = variableRepository.deleteVariable(id)
}
