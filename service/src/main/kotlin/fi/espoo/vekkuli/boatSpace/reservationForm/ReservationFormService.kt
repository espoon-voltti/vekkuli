package fi.espoo.vekkuli.boatSpace.reservationForm

import fi.espoo.vekkuli.boatSpace.seasonalService.SeasonalService
import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.DomainConstants
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.*
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.*
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.validation.constraints.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

data class ReserveBoatSpaceInput(
    val reservationId: Int,
    val boatId: Int?,
    val boatType: BoatType,
    val boatRegistrationNumber: String?,
    val width: BigDecimal,
    val length: BigDecimal,
    val depth: BigDecimal,
    val weight: Int?,
    val boatName: String?,
    val otherIdentification: String?,
    val extraInformation: String?,
    val ownerShip: OwnershipStatus?,
    val email: String?,
    val phone: String?,
    val storageType: StorageType? = null,
    val trailerRegistrationNumber: String? = null,
    val trailerWidthInM: BigDecimal? = null,
    val trailerLengthInM: BigDecimal? = null,
)

@Service
class ReservationFormService(
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
    private val reservationRepository: ReservationFormRepository,
    private val boatRepository: BoatRepository,
    private val boatSpaceReservationRepo: BoatSpaceReservationRepository,
    private val boatSpaceRepository: BoatSpaceRepository,
    private val reserverRepository: ReserverRepository,
    private val emailEnv: EmailEnv,
    private val emailService: TemplateEmailService,
    private val seasonalService: SeasonalService,
    private val trailerRepository: TrailerRepository,
) {
    @Transactional
    fun createOrUpdateReserverAndReservationForCitizen(
        reservationId: Int,
        citizenId: UUID,
        input: ReservationInput,
    ) {
        val reservation = getReservationForApplicationForm(reservationId) ?: throw BadRequest("Reservation not found")
        var reserverId: UUID = citizenId
        if (input.isOrganization == true) {
            reserverId = addOrUpdateOrganization(citizenId, input)
        }
        reserveSpaceByCitizen(reservationId, reserverId, input, reservation.boatSpaceType)
    }

    fun getOrCreateReservationForCitizen(
        citizenId: UUID,
        spaceId: Int,
    ): Int {
        val boatSpace =
            boatSpaceRepository.getBoatSpace(spaceId)
                ?: throw BadRequest("Boat space not found")
        val result = seasonalService.canReserveANewSpace(citizenId, boatSpace.type)
        if (result is ReservationResult.Failure) {
            throw Forbidden("Citizen can not reserve slip", result.errorCode.toString())
        }

        val existingReservation = boatReservationService.getUnfinishedReservationForCitizen(citizenId)

        return (
            if (existingReservation != null) {
                existingReservation.id
            } else {
                val today = timeProvider.getCurrentDate()
                boatReservationService
                    .insertBoatSpaceReservation(
                        citizenId,
                        citizenId,
                        spaceId,
                        today,
                        getEndDate(result),
                    ).id
            }
        )
    }

    @Transactional
    fun updateReserverAndReservationForEmployee(
        reservationId: Int,
        input: ReservationInput
    ) {
        val citizen = createOrUpdateCitizen(input) ?: throw BadRequest("No citizen found.")

        var reserverId: UUID = citizen.id
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
                    "",
                    "",
                    "",
                    "",
                    name = input.orgName ?: "",
                    phone = input.orgPhone ?: "",
                    email = input.orgEmail ?: "",
                    streetAddress = input.orgAddress ?: "",
                    streetAddressSv = input.orgAddress ?: "",
                    postalCode = input.orgPostalCode ?: "",
                    postOffice = input.orgCity ?: "",
                    postOfficeSv = input.orgCity ?: "",
                    municipalityCode = (input.orgMunicipalityCode ?: "1").toInt(),
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
                    null,
                    null,
                    null,
                    null,
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

    @Transactional
    fun reserveSpaceForEmployee(
        reservationId: Int,
        reserverId: UUID,
        input: ReservationInput,
    ) {
        val reservation = getReservationForApplicationForm(reservationId)
        if (reservation == null) {
            throw BadRequest("Reservation not found")
        }

        val reserveSlipResult = seasonalService.canReserveANewSpace(reserverId, reservation.boatSpaceType)
        val data =
            if (reserveSlipResult is ReservationResult.Success) {
                reserveSlipResult.data
            } else {
                val now = timeProvider.getCurrentDate()
                // TODO: get validity from input parameter for employee
                ReservationResultSuccess(now, getLastDayOfYear(now.year), ReservationValidity.FixedTerm)
            }
        processBoatSpaceReservation(
            reserverId,
            buildReserveBoatSpaceInput(reservationId, input),
            ReservationStatus.Payment,
            data.reservationValidity,
            data.startDate,
            data.endDate
        )
    }

    @Transactional
    fun reserveSpaceByCitizen(
        reservationId: Int,
        reserverId: UUID,
        input: ReservationInput,
        boatSpaceType: BoatSpaceType
    ) {
        val reserveSlipResult = seasonalService.canReserveANewSpace(reserverId, boatSpaceType)

        if (!reserveSlipResult.success || reserveSlipResult !is ReservationResult.Success) {
            if (reserveSlipResult is ReservationResult.Failure) {
                throw Forbidden(
                    "Reservation not allowed",
                    reserveSlipResult.errorCode.toString()
                )
            }
            throw BadRequest("Reservation can not be made.")
        }
        processBoatSpaceReservation(
            reserverId,
            buildReserveBoatSpaceInput(reservationId, input),
            ReservationStatus.Payment,
            reserveSlipResult.data.reservationValidity,
            reserveSlipResult.data.startDate,
            reserveSlipResult.data.endDate
        )
    }

    fun getReservationForApplicationForm(reservationId: Int) = reservationRepository.getReservationForApplicationForm(reservationId)

    fun getBoatSpaceFormForCitizen(
        citizenId: UUID,
        reservationId: Int,
        formInput: ReservationInput,
        requestURI: String
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
                true,
                citizen?.fullName,
                requestURI,
                createBodyContent(formInput, citizen, reservation, UserType.CITIZEN)
            )
        )
    }

    fun getBoatSpaceFormForEmployee(
        reservationId: Int,
        formInput: ReservationInput,
        requestURI: String,
    ): String {
        val citizen = formInput.citizenId?.let { citizenService.getCitizen(formInput.citizenId) }

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

    fun getOrCreateReservationIdForEmployee(
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

    fun buildReserveBoatSpaceInput(
        reservationId: Int,
        input: BoatRegistrationBaseInput,
    ) = ReserveBoatSpaceInput(
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
        storageType = input.storageType,
        trailerRegistrationNumber = input.trailerRegistrationNumber,
        trailerLengthInM = input.trailerLength,
        trailerWidthInM = input.trailerWidth,
    )

    @Transactional
    fun processBoatSpaceReservation(
        reserverId: UUID,
        input: ReserveBoatSpaceInput,
        reservationStatus: ReservationStatus,
        reservationValidity: ReservationValidity,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        val boatSpace =
            boatReservationService.getBoatSpaceRelatedToReservation(input.reservationId)
                ?: throw IllegalArgumentException("Boat space not found")

        val boat = createOrUpdateBoat(reserverId, input)

        addReservationWarnings(input.reservationId, boatSpace, boat)

        if (boatSpace.type == BoatSpaceType.Winter) {
            updateReservationWithStorageTypeRelatedInformation(input, reserverId)
        }

        updateReserverContactInfo(reserverId, input)

        val reservation =
            boatSpaceReservationRepo.updateBoatInBoatSpaceReservation(
                input.reservationId,
                boat.id,
                reserverId,
                reservationStatus,
                reservationValidity,
                startDate,
                endDate
            )
        if (reservationStatus == ReservationStatus.Invoiced) {
            sendReservationConfirmationEmail(input, reserverId, boatSpace, reservation)
        }
    }

    private fun updateReservationWithStorageTypeRelatedInformation(
        input: ReserveBoatSpaceInput,
        reserverId: UUID,
    ) {
        if (input.storageType == null || input.storageType == StorageType.None) {
            throw IllegalArgumentException("Storage type has to be given.")
        }
        addStorageType(input.reservationId, input.storageType)
        if (input.storageType == StorageType.Trailer) {
            val trailer = createTrailerAndUpdateReservation(reserverId, input)
            boatReservationService.addTrailerWarningsToReservations(trailer.id, trailer.widthCm, trailer.lengthCm)
        }
    }

    private fun addStorageType(
        reservationId: Int,
        storageType: StorageType?
    ) {
        if (storageType == null) throw IllegalArgumentException("Storage type can not be null.")
        boatSpaceReservationRepo.updateStorageType(reservationId, storageType)
    }

    private fun createTrailerAndUpdateReservation(
        reserverId: UUID,
        input: ReserveBoatSpaceInput
    ): Trailer {
        if (
            input.trailerRegistrationNumber?.isEmpty() == false &&
            input.trailerWidthInM != null &&
            input.trailerLengthInM != null
        ) {
            return trailerRepository.insertTrailerAndAddToReservation(
                input.reservationId,
                reserverId,
                input.trailerRegistrationNumber,
                input.trailerWidthInM.mToCm(),
                input.trailerLengthInM.mToCm()
            )
        } else {
            throw IllegalArgumentException("Trailer information can not be empty.")
        }
    }

    private fun createOrUpdateBoat(
        reserverId: UUID,
        input: ReserveBoatSpaceInput
    ): Boat =
        if (input.boatId == 0 || input.boatId == null) {
            boatRepository.insertBoat(
                reserverId,
                input.boatRegistrationNumber ?: "",
                input.boatName!!,
                input.width.mToCm(),
                input.length.mToCm(),
                input.depth.mToCm(),
                input.weight!!,
                input.boatType,
                input.otherIdentification ?: "",
                input.extraInformation ?: "",
                input.ownerShip!!
            )
        } else {
            boatRepository.updateBoat(
                Boat(
                    id = input.boatId,
                    reserverId = reserverId,
                    registrationCode = input.boatRegistrationNumber ?: "",
                    name = input.boatName!!,
                    widthCm = input.width.mToCm(),
                    lengthCm = input.length.mToCm(),
                    depthCm = input.depth.mToCm(),
                    weightKg = input.weight!!,
                    type = input.boatType,
                    otherIdentification = input.otherIdentification ?: "",
                    extraInformation = input.extraInformation ?: "",
                    ownership = input.ownerShip!!
                )
            )
        }

    private fun addReservationWarnings(
        reservationId: Int,
        boatSpace: BoatSpace,
        boat: Boat
    ) {
        boatReservationService.addReservationWarnings(
            reservationId,
            boat.id,
            boatSpace.widthCm,
            boatSpace.lengthCm,
            boatSpace.amenity,
            boat.widthCm,
            boat.lengthCm,
            boat.ownership,
            boat.weightKg,
            boat.type,
            boatSpace.excludedBoatTypes ?: listOf(),
        )
    }

    private fun updateReserverContactInfo(
        reserverId: UUID,
        input: ReserveBoatSpaceInput
    ) {
        reserverRepository.updateCitizen(
            UpdateCitizenParams(
                id = reserverId,
                phone = input.phone ?: "",
                email = input.email ?: ""
            )
        )
    }

    private fun sendReservationConfirmationEmail(
        input: ReserveBoatSpaceInput,
        reserverId: UUID,
        boatSpace: BoatSpace,
        reservation: BoatSpaceReservation
    ) {
        emailService.sendEmail(
            "reservation_confirmation_invoice",
            null,
            emailEnv.senderAddress,
            Recipient(reserverId, input.email!!),
            mapOf(
                "name" to "${boatSpace.locationName} ${boatSpace.section}${boatSpace.placeNumber}",
                "width" to boatSpace.widthCm.cmToM(),
                "length" to boatSpace.lengthCm.cmToM(),
                "amenity" to messageUtil.getMessage("boatSpaces.amenityOption.${boatSpace.amenity}"),
                "endDate" to reservation.endDate,
                "invoiceDueDate" to
                    formatAsFullDate(
                        timeProvider.getCurrentDate().plusDays(DomainConstants.INVOICE_PAYMENT_PERIOD.toLong())
                    )
            )
        )
    }

    private fun createBodyContent(
        formInput: ReservationInput,
        citizen: CitizenWithDetails?,
        reservation: ReservationForApplicationForm,
        userType: UserType
    ): String {
        // use citizen's email and phone if not given in form input
        var input = formInput.copy(email = formInput.email ?: citizen?.email, phone = formInput.phone ?: citizen?.phone)

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

        return buildApplicationForm(reservation, boats, citizen, organizations, input, userType, municipalities)
    }

    private fun buildApplicationForm(
        reservation: ReservationForApplicationForm,
        boats: List<Boat>,
        citizen: CitizenWithDetails?,
        organizations: List<Organization>,
        input: ReservationInput,
        userType: UserType,
        municipalities: List<Municipality>,
    ): String {
        if (reservation.boatSpaceType == BoatSpaceType.Winter) {
            return (
                reservationFormView.winterStorageForm(
                    reservation,
                    boats,
                    citizen,
                    organizations,
                    input,
                    userType,
                    municipalities
                )
            )
        }
        return (
            reservationFormView.slipForm(
                reservation,
                boats,
                citizen,
                organizations,
                input,
                userType,
                municipalities
            )
        )
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

    private fun createOrUpdateCitizen(input: ReservationInput): CitizenWithDetails? {
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
}

@ValidBoatRegistration
data class ReservationInput(
    @field:NotNull(message = "{validation.required}")
    private val reservationId: Int?,
    override val boatId: Int?,
    @field:NotNull(message = "{validation.required}")
    override val boatType: BoatType?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val width: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val length: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val depth: BigDecimal?,
    @field:NotNull(message = "{validation.required}")
    @field:Positive(message = "{validation.positiveNumber}")
    override val weight: Int?,
    override val boatName: String?,
    override val extraInformation: String?,
    override val noRegistrationNumber: Boolean?,
    override val boatRegistrationNumber: String?,
    override val otherIdentification: String?,
    @field:NotNull(message = "{validation.required}")
    override val ownership: OwnershipStatus?,
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
    override val email: String?,
    @field:NotBlank(message = "{validation.required}")
    override val phone: String?,
    @field:AssertTrue(message = "{validation.certifyInformation}")
    override val certifyInformation: Boolean?,
    @field:AssertTrue(message = "{validation.agreeToRules}")
    override val agreeToRules: Boolean?,
    val isOrganization: Boolean?,
    val organizationId: UUID? = null,
    val orgName: String? = null,
    val orgBusinessId: String? = null,
    val orgMunicipalityCode: String? = null,
    override val orgPhone: String? = null,
    override val orgEmail: String? = null,
    val orgAddress: String? = null,
    val orgPostalCode: String? = null,
    val orgCity: String? = null,
    val citizenSelection: String? = "newCitizen",
    override val storageType: StorageType?,
    override val trailerRegistrationNumber: String?,
    override val trailerWidth: BigDecimal?,
    override val trailerLength: BigDecimal?
) : BoatRegistrationBaseInput
