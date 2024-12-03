package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.boatSpace.admin.Layout
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.UpdateCitizenParams
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.getLastDayOfYear
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.validation.constraints.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@Service
class ReservationService(
    private val organizationService: OrganizationService,
    private val boatReservationService: BoatReservationService,
    private val citizenService: CitizenService,
    private val reservationFormView: ReservationFormView,
    private val boatService: BoatService,
    private val messageUtil: MessageUtil,
    private val timeProvider: TimeProvider,
    private val employeeLayout: EmployeeLayout,
    private val citizenLayout: Layout,
    private val permissionService: PermissionService,
    private val reservationRepository: ReservationRepository,
) {
    fun createOrUpdateReserverAndReservationForCitizen(
        reservationId: Int,
        citizenId: UUID,
        input: ReservationInput
    ) {
        var reserverId: UUID = citizenId
        if (input.isOrganization == true) {
            reserverId = addOrUpdateOrganization(citizenId, input)
        }
        reserveSpaceByCitizen(reservationId, reserverId, input)
    }

    fun createOrUpdateCitizen(input: ReservationInput): CitizenWithDetails? {
        if (input.citizenId != null) {
            return citizenService
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
        }
        return citizenService
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

    fun createOrUpdateReserverAndReservationForEmployee(
        reservationId: Int,
        citizenId: UUID,
        input: ReservationInput
    ) {
        var reserverId: UUID = citizenId
        if (input.isOrganization == true) {
            reserverId = addOrUpdateOrganization(reserverId, input)
        }
        reserveSpaceForEmployee(reservationId, reserverId, input)
    }

    fun addOrUpdateOrganization(
        reserverId: UUID,
        input: ReservationInput,
    ): UUID {
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
            organizationService.addCitizenToOrganization(newOrg.id, reserverId)
            return newOrg.id
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
            return input.organizationId
        }
    }

    fun reserveSpaceForEmployee(
        reservationId: Int,
        reserverId: UUID,
        input: ReservationInput,
    ) {
        val reservation = getReservationForApplicationForm(reservationId)
        if (reservation == null) {
            throw BadRequest("Reservation not found")
        }

        val reserveSlipResult = permissionService.canReserveANewSlip(reserverId)
        val data =
            if (reserveSlipResult is ReservationResult.Success) {
                reserveSlipResult.data
            } else {
                val now = timeProvider.getCurrentDate()
                // TODO: get validity from input parameter for employee
                ReservationResultSuccess(now, getLastDayOfYear(now.year), ReservationValidity.FixedTerm)
            }
        reserveBoatSpace(reserverId, reservationId, input, data)
    }

    fun reserveSpaceByCitizen(
        reservationId: Int,
        reserverId: UUID,
        input: ReservationInput,
    ) {
        val reserveSlipResult = permissionService.canReserveANewSlip(reserverId)

        if (!reserveSlipResult.success || reserveSlipResult !is ReservationResult.Success) {
            throw Forbidden("Reservation not allowed")
        }
        reserveBoatSpace(reserverId, reservationId, input, reserveSlipResult.data)
    }

    private fun reserveBoatSpace(
        reserverId: UUID,
        reservationId: Int,
        input: ReservationInput,
        reserveSlipResult: ReservationResultSuccess,
    ) {
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
            reserveSlipResult.reservationValidity,
            reserveSlipResult.startDate,
            reserveSlipResult.endDate
        )
    }

    fun getReservationForApplicationForm(reservationId: Int) = reservationRepository.getReservationForApplicationForm(reservationId)

    fun getBoatSpaceFormForCitizen(
        citizenId: UUID,
        reservationId: Int,
        formInput: ReservationInput
    ): String {
        val reservation =
            reservationRepository.getReservationForApplicationForm(reservationId)
                ?: throw BadRequest("Reservation not found")

        if (reservation.reserverId != citizenId) {
            throw UnauthorizedException()
        }

        val citizen = citizenService.getCitizen(citizenId)

        return (
            citizenLayout.render(
                createBodyContent(formInput, citizen, reservation, UserType.CITIZEN)
            )
        )
    }

    fun getBoatSpaceFormForEmployee(
        reservationId: Int,
        formInput: ReservationInput,
        requestURI: String,
    ): String {
        val citizen =
            if (formInput.citizenSelection != "newCitizen" || formInput.citizenId == null) {
                formInput.citizenId?.let { citizenService.getCitizen(formInput.citizenId) }
            } else {
                citizenService.getCitizen(formInput.citizenId)
            }

        val reservation =
            reservationRepository.getReservationForApplicationForm(reservationId)
                ?: throw BadRequest("Reservation not found")

        return (
            employeeLayout.render(
                true,
                requestURI,
                createBodyContent(formInput, citizen, reservation, UserType.EMPLOYEE)
            )
        )
    }

    fun removeBoatSpaceReservation(
        reservationId: Int,
        citizenId: UUID
    ) {
        if (!permissionService.canDeleteBoatSpaceReservation(citizenId, reservationId)) {
            throw Unauthorized()
        }
        boatReservationService.removeBoatSpaceReservation(reservationId, citizenId)
    }

    private fun createBodyContent(
        formInput: ReservationInput,
        citizen: CitizenWithDetails?,
        reservation: ReservationForApplicationForm,
        userType: UserType
    ): String {
        var input = formInput.copy(email = citizen?.email, phone = citizen?.phone)
        val usedBoatId = formInput.boatId ?: reservation.boatId // use boat id from reservation if it exists
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

        val organizations = citizen?.let { organizationService.getCitizenOrganizations(citizen.id) } ?: emptyList()

        val boatReserver = if (input.isOrganization == true) input.organizationId else citizen?.id

        val boats =
            boatReserver?.let {
                boatService
                    .getBoatsForReserver(boatReserver)
                    .map { boat -> boat.updateBoatDisplayName(messageUtil) }
            } ?: emptyList()

        val municipalities = citizenService.getMunicipalities()
        val bodyContent =
            reservationFormView.boatSpaceForm(
                reservation,
                boats,
                citizen,
                organizations,
                input,
                getReservationTimeInSeconds(
                    reservation.created,
                    timeProvider.getCurrentDateTime()
                ),
                userType,
                municipalities
            )
        return bodyContent
    }

    fun getOrCreateReservationForCitizen(
        citizen: CitizenWithDetails,
        spaceId: Int
    ): Int {
        val result = permissionService.canReserveANewSlip(citizen.id)
        if (result is ReservationResult.Failure) {
            throw Forbidden("Citizen can not reserve slip", result.errorCode.toString())
        }

        val existingReservation = boatReservationService.getUnfinishedReservationForCitizen(citizen.id)

        return (
            if (existingReservation != null) {
                existingReservation.id
            } else {
                val today = timeProvider.getCurrentDate()
                boatReservationService
                    .insertBoatSpaceReservation(
                        citizen.id,
                        citizen.id,
                        spaceId,
                        today,
                        getEndDate(result),
                    ).id
            }
        )
    }

    fun getOrCreateReservationForEmployee(
        employeeId: UUID,
        spaceId: Int
    ): Int {
        val existingReservation =
            boatReservationService.getUnfinishedReservationForEmployee(employeeId)
        val reservationId =
            if (existingReservation != null) {
                existingReservation.id
            } else {
                val today = timeProvider.getCurrentDate()
                val endOfYear = LocalDate.of(today.year, Month.DECEMBER, 31)
                boatReservationService.insertBoatSpaceReservationAsEmployee(employeeId, spaceId, today, endOfYear).id
            }
        return reservationId
    }

    fun buildBoatFormParams(
        reservationId: Int,
        userType: UserType,
        citizen: CitizenWithDetails,
        isOrganization: Boolean?,
        organizationId: UUID?,
        boatId: Int?,
        boatType: BoatType?,
        boatWidth: BigDecimal?,
        boatLength: BigDecimal?
    ): BoatFormParams {
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
                    boatType = boatType ?: BoatType.OutboardMotor,
                    width = boatWidth,
                    length = boatLength,
                    depth = null,
                    weight = null,
                    boatRegistrationNumber = "",
                    otherIdentification = "",
                    extraInformation = "",
                    ownership = OwnershipStatus.Owner,
                    noRegistrationNumber = false,
                )
            }
        return BoatFormParams(userType, citizen, boats, reservationId, input)
    }

    private fun getEndDate(result: ReservationResult): LocalDate {
        val endOfYear = LocalDate.of(timeProvider.getCurrentDate().year, Month.DECEMBER, 31)
        val endDate =
            if (result is ReservationResult.Success) {
                result.data.endDate
            } else {
                endOfYear
            }
        return endDate
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
    val boatName: String?,
    val extraInformation: String?,
    override val noRegistrationNumber: Boolean?,
    override val boatRegistrationNumber: String?,
    override val otherIdentification: String?,
    @field:NotNull(message = "{validation.required}")
    val ownership: OwnershipStatus?,
    val firstName: String?,
    val lastName: String?,
    val ssn: String?,
    val address: String?,
    val postalCode: String?,
    val postalOffice: String?,
    val city: String?,
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
    val citizenSelection: String? = "newCitizen"
) : BoatRegistrationInput
