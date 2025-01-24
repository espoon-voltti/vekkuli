package fi.espoo.vekkuli.boatSpace.dev

import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.fullDateTimeFormat
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Component

@Component
class DebugInfoOverlayView(
    private val timeProvider: TimeProvider,
    private val icons: Icons
) {
    fun render(isAuthenticated: Boolean): String =
        if (getEnv() == EnvType.Production || !isAuthenticated) {
            ""
        } else {
            // language=HTML
            """
            <div class="debug-info">
                <div class="debug-info__content">
                    <div class="tags has-addons" x-data="{
                        showOverlay: JSON.parse(localStorage.getItem('debugInfoOverlayVisible') || 'true'),
                        toggleOverlay() {
                            this.showOverlay = !this.showOverlay;
                            localStorage.setItem('debugInfoOverlayVisible', this.showOverlay);
                        }
                    }">
                        <span class="tag" title="Environment">
                            <a class="icon icon-small" :class="{ 'icon-rotate-270': showOverlay, 'icon-rotate-90': !showOverlay }" @click="toggleOverlay()">
                                ${icons.outlinedChevronDown}
                            </a>
                         </span>   
                        <span x-show="showOverlay" class="tag ${if (timeProvider.isOverwritten()) "is-warning" else "is-success"}" title="Current system time">
                            <i class="mr-s">Järjestelmän aika:</i>${timeProvider.getCurrentDateTime().format(fullDateTimeFormat)}
                        </span>
                        <span x-show="showOverlay" class="tag">
                            <a href="/dev/dashboard" class="icon is-small">
                                ${icons.cog}
                            </a>
                        </span>
                    </div>
                </div>
            </div>
            """.trimIndent()
        }
}
