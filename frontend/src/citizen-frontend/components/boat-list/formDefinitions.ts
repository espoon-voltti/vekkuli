import { UpdateBoatInput } from 'citizen-frontend/api-types/boat'
import {
  Boat,
  BoatType,
  boatTypes,
  OwnershipStatus,
  ownershipStatuses
} from 'citizen-frontend/shared/types'
import { positiveNumber, string } from 'lib-common/form/fields'
import {
  mapped,
  multiSelect,
  object,
  oneOf,
  required
} from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

export const boatForm = mapped(
  object({
    name: required(string()),
    type: required(oneOf<BoatType>()),
    width: required(positiveNumber()),
    length: required(positiveNumber()),
    depth: required(positiveNumber()),
    weight: required(positiveNumber()),
    registrationNumber: string(),
    otherIdentification: required(string()),
    extraInformation: string(),
    ownership: required(oneOf<OwnershipStatus>())
  }),
  (values): UpdateBoatInput => ({
    name: values.name,
    type: values.type,
    width: values.width,
    length: values.length,
    depth: values.depth,
    weight: values.weight,
    registrationNumber: values.registrationNumber,
    hasNoRegistrationNumber: !values.registrationNumber,
    otherIdentification: values.otherIdentification,
    extraInformation: values.extraInformation,
    ownership: values.ownership
  })
)
export type BoatForm = typeof boatForm

export const transformBoatToFormBoat = (boat: Boat): StateOf<BoatForm> => ({
  name: boat.name || '',
  depth: boat.depth.toString(),
  length: boat.length.toString(),
  weight: boat.weight.toString(),
  width: boat.width.toString(),
  type: {
    domValue: boat.type,
    options: boatTypes.map((type) => ({
      domValue: type,
      label: (i18n: Translations) => i18n.boatSpace.boatType[type],
      value: type
    }))
  },
  registrationNumber: boat.registrationNumber || '',
  extraInformation: boat.extraInformation || '',
  otherIdentification: boat.otherIdentification || '',
  ownership: {
    domValue: boat.ownership,
    options: ownershipStatuses.map((type) => ({
      domValue: type,
      label: (i18n: Translations) => i18n.boatSpace.ownershipStatus[type],
      value: type
    }))
  }
})

export const showBoatsForm = object({
  show: multiSelect<boolean>()
})

export const initShowBoatsForm = (): StateOf<typeof showBoatsForm> => ({
  show: {
    domValues: [],
    options: [
      {
        domValue: 'show',
        label: (i18n: Translations) =>
          i18n.citizenPage.reservation.showAllBoats,
        value: true
      }
    ]
  }
})
