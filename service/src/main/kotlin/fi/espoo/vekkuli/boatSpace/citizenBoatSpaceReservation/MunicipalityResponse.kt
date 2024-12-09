package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.domain.Municipality

data class MunicipalityResponse(
    val code: Int,
    val name: String
)

fun List<Municipality>.toMunicipalityListResponse() =
    map {
        it.toMunicipalityResponse()
    }

fun Municipality.toMunicipalityResponse() =
    MunicipalityResponse(
        code = code,
        name = name,
    )
