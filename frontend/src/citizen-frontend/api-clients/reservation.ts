import LocalDate from 'lib-common/date/local-date'
import { uri } from 'lib-common/uri'

import { client } from '../api-client'
import {
  BoatSpaceReservation,
  BoatSpaceReservationResponse,
  FillBoatSpaceReservationInput,
  PaymentInformationResponse
} from '../api-types/reservation'

export async function reserveSpace(
  spaceId: number
): Promise<BoatSpaceReservation> {
  const { data: json } = await client.request<BoatSpaceReservation>({
    url: uri`/reserve/${spaceId}`.toString(),
    method: 'POST'
  })
  return json
}

export async function unfinishedReservation(): Promise<BoatSpaceReservation> {
  const { data: json } = await client.request<BoatSpaceReservationResponse>({
    url: uri`/unfinished-reservation`.toString(),
    method: 'GET'
  })
  return deserializeJsonBoatSpaceReservationResponse(json)
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

export async function fillReservation(
  reservationId: number,
  input: FillBoatSpaceReservationInput
): Promise<void> {
  await client.request<BoatSpaceReservation>({
    url: uri`/reservation/${reservationId}/fill`.toString(),
    method: 'POST',
    data: input
  })
}

export async function cancelReservation(reservationId: number): Promise<void> {
  await client.request<BoatSpaceReservation>({
    url: uri`/reservation/${reservationId}/cancel`.toString(),
    method: 'DELETE'
  })
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

function deserializeJsonBoatSpaceReservationResponse(
  json: BoatSpaceReservationResponse
): BoatSpaceReservation {
  return {
    id: json.id,
    citizen: {
      ...json.citizen,
      birthDate: LocalDate.parseIso(json.citizen.birthDate)
    },
    status: json.status,
    startDate: LocalDate.parseIso(json.startDate),
    endDate: LocalDate.parseIso(json.endDate),
    validity: json.validity,
    boatSpace: json.boatSpace,
    totalPrice: json.totalPrice,
    vatValue: json.vatValue,
    netPrice: json.netPrice
  }
}
