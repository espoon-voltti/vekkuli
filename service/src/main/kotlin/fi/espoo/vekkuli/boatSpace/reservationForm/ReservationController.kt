package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.*
import fi.espoo.vekkuli.config.BoatSpaceConfig.doesBoatFit
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.badRequest
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.getLastDayOfYear
import fi.espoo.vekkuli.utils.mToCm
import fi.espoo.vekkuli.views.Warnings
import fi.espoo.vekkuli.views.citizen.BoatFormInput
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import jakarta.validation.constraints.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.net.URI
import java.util.*

@Controller
class ReservationController(
    private val reservationService: ReservationService,
    private val reservationFormView: ReservationFormView,
    private val employeeLayout: EmployeeLayout,
    private val citizenService: CitizenService,
    private val boatService: BoatService,
    private val messageUtil: MessageUtil,
    private val organizationService: OrganizationService,
    private val warnings: Warnings,
    private val timeProvider: TimeProvider,
    private val permissionService: PermissionService,
    private val boatReservationService: BoatReservationService,
) {
    @RequestMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    @ResponseBody
    fun boatSpaceApplicationFormForCitizen(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @ModelAttribute formInput: ReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()

        try {
            val page = reservationService.getBoatSpaceFormForCitizen(citizenId, reservationId, formInput)
            return ResponseEntity.ok(page)
        } catch (e: BadRequest) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/kuntalainen/venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        } catch (e: UnauthorizedException) {
            return badRequest("Unauthorized")
        }
    }

    @RequestMapping("/virkailija/venepaikka/varaus/{reservationId}")
    @ResponseBody
    fun boatSpaceApplicationFormForEmployee(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @ModelAttribute formInput: ReservationInput,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        try {
            val page = reservationService.getBoatSpaceFormForEmployee(reservationId, formInput, request.requestURI)
            return ResponseEntity.ok(page)
        } catch (e: BadRequest) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/virkailija/venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        } catch (e: UnauthorizedException) {
            return badRequest("Unauthorized")
        } catch (e: Exception) {
            // TODO: should we respond with error page or redirect to some other page?
            val errorPage = reservationFormView.errorPage("error", 1)
            return ResponseEntity.ok(employeeLayout.render(true, request.requestURI, errorPage))
        }
    }

    @DeleteMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservationAsCitizen(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val citizenId = request.ensureCitizenId()
        reservationService.removeBoatSpaceReservation(reservationId, citizenId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/virkailija/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservationAsEmployee(
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val user = request.getAuthenticatedUser() ?: throw Unauthorized()
        reservationService.removeBoatSpaceReservation(reservationId, user.id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/kuntalainen/hae")
    @ResponseBody
    fun searchCitizens(
        request: HttpServletRequest,
        @RequestParam nameParameter: String,
        @PathVariable usertype: String,
        @PathVariable reservationId: Int
    ): String {
        citizenService.getCitizens(nameParameter).let {
            return reservationFormView.citizensSearchForm(it, reservationId)
        }
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/kuntalainen")
    @ResponseBody
    fun searchCitizen(
        @RequestParam citizenIdOption: UUID,
        @PathVariable usertype: String,
    ): String {
        val citizen = citizenService.getCitizen(citizenIdOption)

        return if (citizen != null) {
            reservationFormView.citizenDetails(citizen, citizenService.getMunicipalities())
        } else {
            ""
        }
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/boat-form")
    @ResponseBody
    fun boatForm(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @RequestParam boatId: Int?,
        @RequestParam isOrganization: Boolean?,
        @RequestParam organizationId: UUID?,
        @RequestParam citizenId: UUID?,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        @RequestParam type: BoatType?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val userType = UserType.fromPath(usertype)
        val isEmployee = userType == UserType.EMPLOYEE
        val citizen =
            if (isEmployee) {
                if (citizenId == null) return ResponseEntity.badRequest().build()
                citizenService.getCitizen(citizenId)
            } else {
                getCitizen(request, citizenService)
            }
        if (citizen == null) return ResponseEntity.badRequest().build()
        val reserverId = if (isOrganization == true) organizationId else citizen.id
        val boats =
            if (reserverId != null) {
                boatService
                    .getBoatsForReserver(reserverId)
                    .map { boat ->
                        boat.updateBoatDisplayName(messageUtil)
                    }
            } else {
                emptyList()
            }

        val boat = if (boatId != null) boats.find { it.id == boatId } else null
        val input =
            if (boat !=
                null
            ) {
                BoatFormInput(
                    id = boat.id,
                    boatName = boat.name ?: "",
                    boatType = boat.type,
                    width = boat.widthCm.cmToM(),
                    length = boat.lengthCm.cmToM(),
                    depth = boat.depthCm.cmToM(),
                    weight = boat.weightKg,
                    boatRegistrationNumber = boat.registrationCode ?: "",
                    otherIdentification = boat.otherIdentification ?: "",
                    extraInformation = boat.extraInformation ?: "",
                    ownership = boat.ownership,
                    noRegistrationNumber = boat.registrationCode.isNullOrBlank(),
                )
            } else {
                BoatFormInput(
                    id = boatId ?: 0,
                    boatName = "",
                    boatType = type ?: BoatType.OutboardMotor,
                    width = width,
                    length = length,
                    depth = null,
                    weight = null,
                    boatRegistrationNumber = "",
                    otherIdentification = "",
                    extraInformation = "",
                    ownership = OwnershipStatus.Owner,
                    noRegistrationNumber = false,
                )
            }

        return ResponseEntity.ok(reservationFormView.boatForm(userType, citizen, boats, reservationId, input))
    }

    @PostMapping("/kuntalainen/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpaceForCitizen(
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val citizenId = request.ensureCitizenId()

        if (bindingResult.hasErrors()) {
            return badRequest("Invalid input")
        }

        reservationService.createOrUpdateReserverAndReservation(reservationId, citizenId, input)
        // redirect to payments page with reservation id and slip type
        return redirectUrl("/${UserType.CITIZEN.path}/maksut/maksa?id=$reservationId&type=${PaymentType.BoatSpaceReservation}")
    }

    @PostMapping("/virkailija/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpaceForEmployee(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val userType = UserType.fromPath(usertype)

        fun badRequest(body: String): ResponseEntity<String> = ResponseEntity.badRequest().body(body)

        fun redirectUrl(url: String): ResponseEntity<String> =
            ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", url)
                .body("")

        val isEmployee = userType == UserType.EMPLOYEE
        val citizen =
            if (isEmployee) {
                request.ensureEmployeeId()

                if (input.citizenId != null) {
                    citizenService
                        .updateCitizen(
                            UpdateCitizenParams(
                                id = input.citizenId,
                                email = input.email,
                                phone = input.phone,
                                streetAddress = input.address,
                                streetAddressSv = input.address,
                                postalCode = input.postalCode,
                                postOffice = input.postalOffice,
                                postOfficeSv = input.postalOffice
                            )
                        )
                } else {
                    citizenService
                        .insertCitizen(
                            phone = input.phone ?: "",
                            email = input.email ?: "",
                            nationalId = input.ssn ?: "",
                            firstName = input.firstName ?: "",
                            lastName = input.lastName ?: "",
                            address = input.address ?: "",
                            postalCode = input.postalCode ?: "",
                            municipalityCode = input.municipalityCode ?: 1,
                            false,
                        )
                }
            } else {
                getCitizen(request, citizenService)
            }

        if (citizen == null) {
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "/")
                .build()
        }

        if (bindingResult.hasErrors()) {
            val reservation = reservationService.getReservationForApplicationForm(reservationId)
            if (reservation == null) {
                return redirectUrl("/")
            }
            return badRequest("Invalid input")
        }

        var reserverId: UUID = citizen.id

        if (input.isOrganization == true) {
            if (input.organizationId == null) {
                // add new organization
                val newOrg =
                    organizationService.insertOrganization(
                        businessId = input.orgBusinessId ?: "",
                        name = input.orgName ?: "",
                        phone = input.orgPhone ?: "",
                        email = input.orgEmail ?: "",
                        streetAddress = input.orgAddress ?: "",
                        streetAddressSv = input.orgAddress ?: "",
                        postalCode = input.orgPostalCode ?: "",
                        postOffice = input.orgCity ?: "",
                        postOfficeSv = input.orgCity ?: "",
                        municipalityCode = (input.orgMunicipalityCode ?: "1").toInt()
                    )
                // add person to organization
                organizationService.addCitizenToOrganization(newOrg.id, citizen.id)
                reserverId = newOrg.id
            } else {
                // update organization
                organizationService.updateOrganization(
                    UpdateOrganizationParams(
                        id = input.organizationId,
                        businessId = input.orgBusinessId,
                        name = input.orgName,
                        phone = input.orgPhone,
                        email = input.orgEmail,
                        streetAddress = input.orgAddress,
                        streetAddressSv = input.orgAddress,
                        postalCode = input.orgPostalCode,
                        postOffice = input.orgCity,
                        postOfficeSv = input.orgCity,
                        municipalityCode = input.orgMunicipalityCode.let { it?.toInt() }
                    )
                )
                reserverId = input.organizationId
            }
        }

        val reservation = reservationService.getReservationForApplicationForm(reservationId)
        if (reservation == null) {
            return badRequest("Reservation not found")
        }

        if (reservation.status == ReservationStatus.Renewal) {
            boatReservationService.reserveBoatSpace(
                reserverId,
                ReserveBoatSpaceInput(
                    reservationId = reservationId,
                    boatId = input.boatId,
                    boatType = input.boatType!!,
                    width = input.width ?: BigDecimal.ZERO,
                    length = input.length ?: BigDecimal.ZERO,
                    depth = input.depth ?: BigDecimal.ZERO,
                    weight = input.weight,
                    boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                    boatName = input.boatName ?: "",
                    otherIdentification = input.otherIdentification ?: "",
                    extraInformation = input.extraInformation ?: "",
                    ownerShip = input.ownership!!,
                    email = input.email!!,
                    phone = input.phone!!,
                ),
                ReservationStatus.Payment,
                reservation.validity ?: ReservationValidity.FixedTerm,
                reservation.startDate,
                reservation.endDate
            )
        } else {
            val reserveSlipResult = permissionService.canReserveANewSlip(reserverId)

            val data =
                if (reserveSlipResult is ReservationResult.Success) {
                    reserveSlipResult.data
                } else {
                    val now = timeProvider.getCurrentDate()
                    ReservationResultSuccess(now, getLastDayOfYear(now.year), ReservationValidity.FixedTerm,)
                }

            if (isEmployee || reserveSlipResult.success) {
                boatReservationService.reserveBoatSpace(
                    reserverId,
                    ReserveBoatSpaceInput(
                        reservationId = reservationId,
                        boatId = input.boatId,
                        boatType = input.boatType!!,
                        width = input.width ?: BigDecimal.ZERO,
                        length = input.length ?: BigDecimal.ZERO,
                        depth = input.depth ?: BigDecimal.ZERO,
                        weight = input.weight,
                        boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                        boatName = input.boatName ?: "",
                        otherIdentification = input.otherIdentification ?: "",
                        extraInformation = input.extraInformation ?: "",
                        ownerShip = input.ownership!!,
                        email = input.email!!,
                        phone = input.phone!!,
                    ),
                    ReservationStatus.Payment,
                    data.reservationValidity,
                    data.startDate,
                    data.endDate
                )
            }
        }

        if (isEmployee) {
            return redirectUrl("/virkailija/venepaikka/varaus/$reservationId/lasku")
        }
        // redirect to payments page with reservation id and slip type
        return redirectUrl("/${userType.path}/maksut/maksa?id=$reservationId&type=${PaymentType.BoatSpaceReservation}")
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-type-warning")
    fun boatTypeWarning(
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationForApplicationForm(reservationId)
        val excludedBoatTypes = reservation?.excludedBoatTypes
        if (excludedBoatTypes != null && excludedBoatTypes.contains(boatType)) {
            return ResponseEntity.ok(warnings.boatTypeWarning())
        }
        return ResponseEntity.ok("")
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-size-warning")
    fun boatSizeWarning(
        @PathVariable reservationId: Int,
        @RequestParam width: BigDecimal?,
        @RequestParam length: BigDecimal?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationForApplicationForm(reservationId)

        if (reservation == null) {
            return ResponseEntity.badRequest().build()
        }

        val showBoatSizeWarning =
            showBoatSizeWarning(
                width?.mToCm(),
                length?.mToCm(),
                reservation.amenity,
                reservation.widthCm,
                reservation.lengthCm
            )
        if (showBoatSizeWarning) {
            return ResponseEntity.ok(warnings.boatSizeWarning())
        }
        return ResponseEntity.ok("")
    }

    private fun showBoatSizeWarning(
        widthInCm: Int?,
        lengthInCm: Int?,
        boatSpaceAmenity: BoatSpaceAmenity,
        spaceWidthInCm: Int,
        spaceLengthInCm: Int,
    ): Boolean {
        val boatDimensions = Dimensions(widthInCm, lengthInCm)
        val spaceDimensions = Dimensions(spaceWidthInCm, spaceLengthInCm)
        return !doesBoatFit(spaceDimensions, boatSpaceAmenity, boatDimensions)
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-weight-warning")
    fun boatWeight(
        @PathVariable reservationId: Int,
        @RequestParam weight: Int?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        if (weight != null && weight > BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG) {
            return ResponseEntity.ok(warnings.boatWeightWarning())
        }
        return ResponseEntity.ok("")
    }
}

@ValidBoatRegistration
data class RenewalReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val originalReservationId: Int?,
    val boatId: Int?,
    @field:NotNull(message = "{validation.required}")
    val boatType: BoatType?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val width: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val length: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val depth: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val weight: Int?,
    override val noRegistrationNumber: Boolean?,
    override val boatRegistrationNumber: String?,
    val boatName: String?,
    override val otherIdentification: String?,
    val extraInformation: String?,
    @field:NotNull(message = "{validation.required}")
    val ownership: OwnershipStatus?,
    @field:NotBlank(message = "{validation.required}")
    @field:Email(message = "{validation.email}")
    val email: String?,
    @field:NotBlank(message = "{validation.required}")
    val phone: String?,
    @field:AssertTrue(message = "{validation.certifyInformation}")
    val certifyInformation: Boolean?,
    @field:AssertTrue(message = "{validation.agreeToRules}")
    val agreeToRules: Boolean?,
    val orgPhone: String? = null,
    val orgEmail: String? = null,
) : BoatRegistrationInput
