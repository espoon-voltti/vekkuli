import LocalDate from 'lib-common/date/local-date'

import {
  Boat,
  BoatSpace,
  Citizen,
  NewBoat,
  NewOrganization,
  Organization,
  ReservationStatus,
  ReservationValidity
} from '../shared/types'

export type BoatSpaceReservation = {
  id: number
  citizen: Citizen
  status: ReservationStatus
  boatSpace: BoatSpace
  startDate: LocalDate
  endDate: LocalDate
  validity: ReservationValidity
  totalPrice: string
  vatValue: string
  netPrice: string
  boat: Boat
}

export type BoatSpaceReservationResponse = {
  id: number
  citizen: ResponseCitizen
  boatSpace: BoatSpace
  status: ReservationStatus
  startDate: string
  endDate: string
  validity: ReservationValidity
  totalPrice: string
  vatValue: string
  netPrice: string
  boat: Boat
}

type ResponseCitizen = Omit<Citizen, 'birthDate'> & { birthDate: string }

export type FillBoatSpaceReservationInput = {
  citizen: Pick<Citizen, 'email' | 'phone'>
  organization: Organization | NewOrganization | null
  boat: Boat | NewBoat | null
  certifyInformation: boolean
  agreeToRules: boolean
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
