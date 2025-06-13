import LocalDate from 'lib-common/date/local-date'

export const creationTypes = ['New', 'Renewal', 'Switch'] as const
export type CreationType = (typeof creationTypes)[number]

export const reservationStatuses = [
  'Info',
  'Payment',
  'Confirmed',
  'Cancelled',
  'Invoiced'
] as const

export type ReservationId = number

export type ReservationStatus = (typeof reservationStatuses)[number]

export const reserverTypes = ['Citizen', 'Organization'] as const

export type ReserverType = (typeof reserverTypes)[number]

export const storageTypes = ['Buck', 'BuckWithTent', 'Trailer'] as const

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
  municipalityCode: number
  municipalityName: string
  birthDate: LocalDate
  discountPercentage: number
}

export type Organization = {
  id: string
  name: string
  businessId: string
  municipalityCode: number
  municipalityName: string | null
  phone: string
  email: string
  streetAddress: string | null
  postalCode: string | null
  postOffice: string | null
  discountPercentage: number
}

export type NewOrganization = Omit<Organization, 'id'>

export type BoatId = number

export type Boat = {
  id: BoatId
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
  locationId: HarborId | null
  minLength: number | null
  maxLength: number | null
  minWidth: number | null
  maxWidth: number | null
}

export const harbors = [
  '1', // Haukilahti
  '2', // Kivenlahti
  '3', // Laajalahti
  '4', // Otsolahti
  '5', // Soukka
  '6', // Suomenoja
  '7', // Svinö
  '8' // Ämmäsmäki
] as const

export type HarborId = (typeof harbors)[number]

export type TrailerId = number

export type Trailer = {
  id: TrailerId
  width: number
  length: number
  registrationNumber: string
}

export type NewTrailer = Omit<Trailer, 'id'>

export type ContactDetails = {
  name: string
  phone: string
  email: string
}
