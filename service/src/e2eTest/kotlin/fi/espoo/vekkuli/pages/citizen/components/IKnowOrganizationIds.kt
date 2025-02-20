package fi.espoo.vekkuli.pages.citizen.components

import fi.espoo.vekkuli.pages.BasePage
import java.util.UUID

interface IKnowOrganizationIds<T> where T : BasePage, T : IKnowOrganizationIds<T> {
    companion object{
        final val espoonPursiseura: UUID = UUID.fromString("8b220a43-86a0-4054-96f6-d29a5aba17e7")
        final val citizenInEspoonPursiseura: UUID = UUID.fromString("509edb00-5549-11ef-a1c7-776e76028a49")
    }
}
