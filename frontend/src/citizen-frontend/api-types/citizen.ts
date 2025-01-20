import { BoatType, Organization, OwnershipStatus } from '../shared/types'

export type CitizenBoatsResponse = {
  id: number
  registrationCode: string
  name: string
  widthCm: number
  lengthCm: number
  depthCm: number
  weightKg: number
  type: BoatType
  otherIdentification: string
  extraInformation: string
  ownership: OwnershipStatus
}[]

export type CitizenOrganizationResponse = Organization[]

export type UpdateCitizenInformationInput = {
  email: string
  phone: string
}
