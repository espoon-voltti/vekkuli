import { client } from 'citizen-frontend/api-client'
import {
  CitizenBoatsResponse,
  CitizenOrganizationResponse,
  OrganizationContactDetailsResponse,
  UpdateCitizenInformationInput
} from 'citizen-frontend/api-types/citizen'
import {
  BoatSpaceReservation,
  ExistingBoatSpaceReservation,
  ExistingBoatSpaceReservationResponse
} from 'citizen-frontend/api-types/reservation'
import { formatCmToM } from 'citizen-frontend/shared/formatters'
import {
  Boat,
  ContactDetails,
  Organization
} from 'citizen-frontend/shared/types'
import { uri } from 'lib-common/uri'

import { deserializeJsonExistingBoatSpaceReservationResponse } from './reservation'

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
    hasNoRegistrationNumber: boat.registrationCode?.length === 0,
    ownership: boat.ownership,
    registrationNumber: boat.registrationCode || '',
    otherIdentification: boat.otherIdentification,
    extraInformation: boat.extraInformation
  }))
}

export async function organizationActiveReservations(
  orgId: string
): Promise<ExistingBoatSpaceReservation[]> {
  const { data: json } = await client.request<
    ExistingBoatSpaceReservationResponse[]
  >({
    url: uri`/current/organization-active-reservations/${orgId}`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonExistingBoatSpaceReservationResponse)
}

export async function citizenActiveReservations(): Promise<
  ExistingBoatSpaceReservation[]
> {
  const { data: json } = await client.request<
    ExistingBoatSpaceReservationResponse[]
  >({
    url: uri`/current/active-reservations`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonExistingBoatSpaceReservationResponse)
}

export async function citizenExpiredReservations(): Promise<
  ExistingBoatSpaceReservation[]
> {
  const { data: json } = await client.request<
    ExistingBoatSpaceReservationResponse[]
  >({
    url: uri`/current/expired-reservations`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonExistingBoatSpaceReservationResponse)
}

export async function organizationExpiredReservations(
  orgId: string
): Promise<ExistingBoatSpaceReservation[]> {
  const { data: json } = await client.request<
    ExistingBoatSpaceReservationResponse[]
  >({
    url: uri`/current/organization-expired-reservations/${orgId}`.toString(),
    method: 'GET'
  })
  return json.map(deserializeJsonExistingBoatSpaceReservationResponse)
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

export async function citizenOrganizationContactDetails(
  orgId: string
): Promise<ContactDetails[]> {
  const { data: json } =
    await client.request<OrganizationContactDetailsResponse>({
      url: uri`/current/organization-contact-details/${orgId}`.toString(),
      method: 'GET'
    })
  return json
}
