package fi.espoo.vekkuli.views.components.accordion

import org.springframework.stereotype.Service

@Service
class Accordion {
    fun createBuilder(): AccordionBuilder {
        return AccordionBuilder()
    }
}
