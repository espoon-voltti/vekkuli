import {
  Boat,
  BoatType,
  boatTypes,
  OwnershipStatus,
  ownershipStatuses
} from 'citizen-frontend/shared/types'
import { positiveNumber, string } from 'lib-common/form/fields'
import { multiSelect, object, oneOf, required } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

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
  existingBoat: oneOf<Boat | undefined>(),
  ownershipStatus: oneOf<OwnershipStatus>()
})
export type BoatForm = typeof boatForm

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
  otherIdentification: boat.otherIdentification || '',
  ownershipStatus: {
    domValue: boat.ownership,
    options: ownershipStatuses.map((type) => ({
      domValue: type,
      label: i18n.boatSpace.ownershipStatus[type],
      value: type
    }))
  }
})

export const showBoatsForm = object({
  show: multiSelect<boolean>()
})

export const initShowBoatsForm = (
  i18n: Translations
): StateOf<typeof showBoatsForm> => ({
  show: {
    domValues: [],
    options: [
      {
        domValue: 'show',
        label: 'Näytä myös veneet joita ei ole liitetty venepaikkoihin',
        value: true
      }
    ]
  }
})
