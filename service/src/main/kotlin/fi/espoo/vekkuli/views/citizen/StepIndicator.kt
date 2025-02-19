package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StepIndicator {
    @Autowired
    lateinit var messageUtil: MessageUtil

    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(currentStep: Int): String {
        // language=HTML
        return """
            <div class="container" id="header" th:fragment="stepIndicator(currentStep)">
                <div class="columns is-mobile" id="step-indicator">
                    <div class="column">
                        <div id="chooseBoatSpace" class="step mb-m ${if (currentStep >= 1) "visited" else ""}" ></div>
                        <p class="is-uppercase has-text-centered title is-7">
                            ${t("boatApplication.boatSpaceChooseHeader")}
                        </p>
                    </div>
                    <div class="column">
                        <div id="fillInformation" class="step mb-m ${if (currentStep >= 2) "visited" else ""}"></div>
                        <p class="is-uppercase has-text-centered title is-7">
                            ${t("boatApplication.fillingInformationHeader")}
                        </p>
                    </div>
                    <div class="column">
                        <div id="payment" class="step mb-m ${if (currentStep >= 3) "visited" else ""}"></div>
                        <p class="is-uppercase has-text-centered title is-7">
                            ${t("boatApplication.paymentHeader")}
                        </p>
                    </div>
                    <div class="column">
                        <div id="confirmation" class="step mb-m ${if (currentStep >= 4) "visited" else ""}"></div>
                        <p class="is-uppercase has-text-centered title is-7">
                            ${t("boatApplication.confirmationHeader")}
                        </p>
                    </div>
                </div>
            </div>
            """.trimIndent()
    }
}
