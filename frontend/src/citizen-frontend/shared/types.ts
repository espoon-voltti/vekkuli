import LocalDate from 'lib-common/date/local-date'

export const reservationStatuses = [
  'Info',
  'Renewal',
  'Payment',
  'Confirmed',
  'Cancelled',
  'Invoiced'
] as const

export type ReservationStatus = (typeof reservationStatuses)[number]

export enum ReserverType {
  Citizen = 'Citizen',
  Organization = 'Organization'
}

export const storageTypes = ['Trailer', 'Buck', 'BuckWithTent'] as const

export type StorageType = (typeof storageTypes)[number]

export const boatSpaceTypes = ['Slip', 'Trailer', 'Winter', 'Storage'] as const

export type BoatSpaceType = (typeof boatSpaceTypes)[number]

export const boatSpaceAmenities = [
  'Buoy',
  'RearBuoy',
  'Beam',
  'WalkBeam',
  'Trailer',
  'Buck'
] as const

export type BoatSpaceAmenity = (typeof boatSpaceAmenities)[number]

export const boatTypes = [
  'OutboardMotor',
  'Sailboat',
  'InboardMotor',
  'Rowboat',
  'JetSki',
  'Other'
] as const

export type BoatType = (typeof boatTypes)[number]

export const reservationValidities = ['Indefinite', 'FixedTerm'] as const

export type ReservationValidity = (typeof reservationValidities)[number]

export const ownershipStatuses = [
  'Owner',
  'User',
  'CoOwner',
  'FutureOwner'
] as const

export type OwnershipStatus = (typeof ownershipStatuses)[number]

export type Citizen = {
  id: string
  firstName: string
  lastName: string
  email: string
  phone: string
  address: string
  postalCode: string
  postalOffice: string
  city: string
  municipalityCode: number
  birthDate: LocalDate
}

export type Organization = {
  id: string
  name: string
  businessId: string
  municipalityCode: number
  phone: string
  email: string
  address: string | null
  postalCode: string | null
  city: string | null
}

export type NewOrganization = Omit<Organization, 'id'>

export type Boat = {
  id: string
  name: string
  type: BoatType
  width: number
  length: number
  depth: number
  weight: number
  registrationNumber: string
  hasNoRegistrationNumber: boolean
  otherIdentification: string
  extraInformation: string | null
  ownership: OwnershipStatus
}

export type NewBoat = Omit<Boat, 'id'>

export type BoatSpace = {
  id: string
  type: BoatSpaceType
  section: string
  placeNumber: number
  amenity: BoatSpaceAmenity
  width: number
  length: number
  description: string
  excludedBoatTypes: BoatType[] | null
  locationName: string | null
}

export const harbors = [
  { value: '1', label: 'Haukilahti' },
  { value: '2', label: 'Kivenlahti' },
  { value: '3', label: 'Laajalahti' },
  { value: '4', label: 'Otsolahti' },
  { value: '5', label: 'Soukka' },
  { value: '6', label: 'Suomenoja' },
  { value: '7', label: 'Svinö' }
] as const

export type Harbor = (typeof harbors)[number]

export type Trailer = {
  id: number
  width: number
  length: number
  registrationNumber: string
}

export type NewTrailer = Omit<Trailer, 'id'>
