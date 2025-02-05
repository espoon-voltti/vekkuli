import LocalDate from 'lib-common/date/local-date'

import HelsinkiDateTime from '../../lib-common/date/helsinki-date-time'
import {
  Boat,
  BoatSpace,
  Citizen,
  CreationType,
  NewBoat,
  NewOrganization,
  NewTrailer,
  Organization,
  ReservationId,
  ReservationStatus,
  ReservationValidity,
  ReserverType,
  StorageType,
  Trailer
} from '../shared/types'

import { CitizenBoatsResponse } from './citizen'

export type UnfinishedBoatSpaceReservation = {
  reservation: BoatSpaceReservation
  boats: Boat[]
  municipalities: Municipality[]
  organizations: Organization[]
  organizationsBoats: Record<string, Boat[]>
  organizationRevisedPrices: RevisedPrice[]
}

export type ReservationOperation = 'Switch' | 'Renew' | 'Terminate'

export type BoatSpaceReservation = {
  id: ReservationId
  citizen?: Citizen
  status: ReservationStatus
  boatSpace: BoatSpace
  startDate: LocalDate
  endDate: LocalDate
  validity: ReservationValidity
  totalPrice: string
  vatValue: string
  netPrice: string
  storageType?: StorageType
  trailer?: Trailer
  boat: Boat
  creationType: CreationType
  canReserveNew: boolean
  revisedPrice: RevisedPrice
  reserverType: ReserverType
}

export type ExistingBoatSpaceReservation = {
  id: ReservationId
  boatSpace: BoatSpace
  allowedReservationOperations: ReservationOperation[]
  created: HelsinkiDateTime
  endDate: LocalDate
  validity: ReservationValidity
  active: boolean
  totalPrice: string
  vatValue: string
  boat: Boat
  storageType?: StorageType
  paymentDate?: LocalDate
  trailer?: Trailer
  reserverType: ReserverType
}

export type CanReserveResultStatus =
  | 'CanReserve'
  | 'CanNotReserve'
  | 'CanReserveOnlyForOrganization'

export type SwitchableOrganizationReservation = {
  organizationName: string
  reservations: SwitchableReservation[]
}

export type SwitchableReservation = {
  id: ReservationId
  boatSpace: BoatSpace
  totalPrice: string
  vatValue: string
}

export type CanReserveReservation = {
  status: CanReserveResultStatus
  switchableReservations: SwitchableReservation[]
  switchableOrganizationReservations: SwitchableOrganizationReservation[]
}

export type BoatSpaceReservationResponse = {
  id: ReservationId
  reserverType: ReserverType
  citizen?: ResponseCitizen
  boatSpace: BoatSpace
  status: ReservationStatus
  startDate: string
  endDate: string
  validity: ReservationValidity
  totalPrice: string
  vatValue: string
  netPrice: string
  storageType: StorageType | null
  trailer: Trailer | null
  boat: Boat
  creationType: CreationType
  canReserveNew: boolean
  revisedPrice: RevisedPrice
}

export type ExistingBoatSpaceReservationResponse = {
  id: ReservationId
  boatSpace: BoatSpace
  canRenew: boolean
  canSwitch: boolean
  created: string
  endDate: string
  validity: ReservationValidity
  isActive: boolean
  totalPrice: string
  vatValue: string
  boat: Boat
  storageType: StorageType | null
  paymentDate?: string | null
  trailer?: Trailer | null
  reserverType: ReserverType
}

export type RevisedPrice = {
  reserverType: ReserverType
  id?: string
  name?: string
  discountPercentage: number
  revisedPriceInEuro: string
  revisedPriceWithDiscountInEuro: string
}

export type UnfinishedBoatSpaceReservationResponse = {
  reservation: BoatSpaceReservationResponse
  boats: CitizenBoatsResponse
  municipalities: Municipality[]
  organizations: Organization[]
  organizationsBoats: Record<string, CitizenBoatsResponse>
  organizationRevisedPrices: RevisedPrice[]
}

type ResponseCitizen = Omit<Citizen, 'birthDate'> & { birthDate: string }

export type FillBoatSpaceReservationInput = {
  citizen: Pick<Citizen, 'email' | 'phone'>
  organization: Organization | NewOrganization | null
  boat: Boat | NewBoat | null
  certifyInformation: boolean
  agreeToRules: boolean
  storageType: StorageType | null
  trailer: Trailer | NewTrailer | null
}

export type PaymentInformation = {
  name: string
  url: string
  method: string
  icon: string
  svg: string
  id: string
  group: string
  parameters: {
    name: string
    value: string
  }[]
}

export type PaymentInformationResponse = {
  providers: PaymentInformation[]
}

export type Municipality = {
  code: number
  name: string
}
