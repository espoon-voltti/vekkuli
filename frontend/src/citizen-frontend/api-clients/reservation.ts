import HelsinkiDateTime from 'lib-common/date/helsinki-date-time'
import LocalDate from 'lib-common/date/local-date'
import { uri } from 'lib-common/uri'

import { client } from '../api-client'
import {
  BoatSpaceReservation,
  BoatSpaceReservationResponse,
  CanReserveReservation,
  ExistingBoatSpaceReservation,
  ExistingBoatSpaceReservationResponse,
  FillBoatSpaceReservationInput,
  Municipality,
  PaymentInformationResponse,
  ReservationInfo,
  ReservationInfoResponse,
  ReservationBeingSwitchedResponse,
  ReservationOperation,
  SwitchReservationInformation,
  UnfinishedBoatSpaceReservation,
  UnfinishedBoatSpaceReservationResponse
} from '../api-types/reservation'

import {
  deserializeJsonCitizenBoatsResponse,
  mapResponseToBoatsByOrganization
} from './citizen'

export async function reserveSpace(
  spaceId: number
): Promise<BoatSpaceReservation> {
  const { data: json } = await client.request<BoatSpaceReservation>({
    url: uri`/reserve/${spaceId}`.toString(),
    method: 'POST'
  })
  return json
}

export async function canReserveSpace(
  spaceId: number
): Promise<CanReserveReservation> {
  const { data } = await client.request<CanReserveReservation>({
    url: uri`/can-reserve/${spaceId}`.toString(),
    method: 'GET'
  })
  return data
}

export async function startToSwitchBoatSpace(input: {
  reservationId: number
  spaceId: number
}): Promise<void> {
  await client.request<void>({
    url: uri`/reservation/${input.reservationId}/switch/${input.spaceId}`.toString(),
    method: 'POST'
  })
}

export async function unfinishedReservation(): Promise<UnfinishedBoatSpaceReservation> {
  const { data: json } =
    await client.request<UnfinishedBoatSpaceReservationResponse>({
      url: uri`/unfinished-reservation`.toString(),
      method: 'GET'
    })
  return {
    reservation: deserializeJsonBoatSpaceReservationResponse(json.reservation),
    boats: deserializeJsonCitizenBoatsResponse(json.boats),
    municipalities: json.municipalities,
    organizations: json.organizations,
    organizationsBoats: mapResponseToBoatsByOrganization(
      json.organizationsBoats
    ),
    organizationReservationInfos: json.organizationReservationInfos.map(
      deserializeReservationInfo
    )
  }
}

function deserializeReservationInfo(
  info: ReservationInfoResponse
): ReservationInfo {
  return {
    reserverType: info.reserverType,
    id: info.id,
    name: info.name,
    discountPercentage: info.discountPercentage,
    revisedPriceInEuro: info.revisedPriceInEuro,
    revisedPriceWithDiscountInEuro: info.revisedPriceWithDiscountInEuro,
    validity: info.validity,
    endDate: LocalDate.parseIso(info.endDate)
  }
}

export async function unfinishedReservationExpiration(): Promise<number> {
  const { data: json } = await client.request<number>({
    url: uri`/unfinished-reservation-expiration`.toString(),
    method: 'GET'
  })
  return json
}

export async function getReservation(
  reservationId: number
): Promise<BoatSpaceReservation> {
  const { data: json } = await client.request<BoatSpaceReservationResponse>({
    url: uri`/reservation/${reservationId}`.toString(),
    method: 'GET'
  })
  return deserializeJsonBoatSpaceReservationResponse(json)
}

export async function getReservationBeingSwitched(
  reservationId: number
): Promise<SwitchReservationInformation> {
  const { data: json } = await client.request<ReservationBeingSwitchedResponse>(
    {
      url: uri`/reservation/${reservationId}/switch-source`.toString(),
      method: 'GET'
    }
  )
  return json
}

export async function municipalities(): Promise<Municipality[]> {
  const { data: json } = await client.request<Municipality[]>({
    url: uri`/municipalities`.toString(),
    method: 'GET'
  })

  return json
}

export async function fillReservation(
  reservationId: number,
  input: FillBoatSpaceReservationInput
): Promise<BoatSpaceReservation> {
  const { data: json } = await client.request<BoatSpaceReservationResponse>({
    url: uri`/reservation/${reservationId}/fill`.toString(),
    method: 'POST',
    data: input
  })
  return deserializeJsonBoatSpaceReservationResponse(json)
}

export async function cancelReservation(reservationId: number): Promise<void> {
  await client.request<BoatSpaceReservation>({
    url: uri`/reservation/${reservationId}/cancel`.toString(),
    method: 'DELETE'
  })
}

export async function cancelPayment(reservationId: number): Promise<void> {
  await client.request<void>({
    url: uri`/reservation/${reservationId}/cancel-payment`.toString(),
    method: 'PATCH'
  })
}

export async function terminateReservation(
  reservationId: number
): Promise<void> {
  await client.request<BoatSpaceReservation>({
    url: uri`/reservation/${reservationId}/terminate`.toString(),
    method: 'POST'
  })
}

export async function startRenewReservation(
  reservationId: number
): Promise<BoatSpaceReservation> {
  const { data } = await client.request<BoatSpaceReservation>({
    url: uri`/reservation/${reservationId}/renew`.toString(),
    method: 'POST'
  })
  return data
}

export async function paymentInformation(
  reservationId: number
): Promise<PaymentInformationResponse> {
  const { data } = await client.request<PaymentInformationResponse>({
    url: uri`/reservation/${reservationId}/payment-information`.toString(),
    method: 'POST'
  })

  return data
}

export function deserializeJsonBoatSpaceReservationResponse(
  json: BoatSpaceReservationResponse
): BoatSpaceReservation {
  const citizen = json.citizen
    ? { ...json.citizen, birthDate: LocalDate.parseIso(json.citizen.birthDate) }
    : undefined

  return {
    id: json.id,
    citizen,
    status: json.status,
    startDate: LocalDate.parseIso(json.startDate),
    endDate: LocalDate.parseIso(json.endDate),
    validity: json.validity,
    boatSpace: json.boatSpace,
    totalPrice: json.totalPrice,
    vatValue: json.vatValue,
    netPrice: json.netPrice,
    reservationInfo: deserializeReservationInfo(json.reservationInfo),
    boat: json.boat,
    storageType: json.storageType ?? undefined,
    trailer: json.trailer ?? undefined,
    creationType: json.creationType,
    canReserveNew: json.canReserveNew,
    reserverType: json.reserverType
  }
}

export function deserializeJsonExistingBoatSpaceReservationResponse(
  json: ExistingBoatSpaceReservationResponse
): ExistingBoatSpaceReservation {
  const createAllowedOperationsList = (
    json: ExistingBoatSpaceReservationResponse
  ): ReservationOperation[] => {
    const operationsList: ReservationOperation[] = []
    if (json.canRenew) operationsList.push('Renew')
    if (json.canSwitch) operationsList.push('Switch')
    return operationsList
  }

  return {
    id: json.id,
    created: HelsinkiDateTime.parseIso(json.created),
    endDate: LocalDate.parseIso(json.endDate),
    validity: json.validity,
    active: json.isActive,
    boatSpace: json.boatSpace,
    paymentDate: json.paymentDate
      ? LocalDate.parseIso(json.paymentDate)
      : undefined,
    totalPrice: json.totalPrice,
    vatValue: json.vatValue,
    boat: json.boat,
    storageType: json.storageType ?? undefined,
    trailer: json.trailer ?? undefined,
    allowedReservationOperations: createAllowedOperationsList(json),
    reserverType: json.reserverType
  }
}
