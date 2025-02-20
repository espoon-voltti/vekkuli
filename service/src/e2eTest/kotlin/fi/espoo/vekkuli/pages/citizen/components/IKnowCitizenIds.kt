package fi.espoo.vekkuli.pages.citizen.components

import fi.espoo.vekkuli.pages.BasePage
import java.util.UUID

interface IKnowCitizenIds<T> where T : BasePage, T : IKnowCitizenIds<T> {
    companion object{
        final val citizenIdLeo: UUID = UUID.fromString("f5d377ea-5547-11ef-a1c7-7f2b94cf9afd")
        final val citizenIdOlivia: UUID = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
        final val citizenIdMikko: UUID = UUID.fromString("62d90eed-4ea3-4446-8023-8dad9c01dd34")
        final val organizationId: UUID = UUID.fromString("8b220a43-86a0-4054-96f6-d29a5aba17e7")
        final val citizenIdJorma: UUID = UUID.fromString("82722a75-793a-4cbe-a3d9-a3043f2f5731")
        final val citizenIdMarko: UUID = UUID.fromString("1128bd21-fbbc-4e9a-8658-dc2044a64a58")
    }
}
