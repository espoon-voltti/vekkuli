import { uri } from 'lib-common/uri'

import { client } from '../api-client'
import { CitizenBoatsResponse } from '../api-types/citizen'
import {
  BoatSpaceReservation,
  BoatSpaceReservationResponse
} from '../api-types/reservation'
import { formatCmToM } from '../shared/formatters'
import { Boat } from '../shared/types'

import { deserializeJsonBoatSpaceReservationResponse } from './reservation'

export async function citizenBoats(): Promise<Boat[]> {
  const { data: json } = await client.request<CitizenBoatsResponse>({
    url: uri`/current/boats`.toString(),
    method: 'GET'
  })
  return deserializeJsonCitizenBoatsResponse(json)
}

export async function citizenBoatsInReservations(): Promise<Boat[]> {
  const { data: json } = await client.request<CitizenBoatsResponse>({
    url: uri`/current/boats`.toString(),
    method: 'GET'
  })
  return deserializeJsonCitizenBoatsResponse(json)
}

export async function citizenBoatsWithoutReservations(): Promise<Boat[]> {
  const { data: json } = await client.request<CitizenBoatsResponse>({
    url: uri`/current/boats`.toString(),
    method: 'GET'
  })
  return deserializeJsonCitizenBoatsResponse(json)
}

function deserializeJsonCitizenBoatsResponse(
  json: CitizenBoatsResponse
): Boat[] {
  return json.map((boat) => ({
    id: boat.id,
    name: boat.name,
    type: boat.type,
    width: formatCmToM(boat.widthCm),
    length: formatCmToM(boat.lengthCm),
    depth: formatCmToM(boat.depthCm),
    weight: boat.weightKg,
    hasNoRegistrationNumber: boat.registrationCode.length === 0,
    ownership: boat.ownership,
    registrationNumber: boat.registrationCode,
    otherIdentification: boat.otherIdentification,
    extraInformation: boat.extraInformation
  }))
}

export async function citizenActiveReservations(): Promise<
  BoatSpaceReservation[]
> {
  const { data: json } = await client.request<BoatSpaceReservationResponse[]>({
    url: uri`/current/active-reservations`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonBoatSpaceReservationResponse)
}
