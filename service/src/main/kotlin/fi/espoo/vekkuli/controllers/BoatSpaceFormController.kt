package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.AppUser
import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.BoatSpaceConfig.doesBoatFit
import fi.espoo.vekkuli.config.Dimensions
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Routes.Companion.USERTYPE
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.mToCm
import fi.espoo.vekkuli.views.Warnings
import fi.espoo.vekkuli.views.citizen.BoatFormInput
import fi.espoo.vekkuli.views.citizen.BoatSpaceForm
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.citizen.ReservationConfirmation
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import jakarta.validation.constraints.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*
import kotlin.reflect.KClass

@Controller
class BoatSpaceFormController(
    private val employeeLayout: EmployeeLayout,
    private val boatSpaceForm: BoatSpaceForm,
    private val layout: Layout,
    private val jdbi: Jdbi,
    private val messageUtil: MessageUtil,
    private val reservationService: BoatReservationService,
    private val boatService: BoatService,
    private val citizenService: CitizenService,
    private val organizationService: OrganizationService,
    private val reservationConfirmation: ReservationConfirmation,
    private val warnings: Warnings
) {
    @RequestMapping("/$USERTYPE/venepaikka/varaus/{reservationId}")
    @ResponseBody
    fun boatSpaceFormPage(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam boatId: Int?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): ResponseEntity<String> {
        val userType = UserType.fromPath(usertype)
        val citizen =
            if (userType == UserType.EMPLOYEE) {
                null
            } else {
                getCitizen(request, citizenService)
            }

        val reservation =
            if (userType == UserType.EMPLOYEE) {
                reservationService.getReservationWithoutCitizen(reservationId)
            } else {
                reservationService.getReservationWithReserver(reservationId)
            }

        if (reservation == null) {
            val headers = org.springframework.http.HttpHeaders()
            headers.location = URI(getServiceUrl("/${userType.path}/venepaikat"))
            return ResponseEntity(headers, HttpStatus.FOUND)
        }

        if (userType == UserType.CITIZEN && (citizen == null || reservation.reserverId != citizen.id)) {
            throw UnauthorizedException()
        }

        var input = ReservationInput.initializeInput(boatType, width, length, citizen)
        val usedBoatId = boatId ?: reservation.boatId // use boat id from reservation if it exists
        if (usedBoatId != null && usedBoatId != 0) {
            val boat = boatService.getBoat(usedBoatId)

            if (boat != null) {
                input =
                    input.copy(
                        boatId = boat.id,
                        depth = boat.depthCm.cmToM(),
                        boatName = boat.name,
                        weight = boat.weightKg,
                        width = boat.widthCm.cmToM(),
                        length = boat.lengthCm.cmToM(),
                        otherIdentification = boat.otherIdentification,
                        extraInformation = boat.extraInformation,
                        ownership = boat.ownership,
                        boatType = boat.type,
                        boatRegistrationNumber = boat.registrationCode,
                        noRegistrationNumber = boat.registrationCode.isNullOrEmpty()
                    )
            }
        } else {
            input = input.copy(boatId = 0)
        }
        val organizations: List<Organization> =
            if (citizen != null) {
                organizationService.getCitizenOrganizations(citizen.id)
            } else {
                emptyList()
            }

        return if (userType == UserType.EMPLOYEE) {
            return ResponseEntity.ok(renderBoatSpaceReservationApplication(reservation, null, organizations, input, request, userType))
        } else {
            ResponseEntity.ok(renderBoatSpaceReservationApplication(reservation, citizen, organizations, input, request, userType))
        }
    }

    @DeleteMapping("/$USERTYPE/venepaikka/varaus/{reservationId}")
    fun removeBoatSpaceReservation(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val citizen = getCitizen(request, citizenService) ?: return ResponseEntity.noContent().build()
        reservationService.removeBoatSpaceReservation(reservationId, citizen.id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-type-warning")
    fun boatTypeWarning(
        @PathVariable reservationId: Int,
        @RequestParam boatType: BoatType,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationWithoutCitizen(reservationId)
        val excludedBoatTypes = reservation?.excludedBoatTypes
        if (excludedBoatTypes != null && excludedBoatTypes.contains(boatType)) {
            return ResponseEntity.ok(warnings.boatTypeWarning())
        }
        return ResponseEntity.ok("")
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-size-warning")
    fun boatSizeWarning(
        @PathVariable reservationId: Int,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val reservation = reservationService.getReservationWithoutCitizen(reservationId)

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
        val boatDimensions = Dimensions(widthInCm ?: 0, lengthInCm ?: 0)
        val spaceDimensions = Dimensions(spaceWidthInCm, spaceLengthInCm)
        return !doesBoatFit(spaceDimensions, boatSpaceAmenity, boatDimensions)
    }

    @GetMapping("/venepaikka/varaus/{reservationId}/boat-weight-warning")
    fun boatWeight(
        @PathVariable reservationId: Int,
        @RequestParam weight: Int,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        if (weight > BoatSpaceConfig.BOAT_WEIGHT_THRESHOLD_KG) {
            return ResponseEntity.ok(warnings.boatWeightWarning())
        }
        return ResponseEntity.ok("")
    }

    @PostMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/validate")
    @ResponseBody
    fun validateForm(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @Valid @ModelAttribute("input") input: ReservationInput,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        model: Model,
    ): String {
        val userType = UserType.fromPath(usertype)
        if (userType == UserType.EMPLOYEE) {
            val employee = getEmployee(request)
            if (employee == null) {
                return redirectUrl("/")
            }

            val reservation = reservationService.getReservationWithoutCitizen(reservationId)
            if (reservation == null) {
                return redirectUrl("/")
            }

            return renderBoatSpaceReservationApplication(reservation, null, emptyList(), input, request, userType)
        }
        val citizen = getCitizen(request, citizenService)
        val reservation = reservationService.getReservationWithReserver(reservationId)
        if (reservation == null) return redirectUrl("/")

        val organizations = if (citizen != null) organizationService.getCitizenOrganizations(citizen.id) else emptyList()

        return renderBoatSpaceReservationApplication(reservation, citizen, organizations, input, request, userType)
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/kuntalainen/hae")
    @ResponseBody
    fun searchCitizens(
        request: HttpServletRequest,
        @RequestParam nameParameter: String,
        @PathVariable usertype: String
    ): String {
        citizenService.getCitizens(nameParameter).let {
            return boatSpaceForm.citizensSearchForm(it)
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
            boatSpaceForm.citizenDetails(citizen, citizenService.getMunicipalities())
        } else {
            ""
        }
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/vahvistus")
    @ResponseBody
    fun confirmBoatSpaceReservation(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        model: Model,
        request: HttpServletRequest,
    ): String {
        val citizen = getCitizen(request, citizenService) ?: return redirectUrl("/")
        val reservation = reservationService.getBoatSpaceReservation(reservationId)
        if (reservation == null) return redirectUrl("/")
        model.addAttribute("reservation", reservation)

        return layout.render(
            true,
            citizen.fullName,
            request.requestURI,
            reservationConfirmation.render(reservation)
        )
    }

    @GetMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/boat-form")
    @ResponseBody
    fun boatForm(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @RequestParam boatId: Int?,
        @RequestParam organizationId: UUID?,
        request: HttpServletRequest,
    ): String {
        val userType = UserType.fromPath(usertype)
        val citizen = getCitizen(request, citizenService) ?: return redirectUrl("/")
        val boats =
            boatService
                .getBoatsForReserver(organizationId ?: citizen.id)
                .map { boat ->
                    boat.updateBoatDisplayName(messageUtil)
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
                    boatType = BoatType.OutboardMotor,
                    width = null,
                    length = null,
                    depth = null,
                    weight = null,
                    boatRegistrationNumber = "",
                    otherIdentification = "",
                    extraInformation = "",
                    ownership = OwnershipStatus.Owner,
                    noRegistrationNumber = false,
                )
            }

        return boatSpaceForm.boatForm(userType, citizen, boats, reservationId, input)
    }

    @PostMapping("/$USERTYPE/venepaikka/varaus/{reservationId}")
    fun reserveBoatSpace(
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

        val citizenId =
            if (userType == UserType.EMPLOYEE) {
                val employee = getEmployee(request)

                if (employee == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("")
                }
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
                        )?.id
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
                        ).id
                }
            } else {
                getCitizen(request, citizenService)?.id
            }

        if (citizenId == null) {
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "/")
                .build()
        }

        if (bindingResult.hasErrors()) {
            val reservation = reservationService.getReservationWithReserver(reservationId)
            if (reservation == null) {
                return redirectUrl("/")
            }
            return badRequest("Invalid input")
        }

        var reserverId: UUID = citizenId

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
                organizationService.addCitizenToOrganization(newOrg.id, citizenId)
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

        val reservationStatus = if (userType == UserType.EMPLOYEE) ReservationStatus.Invoiced else ReservationStatus.Payment

        reservationService.reserveBoatSpace(
            reserverId,
            ReserveBoatSpaceInput(
                reservationId = reservationId,
                boatId = input.boatId,
                boatType = input.boatType!!,
                width = input.width ?: 0.0,
                length = input.length ?: 0.0,
                depth = input.depth ?: 0.0,
                weight = input.weight,
                boatRegistrationNumber = input.boatRegistrationNumber ?: "",
                boatName = input.boatName ?: "",
                otherIdentification = input.otherIdentification ?: "",
                extraInformation = input.extraInformation ?: "",
                ownerShip = input.ownership!!,
                email = input.email!!,
                phone = input.phone!!,
            ),
            reservationStatus
        )

        if (userType == UserType.EMPLOYEE) {
            return redirectUrl("/virkailija/venepaikat/varaukset")
        }

        // redirect to payments page with reservation id and slip type
        return redirectUrl("/${userType.path}/maksut/maksa?id=$reservationId&type=${PaymentType.BoatSpaceReservation}")
    }

    @PostMapping("/validate/ssn")
    fun validateSSN(
        @RequestBody request: Map<String, String>
    ): ResponseEntity<Map<String, Any>> {
        val ssn = request["value"]
        val isValid = ssn?.let { citizenService.getCitizenBySsn(ssn) == null } ?: false

        return if (isValid) {
            ResponseEntity.ok(mapOf("isValid" to true))
        } else {
            ResponseEntity.ok(mapOf("isValid" to false, "message" to messageUtil.getMessage("validation.uniqueSsn")))
        }
    }

    @PostMapping("/validate/businessid")
    fun businessIdWarning(
        @RequestBody request: Map<String, String>
    ): ResponseEntity<Map<String, Any>> {
        val value = request["value"]
        val organizations = value?.let { organizationService.getOrganizationByBusinessId(value) }
        val showBusinessIdWarning = !organizations.isNullOrEmpty()
        if (showBusinessIdWarning) {
            val warning = warnings.businessId(organizations ?: listOf(), value ?: "")
            return ResponseEntity.ok(
                mapOf(
                    "isValid" to false,
                    "message" to warning
                )
            )
        }
        return ResponseEntity.ok(mapOf("isValid" to true, "message" to ""))
    }

    // initial reservation in info state
    @GetMapping("/$USERTYPE/venepaikka/varaa/{spaceId}")
    fun reserveBoatSpace(
        @PathVariable usertype: String,
        @PathVariable spaceId: Int,
        @RequestParam boatType: BoatType?,
        @RequestParam width: Double?,
        @RequestParam length: Double?,
        request: HttpServletRequest,
        model: Model,
    ): ResponseEntity<String> {
        val userType = UserType.fromPath(usertype)
        val isEmployee = userType == UserType.EMPLOYEE
        val citizenId = getCitizen(request, citizenService)?.id
        val userId =
            if (isEmployee) {
                getEmployee(request)?.id
            } else {
                citizenId
            }

        if (userId == null) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }

        val existingReservation =
            if (isEmployee) {
                reservationService.getUnfinishedReservationForEmployee(userId)
            } else {
                reservationService.getUnfinishedReservationForCitizen(userId)
            }

        val reservationId =
            if (existingReservation != null) {
                existingReservation.id
            } else {
                val today = LocalDate.now()
                val endOfYear = LocalDate.of(today.getYear(), Month.DECEMBER, 31)
                if (isEmployee) {
                    reservationService.insertBoatSpaceReservationAsEmployee(userId, spaceId, today, endOfYear).id
                } else {
                    reservationService
                        .insertBoatSpaceReservation(
                            userId,
                            citizenId,
                            spaceId,
                            today,
                            endOfYear,
                        ).id
                }
            }

        val queryParams = mutableListOf<String>()
        boatType?.let { queryParams.add("boatType=${it.name}") }
        width?.let { queryParams.add("width=$it") }
        length?.let { queryParams.add("length=$it") }

        val queryString = queryParams.joinToString("&")

        val headers = org.springframework.http.HttpHeaders()
        headers.location = URI(getServiceUrl("/${userType.path}/venepaikka/varaus/$reservationId?$queryString"))
        return ResponseEntity(headers, HttpStatus.FOUND)
    }

    fun renderBoatSpaceReservationApplication(
        reservation: ReservationWithDependencies,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        request: HttpServletRequest,
        userType: UserType
    ): String {
        val boats =
            if (citizen == null) {
                emptyList()
            } else {
                boatService
                    .getBoatsForReserver(citizen.id)
                    .map { boat ->
                        boat.updateBoatDisplayName(messageUtil)
                    }
            }

        val municipalities = citizenService.getMunicipalities()

        return if (userType == UserType.EMPLOYEE) {
            employeeLayout.render(
                true,
                request.requestURI,
                (
                    boatSpaceForm.boatSpaceForm(
                        reservation,
                        boats,
                        citizen,
                        organizations,
                        input,
                        getReservationTimeInSeconds(reservation.created),
                        userType,
                        municipalities
                    )
                )
            )
        } else {
            layout.render(
                true,
                citizen?.fullName,
                request.requestURI,
                boatSpaceForm.boatSpaceForm(
                    reservation,
                    boats,
                    citizen,
                    organizations,
                    input,
                    getReservationTimeInSeconds(reservation.created),
                    userType,
                    municipalities
                )
            )
        }
    }

    fun getEmployee(request: HttpServletRequest): AppUser? {
        val authenticatedUser = request.getAuthenticatedUser() ?: return null

        return authenticatedUser.let {
            jdbi.inTransactionUnchecked { tx ->
                tx.getAppUser(authenticatedUser.id)
            }
        }
    }

    @RequestMapping("/$USERTYPE/venepaikka/varaus/{reservationId}/varaaja")
    @ResponseBody
    fun slipHolderForm(
        @PathVariable usertype: String,
        @PathVariable reservationId: Int,
        @RequestParam isOrganization: Boolean?,
        @RequestParam organizationId: UUID?,
        @RequestParam citizenId: UUID?,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val userType = UserType.fromPath(usertype)
        val isEmployee = userType == UserType.EMPLOYEE
        val usedCitizenId: UUID? =
            if (isEmployee) {
                citizenId
            } else {
                (getCitizen(request, citizenService)?.id ?: return ResponseEntity.badRequest().build())
            }
        if (usedCitizenId == null) return ResponseEntity.badRequest().build()
        val organizations = organizationService.getCitizenOrganizations(usedCitizenId)
        val municipalities = citizenService.getMunicipalities()
        return ResponseEntity.ok(
            boatSpaceForm.slipHolder(organizations, isOrganization ?: false, organizationId, userType, reservationId, municipalities)
        )
    }
}

fun getReservationTimeInSeconds(reservationCreated: LocalDateTime): Long {
    val reservationTimePassed = Duration.between(reservationCreated, LocalDateTime.now()).toSeconds()
    return (BoatSpaceConfig.SESSION_TIME_IN_SECONDS - reservationTimePassed)
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class UnauthorizedException : RuntimeException()

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [BoatRegistrationValidator::class])
annotation class ValidBoatRegistration(
    val message: String = "{validation.required}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class BoatRegistrationValidator : ConstraintValidator<ValidBoatRegistration, ReservationInput> {
    override fun isValid(
        value: ReservationInput,
        context: ConstraintValidatorContext,
    ): Boolean {
        var isValid = true

        // If registration number is selected, it must be filled
        if (value.noRegistrationNumber != true && value.boatRegistrationNumber.isNullOrBlank()) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("boatRegistrationNumber")
                .addConstraintViolation()
            isValid = false
        }

        // If no registration number is selected, other identification field must be filled
        if (value.noRegistrationNumber == true && value.otherIdentification.isNullOrBlank()) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("otherIdentification")
                .addConstraintViolation()
            isValid = false
        }
        return isValid
    }
}

@ValidBoatRegistration
data class ReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val reservationId: Int?,
    val boatId: Int?,
    @field:NotNull(message = "{validation.required}")
    val boatType: BoatType?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val width: Double?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val length: Double?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val depth: Double?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    val weight: Int?,
    val noRegistrationNumber: Boolean?,
    val boatRegistrationNumber: String?,
    val boatName: String?,
    val otherIdentification: String?,
    val extraInformation: String?,
    @field:NotNull(message = "{validation.required}")
    val ownership: OwnershipStatus?,
    val firstName: String?,
    val lastName: String?,
    val ssn: String?,
    val address: String?,
    val postalCode: String?,
    val postalOffice: String?,
    val municipalityCode: Int?,
    val citizenId: UUID?,
    @field:NotBlank(message = "{validation.required}")
    @field:Email(message = "{validation.email}")
    val email: String?,
    @field:NotBlank(message = "{validation.required}")
    val phone: String?,
    @field:AssertTrue(message = "{validation.certifyInformation}")
    val certifyInformation: Boolean?,
    @field:AssertTrue(message = "{validation.agreeToRules}")
    val agreeToRules: Boolean?,
    val isOrganization: Boolean?,
    val organizationId: UUID? = null,
    val orgName: String? = null,
    val orgBusinessId: String? = null,
    val orgMunicipalityCode: String? = null,
    val orgPhone: String? = null,
    val orgEmail: String? = null,
    val orgAddress: String? = null,
    val orgPostalCode: String? = null,
    val orgCity: String? = null,
) {
    companion object {
        fun initializeInput(
            boatType: BoatType?,
            width: Double?,
            length: Double?,
            citizen: CitizenWithDetails?
        ): ReservationInput =
            ReservationInput(
                reservationId = null,
                boatId = null,
                boatType = boatType,
                width = width,
                length = length,
                depth = null,
                weight = null,
                noRegistrationNumber = false,
                boatRegistrationNumber = null,
                boatName = null,
                otherIdentification = null,
                extraInformation = null,
                ownership = OwnershipStatus.Owner,
                email = citizen?.email,
                phone = citizen?.phone,
                agreeToRules = false,
                certifyInformation = false,
                firstName = citizen?.firstName,
                lastName = citizen?.lastName,
                ssn = citizen?.nationalId,
                address = citizen?.streetAddress,
                postalCode = citizen?.postalCode,
                municipalityCode = citizen?.municipalityCode,
                citizenId = citizen?.id,
                postalOffice = null,
                organizationId = null,
                isOrganization = null,
            )
    }
}
