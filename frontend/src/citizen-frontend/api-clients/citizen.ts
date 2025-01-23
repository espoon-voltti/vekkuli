import { uri } from 'lib-common/uri'

import { client } from '../api-client'
import {
  CitizenBoatsResponse,
  CitizenOrganizationResponse,
  UpdateCitizenInformationInput
} from '../api-types/citizen'
import {
  BoatSpaceReservation,
  BoatSpaceReservationResponse
} from '../api-types/reservation'
import { formatCmToM } from '../shared/formatters'
import { Boat, Organization } from '../shared/types'

import { deserializeJsonBoatSpaceReservationResponse } from './reservation'

export async function citizenBoats(): Promise<Boat[]> {
  const { data: json } = await client.request<CitizenBoatsResponse>({
    url: uri`/current/boats`.toString(),
    method: 'GET'
  })
  return deserializeJsonCitizenBoatsResponse(json)
}

export async function organizationBoats(orgId: string): Promise<Boat[]> {
  const { data: json } = await client.request<CitizenBoatsResponse>({
    url: uri`/current/organization-boats/${orgId}`.toString(),
    method: 'GET'
  })
  return deserializeJsonCitizenBoatsResponse(json)
}

export function mapResponseToBoatsByOrganization(
  json: Record<string, CitizenBoatsResponse>
): Record<string, Boat[]> {
  return Object.fromEntries(
    Object.entries(json).map(([orgId, boats]) => [
      orgId,
      deserializeJsonCitizenBoatsResponse(boats)
    ])
  )
}

export function deserializeJsonCitizenBoatsResponse(
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

export async function organizationActiveReservations(
  orgId: string
): Promise<BoatSpaceReservation[]> {
  const { data: json } = await client.request<BoatSpaceReservationResponse[]>({
    url: uri`/current/organization-active-reservations/${orgId}`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonBoatSpaceReservationResponse)
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

export async function citizenExpiredReservations(): Promise<
  BoatSpaceReservation[]
> {
  const { data: json } = await client.request<BoatSpaceReservationResponse[]>({
    url: uri`/current/expired-reservations`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonBoatSpaceReservationResponse)
}

export async function organizationExpiredReservations(
  orgId: string
): Promise<BoatSpaceReservation[]> {
  const { data: json } = await client.request<BoatSpaceReservationResponse[]>({
    url: uri`/current/organization-expired-reservations/${orgId}`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonBoatSpaceReservationResponse)
}

export async function citizenOrganizations(): Promise<Organization[]> {
  const { data: json } = await client.request<CitizenOrganizationResponse>({
    url: uri`/current/organizations`.toString(),
    method: 'GET'
  })
  return json
}

export async function updateCitizenInformation(
  input: UpdateCitizenInformationInput
): Promise<void> {
  await client.request<BoatSpaceReservation>({
    url: uri`/current/update-information`.toString(),
    method: 'POST',
    data: input
  })
}
