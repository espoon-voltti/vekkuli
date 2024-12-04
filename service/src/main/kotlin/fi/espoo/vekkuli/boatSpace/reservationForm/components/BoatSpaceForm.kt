package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationForApplicationForm
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationInput
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.citizen.SessionTimer
import fi.espoo.vekkuli.views.citizen.StepIndicator
import org.springframework.stereotype.Component

// language=HTML
@Component
class BoatSpaceForm(
    private val boatForm: BoatForm,
    private val slipHolder: SlipHolder,
    private val stepIndicator: StepIndicator,
    private val sessionTimer: SessionTimer,
    private val citizenContainer: CitizenContainer,
    private val reservationInformation: ReservationInformation,
    private val icons: Icons,
    private val markDownService: MarkDownService
) : BaseView() {
    fun render(
        reservation: ReservationForApplicationForm,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        reservationTimeInSeconds: Long,
        userType: UserType,
        municipalities: List<Municipality>,
        isNewCustomer: Boolean = true,
        title: String = "",
        formBody: String = ""
    ): String {
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
                    <div class="container">
                        <button x-on:click="modalOpen = true" class="icon-text">
                            <span class="icon">
                                <div>${icons.chevronLeft}</div>
                            </span>
                            <span >${t("boatSpaces.goBack")}</span>
                        </button>
                    </div> 
                    ${stepIndicator.render(2)}
                    ${sessionTimer.render(reservationTimeInSeconds)}
                    <form
                        id="form"
                        class="column"
                        action="/${userType.path}/venepaikka/varaus/${reservation.id}"
                        method="post"
                        novalidate>
                         $title
                        <div id="form-inputs" class="block">
                           $formBody
                               
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
                    </form>
                    
                    <script>
                        validation.init({forms: ['form']})
                        window.addEventListener('load', function() {
                            if (!document.getElementById("width").value && !document.getElementById("length").value) {
                                const type = localStorage.getItem('type');
                                if (type) {
                                    document.getElementById("boatType").value = type;
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
                                hx-delete="/${userType.path}/venepaikka/varaus/${reservation.id}"
                                hx-on-htmx-after-request="${if (userType == UserType.CITIZEN) "window.location = '/kuntalainen/venepaikat';" else "history.back()"}">
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
