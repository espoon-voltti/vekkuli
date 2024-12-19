package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationForApplicationForm
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.citizen.SessionTimer
import fi.espoo.vekkuli.views.citizen.StepIndicator
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

data class ReservationUrls(
    val submitUrl: String,
    val deleteUrl: String,
    val urlToReturnTo: String = "/kuntalainen/venepaikat"
)

// language=HTML
@Component
class BoatSpaceForm(
    private val stepIndicator: StepIndicator,
    private val sessionTimer: SessionTimer,
    private val markDownService: MarkDownService,
    private val timeProvicer: TimeProvider
) : BaseView() {
    fun getReservationTimeInSeconds(
        reservationCreated: LocalDateTime,
        currentDate: LocalDateTime
    ): Long {
        val reservationTimePassed = Duration.between(reservationCreated, currentDate).toSeconds()
        return (BoatSpaceConfig.SESSION_TIME_IN_SECONDS - reservationTimePassed)
    }

    fun render(
        reservation: ReservationForApplicationForm,
        userType: UserType,
        titleText: String = "",
        formContent: String = "",
        urls: ReservationUrls =
            ReservationUrls(
                submitUrl = "/${userType.path}/venepaikka/varaus/${reservation.id}",
                deleteUrl = "/${userType.path}/venepaikka/varaus/${reservation.id}"
            )
    ): String {
        val wholeLocationName = "${reservation.locationName} ${reservation.place}"
        // language=HTML
        val goBackButton = """ <div class="container">
                        <button data-testid='go-back' x-on:click="modalOpen = true" class="icon-text">
                            <span class="icon">
                                <div>${icons.chevronLeft}</div>
                            </span>
                            <span >${t("boatSpaces.goBack")}</span>
                        </button>
                    </div> """

        // language=HTML
        return (
            """
            <section class="section">
                <div class="container" id="container" x-data='{modalOpen: false, citizenFullName: "", citizenId:"", updateFullName(event) {
                    const selectElement = event.target;
                    if (selectElement.selectedOptions.length > 0) {
                        const selectedOption = selectElement.selectedOptions[0];
                        this.citizenFullName = selectedOption.dataset.fullname;
                        this.citizenId = selectedOption.value;
                    } else {
                        this.citizenFullName = "";
                        this.citizenId = "";
                    };
                }}'> 
                    $goBackButton
                    ${stepIndicator.render(2)}
                    ${sessionTimer.render(getReservationTimeInSeconds(reservation.created, timeProvicer.getCurrentDateTime()))}
                    <form
                        id="form"
                        class="column"
                        action="${urls.submitUrl}"
                        method="post"
                        novalidate>
                          <h1 class="title pb-l" id='boat-space-form-header'>
                            $titleText
                            $wholeLocationName
                        </h1>
                        <div id="form-inputs" class="block">
                           $formContent
                               
                            <div class="block">
                                <div id="certify-control" class="field">
                                    <label class="checkbox">
                                        <input
                                            type="checkbox"
                                            data-required
                                            id="certifyInformation"
                                            name="certifyInformation"
                                        >
                                        <span >${t("boatApplication.certifyInfoCheckbox")}</span>
                                    </label>
                                    <div id="certify-error-container">
                                        <span id="certifyInformation-error" class="help is-danger" style="display: none">
                                        ${t("validation.certifyInformation")}</span>
                                    </div>
                                </div>
                                <div id="agree-control" class="field">
                                    <label class="checkbox">
                                        <input
                                            type="checkbox"
                                            data-required
                                            id="agreeToRules"
                                            name="agreeToRules"
                                        />
                                        <span> ${markDownService.render(t("boatApplication.agreementCheckbox"))} </span>
                                    </label>
                                    <div id="agree-error-container">
                                        <span id="agreeToRules-error" class="help is-danger" style="display: none">
                                        ${t("validation.agreeToRules")}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="warning block form-validation-message" id="validation-warning" style="display: none">
                                <span class="icon">${icons.warningExclamation(false)}</span>
                                <span class="p-l">${t("boatApplication.validationWarning")}</span>
                            </div> 
                        </div >
                        <div class="field block">
                            <div class="control">
                                <button id="cancel"
                                    class="button is-secondary"
                                    type="button"
                                    x-on:click="modalOpen = true">
                                    ${t("boatApplication.cancelReservation")}
                                </button>
                                <button id="submit-button"
                                    class="button is-primary"
                                    type="submit">
                                    ${t("boatApplication.$userType.continueToPaymentButton")}
                                </button>
                            </div>
                        </div>             
                        <script>
                            validation.init({forms: ['form']})
                        </script>
                    </form>
                    
                    <script>
                        window.addEventListener('load', function() {
                            const boatSpaceType = localStorage.getItem('boatSpaceType');
                            if (boatSpaceType === "Slip" && !document.getElementById("width").value && !document.getElementById("length").value) {
                                const boatType = localStorage.getItem('boatType');
                                if (boatType) {
                                    document.getElementById("boatType").value = boatType;
                                }
                         
                                const width = localStorage.getItem('width');
                                if (width) {
                                    document.getElementById('width').value = width;
                                }
                                const length = localStorage.getItem('length');
                                if (length) {
                                    document.getElementById('length').value = length;
                                }
                            }
                        });
                            
                    </script>
                    
                    <div id="confirm-cancel-modal" class="modal" x-show="modalOpen" style="display:none;" >
                        <div class="modal-underlay" @click="modalOpen = false"></div>
                        <div class="modal-content">
                            <p class="block has-text-left">${t("boatSpaceApplication.cancelConfirmation")}</p>
                            <button id="confirm-cancel-modal-cancel"
                                class="button"
                                x-on:click="modalOpen = false"
                                type="button">
                                ${t("cancel")}
                            </button>
                            <button id="confirm-cancel-modal-confirm"
                                class="button is-primary"
                                type="button"
                                hx-delete="${urls.deleteUrl}"
                                hx-on-htmx-after-request="${if (userType == UserType.CITIZEN) "window.location = '${urls.urlToReturnTo}';" else "history.back()"}">
                                ${t("confirm")}
                            </button>
                        </div>
                    </div>
                    
                </div>
            </section>
            """.trimIndent()
        )
    }
}
