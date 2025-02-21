package fi.espoo.vekkuli.shared

import java.util.UUID

object CitizenIds {
    val leo: UUID = UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd")
    val olivia: UUID = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
    val mikko: UUID = UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34")
    val jorma: UUID = UUID.fromString("82722a75-793a-4cbe-a3d9-a3043f2f5731")
    val marko: UUID = UUID.fromString("1128bd21-fbbc-4e9a-8658-dc2044a64a58")
    val espooCitizenWithoutReservationsId: UUID = mikko
    val nonEspooCitizenWithoutReservationsId: UUID = marko
    val citizenInEspoonPursiseura = olivia
}
