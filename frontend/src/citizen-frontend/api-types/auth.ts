import { UUID } from 'lib-common/types'

export interface CitizenUserDetails {
  id: UUID
  firstName: string
  lastName: string
  email: string
  phone: string
  municipalityName: string
  streetAddress: string
  streetAddressSv: string
  postOffice: string
  postOfficeSv: string
  postalCode: string
  birthday: string
}

export interface CitizenUserResponse {
  details: CitizenUserDetails
}

export type AuthStatus =
  | { loggedIn: false; apiVersion: string }
  | {
      loggedIn: true
      user: CitizenUserResponse
      apiVersion: string
    }
