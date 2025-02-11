import { number, positiveNumber, string } from 'lib-common/form/fields'
import {
  multiSelect,
  object,
  oneOf,
  required,
  validated,
  value
} from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import {
  Boat,
  BoatSpaceType,
  BoatType,
  boatTypes,
  OwnershipStatus,
  ownershipStatuses
} from '../../../../shared/types'
import { StoredSearchState } from '../../useStoredSearchState'

export const boatInfoForm = object({
  id: number(),
  name: required(string()),
  type: required(oneOf<BoatType>()),
  width: required(positiveNumber()),
  length: required(positiveNumber()),
  depth: required(positiveNumber()),
  weight: required(positiveNumber()),
  registrationNumber: validated(
    object({
      number: string(),
      noRegisterNumber: multiSelect<boolean>()
    }),
    (fields) => {
      const noRegisterNumberSelected = fields.noRegisterNumber?.length || 0
      return fields.number.length > 0 || noRegisterNumberSelected
        ? undefined
        : { number: 'required' }
    }
  ),
  otherIdentification: required(string()),
  extraInformation: string()
})
export type BoatInfoForm = typeof boatInfoForm

export const boatOwnershipTypeForm = required(oneOf<OwnershipStatus>())
export type BoatOwnershipTypeForm = typeof boatOwnershipTypeForm

export const boatSelectionForm = oneOf<Boat | null>()

export type BoatSelectionForm = typeof boatSelectionForm

export const boatForm = object({
  boatSelection: boatSelectionForm,
  boatInfo: boatInfoForm,
  ownership: boatOwnershipTypeForm,
  newBoatCache: value<StateOf<BoatInfoForm>>()
})
export type BoatForm = typeof boatForm

export default function initialFormState(
  i18n: Translations,
  boats: Boat[],
  boatSpaceType: BoatSpaceType,
  excludedBoatTypes?: BoatType[],
  storedSearchState?: StoredSearchState,
  selectedBoat?: Boat
): StateOf<BoatForm> {
  return {
    boatInfo: initialBoatInfoFormState(
      i18n,
      boatSpaceType,
      excludedBoatTypes,
      storedSearchState,
      selectedBoat
    ),
    boatSelection: initialBoatSelectionState(boats, selectedBoat),
    ownership: initialOwnershipState(
      i18n,
      boatSpaceType,
      selectedBoat?.ownership
    ),
    newBoatCache: initialBoatInfoFormState(
      i18n,
      boatSpaceType,
      excludedBoatTypes,
      storedSearchState
    )
  }
}

function initialBoatInfoFormState(
  i18n: Translations,
  boatSpaceType: BoatSpaceType,
  excludedBoatTypes?: BoatType[],
  storedSearchState?: StoredSearchState,
  defaultBoat?: Boat
) {
  if (defaultBoat)
    return transformBoatToFormBoat(defaultBoat, i18n, excludedBoatTypes)
  let width = positiveNumber.empty().value
  let length = positiveNumber.empty().value
  if (boatSpaceType !== 'Winter') {
    width = storedSearchState?.width ?? positiveNumber.empty().value
    length = storedSearchState?.length ?? positiveNumber.empty().value
  }

  const acceptedBoatTypes = getAcceptedBoatTypes(excludedBoatTypes)

  return {
    id: 0,
    name: '',
    type: {
      domValue: storedSearchState?.boatType ?? 'OutboardMotor',
      options: acceptedBoatTypes.map((type) => ({
        domValue: type,
        label: i18n.boatSpace.boatType[type],
        value: type
      }))
    },
    width,
    length,
    depth: positiveNumber.empty().value,
    weight: positiveNumber.empty().value,
    registrationNumber: {
      number: '',
      noRegisterNumber: {
        domValues: [],
        options: [
          {
            domValue: '',
            label: i18n.reservation.noRegistererNumber,
            value: true
          }
        ]
      }
    },
    otherIdentification: '',
    extraInformation: ''
  }
}

const initialOwnershipState = (
  i18n: Translations,
  boatSpaceType: BoatSpaceType,
  initialValue?: OwnershipStatus
) => ({
  domValue: initialValue || 'Owner',
  options: ownershipStatuses.map((type) => ({
    domValue: type,
    label: i18n.boatSpace.ownershipStatus[type],
    info: i18n.boatSpace.ownershipStatusInfo(type, boatSpaceType),
    value: type
  }))
})

const initialBoatSelectionState = (
  boats: Boat[],
  selectedBoat?: Boat
): StateOf<BoatSelectionForm> => {
  const initialBoatSelection: StateOf<BoatSelectionForm> = {
    domValue: selectedBoat?.id?.toString() ?? '',
    options: boats.map((boat) => ({
      domValue: boat.id.toString(),
      label: boat.name,
      value: boat
    }))
  }
  if (initialBoatSelection.options.length > 0)
    initialBoatSelection.options.unshift({
      domValue: '',
      label: 'Uusi vene',
      value: null
    })
  return initialBoatSelection
}

const transformBoatToFormBoat = (
  boat: Boat,
  i18n: Translations,
  excludedBoatTypes?: BoatType[]
): StateOf<BoatInfoForm> => ({
  id: boat.id,
  name: boat.name,
  depth: boat.depth.toString(),
  length: boat.length.toString(),
  weight: boat.weight.toString(),
  width: boat.width.toString(),
  type: {
    domValue: boat.type,
    options: getAcceptedBoatTypes(excludedBoatTypes).map((type) => ({
      domValue: type,
      label: i18n.boatSpace.boatType[type],
      value: type
    }))
  },
  registrationNumber: {
    number: boat.registrationNumber || '',
    noRegisterNumber: {
      domValues: boat.registrationNumber.length === 0 ? [''] : [],
      options: [
        {
          domValue: '',
          label: i18n.reservation.noRegistererNumber,
          value: true
        }
      ]
    }
  },

  extraInformation: boat.extraInformation || '',
  otherIdentification: boat.otherIdentification || ''
})

type BoatFormUpdateProps = {
  prev: StateOf<BoatForm>
  next: StateOf<BoatForm>
  boats: Boat[]
  i18n: Translations
  excludedBoatTypes?: BoatType[]
}

function getAcceptedBoatTypes(excludedBoatTypes?: BoatType[]) {
  return boatTypes.filter(
    (type) => !excludedBoatTypes || !excludedBoatTypes.includes(type)
  )
}

export function onBoatFormUpdate({
  prev,
  next,
  boats,
  i18n,
  excludedBoatTypes
}: BoatFormUpdateProps): StateOf<BoatForm> {
  const prevBoatId = prev.boatSelection.domValue
  const nextBoatId = next.boatSelection.domValue
  const selectedBoat = boats.find(
    (boat) => boat.id === parseInt(nextBoatId, 10)
  )
  // Boat has been changed, we need to update the form values
  if (prevBoatId !== nextBoatId) {
    const cache = !prevBoatId ? prev.boatInfo : prev.newBoatCache

    if (!nextBoatId) {
      return {
        ...next,
        boatInfo: cache,
        boatSelection: initialBoatSelectionState(boats)
      }
    }
    if (selectedBoat) {
      return {
        ...next,
        boatInfo: transformBoatToFormBoat(
          selectedBoat,
          i18n,
          excludedBoatTypes
        ),
        newBoatCache: cache,
        boatSelection: initialBoatSelectionState(boats, selectedBoat)
      }
    }
  }

  return {
    ...next,
    boatSelection: initialBoatSelectionState(boats, selectedBoat)
  }
}
