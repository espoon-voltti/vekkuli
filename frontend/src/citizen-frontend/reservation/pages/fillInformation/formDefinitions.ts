import { number, positiveNumber, string } from 'lib-common/form/fields'
import {
  multiSelect,
  object,
  oneOf,
  required,
  transformed
} from 'lib-common/form/form'
import { StateOf, ValidationSuccess } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { FillBoatSpaceReservationInput } from '../../../api-types/reservation'
import { formatMToCm } from '../../../shared/formatters'
import {
  Boat,
  BoatType,
  boatTypes,
  OwnershipStatus,
  ownershipStatuses,
  ReserverType
} from '../../../shared/types'

const reserverForm = object({
  email: required(string()),
  phone: required(string())
})
export type ReserverForm = typeof reserverForm

const renterTypeForm = object({
  type: oneOf<ReserverType>()
})
export type RenterTypeForm = typeof renterTypeForm

const boatForm = object({
  id: string(),
  name: required(string()),
  type: required(oneOf<BoatType>()),
  width: required(positiveNumber()),
  length: required(positiveNumber()),
  depth: required(positiveNumber()),
  weight: required(positiveNumber()),
  registrationNumber: string(),
  noRegisterNumber: multiSelect<boolean>(),
  otherIdentification: required(string()),
  extraInformation: string(),
  existingBoat: oneOf<Boat | undefined>()
})
export type BoatForm = typeof boatForm

const organizationForm = object({
  name: string(),
  businessId: string(),
  municipalityCode: number(),
  phone: string(),
  email: string(),
  address: string(),
  postalCode: string(),
  city: string()
})

export type OrganizationForm = typeof organizationForm

const boatOwnershipTypeForm = object({
  status: required(oneOf<OwnershipStatus>())
})
export type BoatOwnershipTypeForm = typeof boatOwnershipTypeForm

const userAgreementForm = object({
  agreements: required(multiSelect<boolean>())
})
export type UserAgreementForm = typeof userAgreementForm

export const reserveSpaceForm = transformed(
  object({
    reserver: reserverForm,
    renterType: renterTypeForm,
    organization: organizationForm,
    boat: boatForm,
    boatOwnership: boatOwnershipTypeForm,
    userAgreement: userAgreementForm
  }),
  ({
    reserver,
    organization,
    boat,
    boatOwnership,
    userAgreement,
    renterType
  }) => {
    const mapped: FillBoatSpaceReservationInput = {
      citizen: { ...reserver },
      organization:
        renterType.type === ReserverType.Organization
          ? { ...organization }
          : null,
      boat: {
        id: boat.id || undefined,
        name: boat.name,
        type: boat.type,
        width: formatMToCm(boat.width),
        length: formatMToCm(boat.length),
        depth: formatMToCm(boat.depth),
        weight: boat.weight,
        registrationNumber: boat.registrationNumber,
        hasNoRegistrationNumber:
          boat.noRegisterNumber == undefined ||
          boat.noRegisterNumber.length > 0,
        otherIdentification: boat.otherIdentification,
        extraInformation: boat.extraInformation,
        ownership: boatOwnership.status
      },
      certifyInformation: !!userAgreement.agreements?.includes(true),
      agreeToRules: !!userAgreement.agreements?.includes(true)
    }

    return ValidationSuccess.of(mapped)
  }
)

export type ReserveSpaceForm = typeof reserveSpaceForm

export const initialBoatValue = (): Boat => ({
  id: '',
  name: '',
  type: 'OutboardMotor',
  width: 0,
  length: 0,
  weight: 0,
  depth: 0,
  registrationNumber: '',
  otherIdentification: '',
  extraInformation: '',
  hasNoRegistrationNumber: false,
  ownership: 'Owner'
})

export const transformBoatToFormBoat = (
  boat: Boat,
  i18n: Translations
): StateOf<BoatForm> => ({
  id: boat.id.toString(),
  name: boat.name,
  depth: boat.depth.toString(),
  length: boat.length.toString(),
  weight: boat.weight.toString(),
  width: boat.width.toString(),
  type: {
    domValue: boat.type,
    options: boatTypes.map((type) => ({
      domValue: type,
      label: i18n.boatSpace.boatType[type],
      value: type
    }))
  },
  noRegisterNumber: {
    domValues: boat.registrationNumber.length === 0 ? [''] : [],
    options: [
      {
        domValue: '',
        label: i18n.reservation.noRegistererNumber,
        value: true
      }
    ]
  },
  registrationNumber: boat.registrationNumber || '',
  existingBoat: {
    domValue: '',
    options: []
  },
  extraInformation: boat.extraInformation || '',
  otherIdentification: boat.otherIdentification || ''
})

export function initialFormState(i18n: Translations) {
  return {
    reserver: {
      email: '',
      phone: ''
    },
    organization: {
      name: '',
      businessId: '',
      municipalityCode: NaN,
      phone: '',
      email: '',
      address: '',
      postalCode: '',
      city: ''
    },
    renterType: {
      type: {
        domValue: ReserverType.Citizen,
        options: Object.values(ReserverType).map((type) => ({
          domValue: type,
          label: i18n.boatSpace.renterType[type],
          value: type
        }))
      }
    },
    boat: {
      id: '',
      name: '',
      type: {
        domValue: 'OutboardMotor',
        options: boatTypes.map((type) => ({
          domValue: type,
          label: i18n.boatSpace.boatType[type],
          value: type
        }))
      },
      width: positiveNumber.empty().value,
      length: positiveNumber.empty().value,
      depth: positiveNumber.empty().value,
      weight: positiveNumber.empty().value,
      registrationNumber: '',
      noRegisterNumber: {
        domValues: [],
        options: [
          {
            domValue: '',
            label: i18n.reservation.noRegistererNumber,
            value: true
          }
        ]
      },
      otherIdentification: '',
      extraInformation: '',
      existingBoat: {
        domValue: '',
        options: []
      }
    },
    boatOwnership: {
      status: {
        domValue: 'Owner',
        options: ownershipStatuses.map((type) => ({
          domValue: type,
          label: i18n.boatSpace.ownershipStatus[type],
          value: type
        }))
      }
    },
    userAgreement: {
      agreements: {
        domValues: [],
        options: [
          {
            domValue: 'certify',
            label: i18n.reservation.certify,
            value: true
          },
          {
            domValue: 'agreeToRules',
            label: i18n.reservation.agreeToRules,
            value: true
          }
        ]
      }
    }
  }
}
