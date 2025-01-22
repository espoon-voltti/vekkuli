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
  ReservationStatus,
  ReservationValidity,
  ReserverType,
  StorageType,
  Trailer
} from '../shared/types'

export type ReservationOperation = 'Switch' | 'Renew' | 'Terminate'

export type BoatSpaceReservation = {
  id: number
  citizen?: Citizen
  organization?: Organization
  status: ReservationStatus
  boatSpace: BoatSpace
  created: HelsinkiDateTime
  startDate: LocalDate
  endDate: LocalDate
  validity: ReservationValidity
  paymentDate?: LocalDate
  totalPrice: string
  vatValue: string
  netPrice: string
  revisedPrice: string
  storageType?: StorageType
  trailer?: Trailer
  boat: Boat
  creationType: CreationType
  allowedReservationOperations: ReservationOperation[]
}

export type CanReserveResultStatus =
  | 'CanReserve'
  | 'CanNotReserve'
  | 'CanReserveOnlyForOrganization'

export type SwitchableReservation = {
  id: number
  boatSpace: BoatSpace
  totalPrice: string
  vatValue: string
}
export type CanReserveReservation = {
  status: CanReserveResultStatus
  switchableReservations: SwitchableReservation[]
}

export type BoatSpaceReservationResponse = {
  id: number
  reserverType: ReserverType
  citizen?: ResponseCitizen
  organization?: Organization
  boatSpace: BoatSpace
  status: ReservationStatus
  created: string
  startDate: string
  endDate: string
  validity: ReservationValidity
  paymentDate: string | null
  revisedPrice: string
  totalPrice: string
  vatValue: string
  netPrice: string
  storageType: StorageType | null
  trailer: Trailer | null
  boat: Boat
  creationType: CreationType
  canRenew: boolean
  canSwitch: boolean
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
  code: string
  name: string
}
