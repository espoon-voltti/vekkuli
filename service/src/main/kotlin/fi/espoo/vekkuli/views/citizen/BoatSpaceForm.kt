package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.ReservationInput
import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.utils.cmToM
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties

class BoatSpaceForm {
    @Autowired
    lateinit var messageUtil: MessageUtil

    fun t(key: String): String {
        return messageUtil.getMessage(key)
    }

    fun boatSpaceForm(
        reservationId: String,
        reservation: ReservationWithDependencies, // Assuming BoatSpace is a data class with relevant fields
        boats: List<Boat>, // Assuming Boat is a data class with relevant fields
        boatTypes: List<String>,
        ownershipOptions: List<String>,
        user: SecurityProperties.User, // Assuming User is a data class with relevant fields
        input: ReservationInput, // Assuming this contains the form input data
        errors: Map<String, String>,
        isAuthenticated: Boolean,
        userName: String?
    ): String {
        // language=HTML
        val boatSpaceInformation =
            """
            <div class="block">
                <h3 id="boat-space-form-header" class="header">${t("boatApplication.boatSpaceToApply")}</h3>
                <p>${reservation.locationName}</p>
                <p>${t("boatApplication.boatSpaceSection")} ${reservation.section}</p>
                <p>${t("boatApplication.boatSpacePlace")} ${reservation.section}${reservation.placeNumber}</p>
                <p>${reservation.widthCm.cmToM()} m x ${reservation.lengthCm.cmToM()} m</p>
                <p>${t("boatSpaces.amenityOption.${reservation.amenity}")}</p>
            </div>
            <div class="block">
                <h4 class="label">${t("boatApplication.boatSpacePrice")}</h4>
                <p>${t("boatApplication.boatSpaceFee")}: ${reservation.priceWithoutAlvInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeAlv")}: ${reservation.alvPriceInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeTotal")}: ${reservation.priceInEuro} &euro;</p>
            </div>
            """.trimIndent()

        // language=HTML
        return """
                        $boatSpaceInformation
                                                                                                                                                                                                                                                                                                                                                                        ${if (boats.isNotEmpty()) {
            """
                            <div class="field">
                                <div class="radio">
                                    <input type="radio" name="boatId" value="0"
                                           hx-trigger="change"
                                           hx-get="/kuntalainen/venepaikka/varaus/$reservationId?boatId=0"
                                           hx-target="body"
                                           hx-swap="outerHTML" />
                                    <label for="boatId">${translate("boatApplication.newBoat")}</label>
                                </div>
                                ${boats.joinToString("\n") { boat ->
                """
                <div class="radio">
                    <input type="radio" name="boatId" value="${boat.id}"
                           hx-trigger="change"
                           hx-get="/kuntalainen/venepaikka/varaus/$reservationId?boatId=${boat.id}"
                           hx-target="body"
                           hx-swap="outerHTML" />
                    <label for="boatId">${boat.displayName}</label>
                </div>
                """.trimIndent()
            }}
                            </div>
            """.trimIndent()
        } else {
            ""
        }}
                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                        ${generateSelectField(
            "boatType",
            "boatApplication.boatType",
            boatTypes,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                        <div class="field is-grouped">
                                                                                                                                                                                                                                                                                                                                                                            ${generateNumberInputField(
            "width",
            "boatApplication.boatWidthInMeters",
            input.width,
            reservationId,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                            ${generateNumberInputField(
            "length",
            "boatApplication.boatLengthInMeters",
            input.length,
            reservationId,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                        </div>
                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                        <div id="warning" hx-swap-oob="true">
                                                                                                                                                                                                                                                                                                                                                                            <!-- Warning Section -->
                                                                                                                                                                                                                                                                                                                                                                        </div>

                                                                                                                                                                                                                                                                                                                                                                        <div class="field is-grouped">
                                                                                                                                                                                                                                                                                                                                                                            ${generateNumberInputField(
            "depth",
            "boatApplication.boatDepthInMeters",
            input.depth,
            reservationId,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                            ${generateNumberInputField(
            "weight",
            "boatApplication.boatWeightInKg",
            input.weight,
            reservationId,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                        </div>

                                                                                                                                                                                                                                                                                                                                                                        ${generateTextInputField(
            "boatName",
            "boatApplication.boatName",
            input.boatName,
            reservationId,
            errors
        )}

                                                                                                                                                                                                                                                                                                                                                                        <div class="field" x-data="{ noReg: ${input.noRegistrationNumber} }">
                                                                                                                                                                                                                                                                                                                                                                            <h4 class="label required">${translate(
            "boatApplication.boatIdentificationTitle"
        )}</h4>
                                                                                                                                                                                                                                                                                                                                                                            <label class="checkbox">
                                                                                                                                                                                                                                                                                                                                                                                <input type="checkbox" name="noRegistrationNumber" @click="noReg = !noReg" />
                                                                                                                                                                                                                                                                                                                                                                                <span>${translate(
            "boatApplication.noRegistrationNumber"
        )}</span>
                                                                                                                                                                                                                                                                                                                                                                            </label>
                                                                                                                                                                                                                                                                                                                                                                            <template x-if="!noReg">
                                                                                                                                                                                                                                                                                                                                                                                ${generateTextInputField(
            "boatRegistrationNumber",
            "boatApplication.registrationNumberPlaceHolder",
            input.boatRegistrationNumber,
            reservationId,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                            </template>

                                                                                                                                                                                                                                                                                                                                                                            ${generateTextInputField(
            "otherIdentification",
            "boatApplication.otherIdentification",
            input.otherIdentification,
            reservationId,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                        </div>

                                                                                                                                                                                                                                                                                                                                                                        ${generateTextAreaField(
            "extraInformation",
            "boatApplication.extraInformation",
            input.extraInformation
        )}

                                                                                                                                                                                                                                                                                                                                                                        ${generateRadioField(
            "ownerShip",
            "boatApplication.ownerShipTitle",
            ownershipOptions,
            errors
        )}

                                                                                                                                                                                                                                                                                                                                                                        <h3 class="header">${translate(
            "boatApplication.personalInformation"
        )}</h3>
                                                                                                                                                                                                                                                                                                                                                                        <div class="field">
                                                                                                                                                                                                                                                                                                                                                                            <p>${user.firstName} ${user.lastName}</p>
                                                                                                                                                                                                                                                                                                                                                                            <p>${user.nationalId}</p>
                                                                                                                                                                                                                                                                                                                                                                            <p>${user.address}</p>
                                                                                                                                                                                                                                                                                                                                                                            <p>${user.postalCode} ${user.municipality}</p>
                                                                                                                                                                                                                                                                                                                                                                        </div>

                                                                                                                                                                                                                                                                                                                                                                        ${generateTextInputField(
            "email",
            "boatApplication.email",
            input.email,
            reservationId,
            errors
        )}
                                                                                                                                                                                                                                                                                                                                                                        ${generateTextInputField(
            "phone",
            "boatApplication.phone",
            input.phone,
            reservationId,
            errors
        )}

                                                                                                                                                                                                                                                                                                                                                                        <div class="block">
                                                                                                                                                                                                                                                                                                                                                                            <div id="certify-control">
                                                                                                                                                                                                                                                                                                                                                                                <label class="checkbox">
                                                                                                                                                                                                                                                                                                                                                                                    <input type="checkbox" name="certifyInformation" data-required />
                                                                                                                                                                                                                                                                                                                                                                                    <span>${translate(
            "boatApplication.certifyInfoCheckbox"
        )}</span>
                                                                                                                                                                                                                                                                                                                                                                                </label>
                                                                                                                                                                                                                                                                                                                                                                                <div id="certify-error-container">
                                                                                                                                                                                                                                                                                                                                                                                    <span class="help is-danger">${if (errors.containsKey(
                "certifyInformation"
            )
        ) {
            errors["certifyInformation"]
        } else {
            ""
        }}</span>
                                                                                                                                                                                                                                                                                                                                                                                </div>
                                                                                                                                                                                                                                                                                                                                                                            </div>

                                                                                                                                                                                                                                                                                                                                                                            <div id="agree-control">
                                                                                                                                                                                                                                                                                                                                                                                <label class="checkbox">
                                                                                                                                                                                                                                                                                                                                                                                    <input type="checkbox" name="agreeToRules" data-required />
                                                                                                                                                                                                                                                                                                                                                                                    <span>${translate(
            "boatApplication.agreementCheckbox"
        )}</span>
                                                                                                                                                                                                                                                                                                                                                                                </label>
                                                                                                                                                                                                                                                                                                                                                                                <div id="agree-error-container">
                                                                                                                                                                                                                                                                                                                                                                                    <span class="help is-danger">${if (errors.containsKey(
                "agreeToRules"
            )
        ) {
            errors["agreeToRules"]
        } else {
            ""
        }}</span>
                                                                                                                                                                                                                                                                                                                                                                                </div>
                                                                                                                                                                                                                                                                                                                                                                            </div>
                                                                                                                                                                                                                                                                                                                                                                        </div>

                                                                                                                                                                                                                                                                                                                                                                        <div class="field block">
                                                                                                                                                                                                                                                                                                                                                                            <div class="control">
                                                                                                                                                                                                                                                                                                                                                                                <button id="cancel" class="button is-secondary" type="button" x-on:click="modalOpen = true">
                                                                                                                                                                                                                                                                                                                                                                                    ${translate(
            "boatApplication.cancelReservation"
        )}
                                                                                                                                                                                                                                                                                                                                                                                </button>
                                                                                                                                                                                                                                                                                                                                                                                <button id="submit" class="button is-primary" type="submit">
                                                                                                                                                                                                                                                                                                                                                                                    ${translate(
            "boatApplication.continueToPaymentButton"
        )}
                                                                                                                                                                                                                                                                                                                                                                                </button>
                                                                                                                                                                                                                                                                                                                                                                            </div>
                                                                                                                                                                                                                                                                                                                                                                        </div>
                                                                                                                                                                                                                                                                                                                                                                    </div>
                                                                                                                                                                                                                                                                                                                                                                </form>
                                                                                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                                                                                <!-- Comment: fragments/confirm-cancellation-modal :: confirmCancellationModal -->

                                                                                                                                                                                                                                                                                                                                                            </div>
                                                                                                                                                                                                                                                                                                                                                        </section>
                                                                                                                                                                                                                                                                                                                                                        </body>
                                                                                                                                                                                                                                                                                                                                                        </html>
            """.trimIndent()
    }
}
