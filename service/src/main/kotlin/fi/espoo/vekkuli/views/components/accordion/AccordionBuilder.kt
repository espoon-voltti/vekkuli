package fi.espoo.vekkuli.views.components.accordion

import fi.espoo.vekkuli.views.Icons

class AccordionBuilder {
    private val openStateId = "accordionIsOpen"
    private var title: String? = null
    private var content: String? = null
    private var isOpen: Boolean = false

    fun setTitle(title: String) =
        apply {
            this.title = title
        }

    fun setContent(content: String) =
        apply {
            this.content = content
        }

    fun setIsOpen(isOpen: Boolean) =
        apply {
            this.isOpen = isOpen
        }

    fun build(): String {
        // language=HTML
        return """
            <div 
                class="accordion" 
                x-data="{ $openStateId: $isOpen }" 
                >
                <h4 class="accordion-title is-flex is-align-items-center" @click="$openStateId = !$openStateId">
                    $title
                    <span class="ml-5 icon icon-transform" style="margin-left: 1.5em" :class="{'icon-rotate-90': !$openStateId}">
                        ${Icons().outlinedChevronDown}
                    </span>  
                </h4>
                <div class="accordion-content" x-show="$openStateId">
                    $content
                </div>
            </div>
            """.trimIndent()
    }
}
