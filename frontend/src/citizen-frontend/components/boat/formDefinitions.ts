import { UpdateCitizenBoatInput } from 'citizen-frontend/api-types/boat'
import {
  Boat,
  BoatType,
  boatTypes,
  OwnershipStatus,
  ownershipStatuses
} from 'citizen-frontend/shared/types'
import { number, positiveNumber, string } from 'lib-common/form/fields'
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
    id: number(),
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
  (values): UpdateCitizenBoatInput => ({
    id: values.id,
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

export const transformBoatToFormBoat = (
  boat: Boat,
  i18n: Translations
): StateOf<BoatForm> => ({
  id: boat.id,
  name: boat.name || '-',
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
  registrationNumber: boat.registrationNumber || '-',
  extraInformation: boat.extraInformation || '-',
  otherIdentification: boat.otherIdentification || '-',
  ownership: {
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
