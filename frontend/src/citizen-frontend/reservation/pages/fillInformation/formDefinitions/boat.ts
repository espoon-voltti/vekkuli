import { positiveNumber, string } from 'lib-common/form/fields'
import { multiSelect, object, oneOf, required } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import {
  Boat,
  BoatType,
  boatTypes,
  OwnershipStatus,
  ownershipStatuses
} from '../../../../shared/types'

export const boatForm = object({
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

export const boatOwnershipTypeForm = object({
  status: required(oneOf<OwnershipStatus>())
})
export type BoatOwnershipTypeForm = typeof boatOwnershipTypeForm

export default function initialFormState(i18n: Translations) {
  return {
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
    }
  }
}

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
