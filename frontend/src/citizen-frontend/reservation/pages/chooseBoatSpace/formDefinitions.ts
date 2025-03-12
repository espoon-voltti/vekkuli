import { SearchFreeSpacesParams } from 'citizen-frontend/api-types/free-spaces'
import { SwitchReservationInformation } from 'citizen-frontend/api-types/reservation'
import { formatInputNumberValue } from 'citizen-frontend/shared/formatters'
import {
  BoatSpaceAmenity,
  BoatSpaceType,
  boatSpaceTypes,
  BoatType,
  boatTypes,
  Harbor,
  harbors
} from 'citizen-frontend/shared/types'
import { positiveNumber } from 'lib-common/form/fields'
import {
  mapped,
  multiSelect,
  object,
  oneOf,
  required,
  union,
  value
} from 'lib-common/form/form'
import { OutputOf, StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { StoredSearchState } from '../useStoredSearchState'

const searchSpaceParamsForm = object({
  boatType: oneOf<BoatType>(),
  width: required(positiveNumber()),
  length: required(positiveNumber()),
  amenities: multiSelect<BoatSpaceAmenity>(),
  storageAmenity: oneOf<BoatSpaceAmenity>(),
  harbor: multiSelect<Harbor>()
})

export type SearchSpaceParamsForm = typeof searchSpaceParamsForm

const boatSpaceUnionForm = union({
  Slip: searchSpaceParamsForm,
  Trailer: searchSpaceParamsForm,
  Winter: searchSpaceParamsForm,
  Storage: searchSpaceParamsForm
})

type BoatSpaceUnionForm = typeof boatSpaceUnionForm

export type SearchFormUnion = typeof boatSpaceUnionForm

const boatSpaceUnionCache = object({
  Slip: value<StateOf<SearchSpaceParamsForm>>(),
  Trailer: value<StateOf<SearchSpaceParamsForm>>(),
  Winter: value<StateOf<SearchSpaceParamsForm>>(),
  Storage: value<StateOf<SearchSpaceParamsForm>>()
})

export type BoatSpaceUnionCache = typeof boatSpaceUnionCache

function parseAmenities(
  boatSpaceUnionForm: OutputOf<BoatSpaceUnionForm>
): BoatSpaceAmenity[] {
  const { amenities, storageAmenity } = boatSpaceUnionForm.value

  if (storageAmenity) return [storageAmenity]

  return amenities || []
}

export const searchFreeSpacesForm = mapped(
  object({
    boatSpaceType: required(oneOf<BoatSpaceType>()),
    boatSpaceUnionForm: boatSpaceUnionForm,
    boatSpaceUnionCache: boatSpaceUnionCache
  }),
  (output): SearchFreeSpacesParams => {
    const boatTypeValue = output.boatSpaceUnionForm.value.boatType
    const result: SearchFreeSpacesParams = {
      spaceType: output.boatSpaceType,
      amenities: parseAmenities(output.boatSpaceUnionForm),
      harbor:
        output.boatSpaceUnionForm.value.harbor?.map((harbor) => harbor.value) ||
        [],
      width: output.boatSpaceUnionForm.value.width,
      length: output.boatSpaceUnionForm.value.length
    }
    if (boatTypeValue) {
      result.boatType = boatTypeValue
    }

    return result
  }
)
export type SearchForm = typeof searchFreeSpacesForm

function buildBranchDefaultValues(
  branch: BoatSpaceType,
  storedSearchState: StoredSearchState | undefined,
  switchInfo: SwitchReservationInformation | undefined
): initialFormStateDefaultValue {
  let width = positiveNumber.empty().value
  let length = positiveNumber.empty().value
  let boatType: BoatType = 'OutboardMotor'
  let harbors: Harbor[] = []
  let amenities: BoatSpaceAmenity[] = []

  if (switchInfo && switchInfo.spaceType === branch) {
    if (switchInfo.width) width = formatInputNumberValue(switchInfo.width)
    if (switchInfo.width) length = formatInputNumberValue(switchInfo.length)
    if (switchInfo.boatType) boatType = switchInfo.boatType
  } else if (storedSearchState && storedSearchState[branch]) {
    if (storedSearchState[branch].width)
      width = formatInputNumberValue(storedSearchState[branch].width)
    if (storedSearchState[branch].length)
      length = formatInputNumberValue(storedSearchState[branch].length)
    if (storedSearchState[branch].boatType)
      boatType = storedSearchState[branch].boatType
    if (storedSearchState[branch].amenities.length) {
      amenities = storedSearchState[branch].amenities
    }
    if (storedSearchState[branch].harbors.length) {
      harbors = storedSearchState[branch].harbors
    }
  }

  return {
    width,
    length,
    harbors,
    amenities,
    boatType
  }
}

export function buildDefaultValues(
  storedSearchState: StoredSearchState | undefined,
  switchInfo: SwitchReservationInformation | undefined
): initialFormStateDefaultValues {
  return {
    branch: storedSearchState?.branch || 'Slip',
    Slip: buildBranchDefaultValues('Slip', storedSearchState, switchInfo),
    Trailer: buildBranchDefaultValues('Trailer', storedSearchState, switchInfo),
    Winter: buildBranchDefaultValues('Winter', storedSearchState, switchInfo),
    Storage: buildBranchDefaultValues('Storage', storedSearchState, switchInfo)
  }
}

type initialFormStateDefaultValues = {
  branch: BoatSpaceType
  Slip: initialFormStateDefaultValue
  Trailer: initialFormStateDefaultValue
  Winter: initialFormStateDefaultValue
  Storage: initialFormStateDefaultValue
}

type initialFormStateDefaultValue = {
  width: string
  length: string
  harbors: Harbor[]
  amenities: BoatSpaceAmenity[]
  boatType: BoatType
}

export function initialFormState(
  defaults: initialFormStateDefaultValues,
  lockedSpaceType?: BoatSpaceType
): StateOf<SearchForm> {
  return {
    boatSpaceType: {
      domValue: defaults.branch,
      options: boatSpaceTypes.map((type) => ({
        domValue: type,
        label: (i18n: Translations) => i18n.boatSpace.boatSpaceType[type].label,
        info: (i18n: Translations) => i18n.boatSpace.boatSpaceType[type].info,
        value: type,
        disabled: lockedSpaceType !== undefined && type !== lockedSpaceType
      }))
    },
    boatSpaceUnionForm: initialUnionFormState(defaults.branch, defaults),
    boatSpaceUnionCache: initialUnionCacheFormState(defaults)
  }
}

export type SearchFormBranches = BoatSpaceType

const initialUnionCacheFormState = (
  defaults: initialFormStateDefaultValues
): StateOf<BoatSpaceUnionCache> => {
  return {
    Slip: initialUnionFormState('Slip', defaults).state,
    Trailer: initialUnionFormState('Trailer', defaults).state,
    Winter: initialUnionFormState('Winter', defaults).state,
    Storage: initialUnionFormState('Storage', defaults).state
  }
}

export const initialUnionFormState = (
  branch: BoatSpaceType,
  defaults: initialFormStateDefaultValues
): StateOf<SearchFormUnion> => {
  let branchAmenities: BoatSpaceAmenity[] = []
  let branchHarbors: Harbor[] = []
  let branchBoatTypes: BoatType[] = []
  let storageAmenities: BoatSpaceAmenity[] = []
  let selectedStorageAmenity = ''
  const width = defaults[branch].width
  const length = defaults[branch].length

  switch (branch) {
    case 'Slip':
      branchBoatTypes = boatTypes.map((t) => t)
      branchAmenities = ['Buoy', 'RearBuoy', 'Beam', 'WalkBeam']
      branchHarbors = harbors.map((h) => h)
      break
    case 'Winter':
      branchHarbors = harbors.filter((h) =>
        ['Laajalahti', 'Otsolahti', 'Suomenoja'].includes(h.label)
      )
      break
    case 'Storage':
      storageAmenities = ['Trailer', 'Buck']
      selectedStorageAmenity = defaults[branch].amenities[0] || 'Trailer'
      break
  }

  const selectedHarbors = branchHarbors
    .filter((bh) =>
      defaults[branch].harbors.map((dh) => dh.value).includes(bh.value)
    )
    .map((h) => h.value)

  return {
    branch: branch,
    state: {
      boatType: {
        domValue: defaults[branch].boatType,
        options: branchBoatTypes.map((type) => ({
          domValue: type,
          label: (i18n) => i18n.boatSpace.boatType[type],
          value: type
        }))
      },
      amenities: {
        domValues: defaults[branch].amenities ?? [],
        options: Object.values(branchAmenities).map((amenity) => ({
          domValue: amenity,
          label: (i18n) => i18n.boatSpace.amenities[amenity],
          value: amenity
        }))
      },
      harbor: {
        domValues: selectedHarbors,
        options: branchHarbors.map((harbor) => ({
          domValue: harbor.value,
          label: harbor.label,
          value: harbor
        }))
      },
      width: width,
      length: length,
      storageAmenity: {
        domValue: selectedStorageAmenity,
        options: storageAmenities.map((type) => ({
          domValue: type,
          label: (i18n) => i18n.boatSpace.amenities[type],
          value: type
        }))
      }
    }
  }
}
