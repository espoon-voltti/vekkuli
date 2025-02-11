import { SearchFreeSpacesParams } from 'citizen-frontend/api-types/free-spaces'
import { SwitchReservationInformation } from 'citizen-frontend/api-types/reservation'
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

type SearchSpaceParamsForm = typeof searchSpaceParamsForm

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

export function initialFormState(
  storedSearchState: StoredSearchState | undefined,
  switchInfo: SwitchReservationInformation | undefined
): StateOf<SearchForm> {
  const boatSpaceType =
    switchInfo?.spaceType ??
    (storedSearchState?.spaceType as BoatSpaceType) ??
    'Slip'
  return {
    boatSpaceType: {
      domValue: boatSpaceType,
      options: boatSpaceTypes.map((type) => ({
        domValue: type,
        label: (i18n: Translations) => i18n.boatSpace.boatSpaceType[type].label,
        info: (i18n: Translations) => i18n.boatSpace.boatSpaceType[type].info,
        value: type,
        disabled:
          switchInfo?.spaceType !== undefined && type !== switchInfo.spaceType
      }))
    },
    boatSpaceUnionForm: initialUnionFormState(boatSpaceType, storedSearchState),
    boatSpaceUnionCache: initialUnionCacheFormState(storedSearchState)
  }
}

export type SearchFormBranches = BoatSpaceType

const initialUnionCacheFormState = (
  storedSearchState: StoredSearchState | undefined
): StateOf<BoatSpaceUnionCache> => {
  return {
    Slip: initialUnionFormState('Slip', storedSearchState).state,
    Trailer: initialUnionFormState('Trailer', storedSearchState).state,
    Winter: initialUnionFormState('Winter', storedSearchState).state,
    Storage: initialUnionFormState('Storage', storedSearchState).state
  }
}

export const initialUnionFormState = (
  branch: BoatSpaceType,
  storedSearchState: StoredSearchState | undefined
): StateOf<SearchFormUnion> => {
  let branchAmenities: BoatSpaceAmenity[] = []
  let branchHarbors: Harbor[] = []
  let branchBoatTypes: BoatType[] = []
  let storageAmenities: BoatSpaceAmenity[] = []

  switch (branch) {
    case 'Slip':
      branchBoatTypes = boatTypes.map((t) => t)
      branchAmenities = ['Buoy', 'RearBuoy', 'Beam', 'WalkBeam']
      branchHarbors = harbors.map((h) => h)
      break
    case 'Trailer':
      branchHarbors = harbors.map((h) => h)
      break
    case 'Winter':
      branchHarbors = harbors.filter((h) =>
        ['Laajalahti', 'Otsolahti', 'Suomenoja'].includes(h.label)
      )
      break
    case 'Storage':
      storageAmenities = ['Trailer', 'Buck']
      break
  }
  const selectedHarbors = storedSearchState?.harbor
    ? branchHarbors
        .filter((h) => storedSearchState?.harbor?.includes(h.value))
        .map((h) => h.value)
    : []

  return {
    branch: branch,
    state: {
      boatType: {
        domValue: storedSearchState?.boatType ?? 'OutboardMotor',
        options: branchBoatTypes.map((type) => ({
          domValue: type,
          label: (i18n) => i18n.boatSpace.boatType[type],
          value: type
        }))
      },
      amenities: {
        domValues: storedSearchState?.amenities ?? [],
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
      width: storedSearchState?.width ?? positiveNumber.empty().value,
      length: storedSearchState?.length ?? positiveNumber.empty().value,
      storageAmenity: {
        domValue: storedSearchState?.storageAmenity || 'Trailer',
        options: storageAmenities.map((type) => ({
          domValue: type,
          label: (i18n) => i18n.boatSpace.amenities[type],
          value: type
        }))
      }
    }
  }
}
