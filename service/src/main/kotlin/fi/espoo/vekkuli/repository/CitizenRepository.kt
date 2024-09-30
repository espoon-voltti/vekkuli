package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.*
import java.util.*

interface CitizenRepository {
    fun getMunicipalities(): List<Municipality>
}
