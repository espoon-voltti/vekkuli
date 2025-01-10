import LocalDate from 'lib-common/date/local-date'

import HelsinkiDateTime from '../../lib-common/date/helsinki-date-time'
import {
  Boat,
  BoatSpace,
  Citizen,
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
  storageType?: StorageType
  trailer?: Trailer
  boat: Boat
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
  totalPrice: string
  vatValue: string
  netPrice: string
  storageType: StorageType | null
  trailer: Trailer | null
  boat: Boat
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
