package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.boatSpace.admin.Layout
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.repository.UpdateOrganizationParams
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.getLastDayOfYear
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.validation.constraints.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class ReservationService(
    private val organizationService: OrganizationService,
    private val reservationService: BoatReservationService,
    private val citizenService: CitizenService,
    private val reservationFormView: ReservationFormView,
    private val boatService: BoatService,
    private val messageUtil: MessageUtil,
    private val timeProvider: TimeProvider,
    private val employeeLayout: EmployeeLayout,
    private val citizenLayout: Layout,
    private val permissionService: PermissionService,
    private val reservationRepository: ReservationRepository,
    private val boatReservationService: BoatReservationService,
) {
    fun createOrUpdateReserverAndReservation(
        reservationId: Int,
        citizenId: UUID,
        input: ReservationInput
    ) {
        val reserverId: UUID = citizenId
        if (input.isOrganization == true) {
            addOrUpdateOrganization(citizenId, input)
        }
        reserveSpace(reservationId, reserverId, input)
    }

    fun addOrUpdateOrganization(
        citizenId: UUID,
        input: ReservationInput,
    ): UUID? {
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

    fun reserveSpace(
        reservationId: Int,
        reserverId: UUID,
        input: ReservationInput,
    ) {
        val reserveSlipResult = permissionService.canReserveANewSlip(reserverId)

        val data =
            if (reserveSlipResult is ReservationResult.Success) {
                reserveSlipResult.data
            } else {
                val now = timeProvider.getCurrentDate()
                ReservationResultSuccess(now, getLastDayOfYear(now.year), ReservationValidity.FixedTerm,)
            }

        if (reserveSlipResult.success) {
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
                UserType.CITIZEN,
                municipalities
            )
        return (
            citizenLayout.render(
                bodyContent
            )
        )
    }

    fun getBoatSpaceFormForEmployee(
        reservationId: Int,
        formInput: ReservationInput,
        requestURI: String,
    ): String {
        val reservation =
            reservationRepository.getReservationForApplicationForm(reservationId)
                ?: throw BadRequest("Reservation not found")
        if (reservation == null) {
            throw BadRequest("Reservation not found")
        }
        val citizen = formInput.citizenId?.let { citizenService.getCitizen(formInput.citizenId) }
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
                UserType.EMPLOYEE,
                municipalities
            )
        return (
            employeeLayout.render(
                true,
                requestURI,
                bodyContent
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
        reservationService.removeBoatSpaceReservation(reservationId, citizenId)
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
