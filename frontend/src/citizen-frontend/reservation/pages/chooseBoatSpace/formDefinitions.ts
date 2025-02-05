import { SearchFreeSpacesParams } from 'citizen-frontend/api-types/free-spaces'
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
import { BoundForm } from 'lib-common/form/hooks'
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
  i18n: Translations,
  storedSearchState: StoredSearchState | undefined
): StateOf<SearchForm> {
  const boatSpaceType =
    (storedSearchState?.spaceType as BoatSpaceType) ?? 'Slip'
  return {
    boatSpaceType: {
      domValue: boatSpaceType,
      options: boatSpaceTypes.map((type) => ({
        domValue: type,
        label: i18n.boatSpace.boatSpaceType[type].label,
        info: i18n.boatSpace.boatSpaceType[type].info,
        value: type
      }))
    },
    boatSpaceUnionForm: initialUnionFormState(
      i18n,
      boatSpaceType,
      storedSearchState
    ),
    boatSpaceUnionCache: initialUnionCacheFormState(i18n, storedSearchState)
  }
}

function mapOptionsWithTranslatedLabels<T extends { value: string }>(
  options: T[],
  translationPath: Record<string, string>
): T[] {
  return options.map((option) => ({
    ...option,
    label: translationPath[option.value]
  }))
}

/**
 * Form component (RadioField, SelectField, CheckBoxField) option labels
 * get entered into the form as translated strings.
 * When the language changes, the strings do not get updated automatically.
 * This applies also to the union cache that is utilized when switching between
 * form branches.
 */
export function onLanguageChange(
  bind: BoundForm<SearchForm>,
  i18n: Translations
) {
  bind.update((prev) => ({
    ...prev,
    boatSpaceType: {
      ...prev.boatSpaceType,
      options: prev.boatSpaceType.options.map((option) => {
        return {
          ...option,
          label: i18n.boatSpace.boatSpaceType[option.value].label,
          info: i18n.boatSpace.boatSpaceType[option.value].info
        }
      })
    },
    boatSpaceUnionForm: {
      ...prev.boatSpaceUnionForm,
      state: {
        ...prev.boatSpaceUnionForm.state,
        amenities: {
          ...prev.boatSpaceUnionForm.state.amenities,
          options: mapOptionsWithTranslatedLabels(
            prev.boatSpaceUnionForm.state.amenities.options,
            i18n.boatSpace.amenities
          )
        },
        boatType: {
          ...prev.boatSpaceUnionForm.state.boatType,
          options: mapOptionsWithTranslatedLabels(
            prev.boatSpaceUnionForm.state.boatType.options,
            i18n.boatSpace.boatType
          )
        },
        storageAmenity: {
          ...prev.boatSpaceUnionForm.state.storageAmenity,
          options: mapOptionsWithTranslatedLabels(
            prev.boatSpaceUnionForm.state.storageAmenity.options,
            i18n.boatSpace.amenities
          )
        }
      }
    },
    // Slip and Storage are currently the only form branches that have translated labels
    boatSpaceUnionCache: {
      ...prev.boatSpaceUnionCache,
      Slip: {
        ...prev.boatSpaceUnionCache.Slip,
        amenities: {
          ...prev.boatSpaceUnionCache.Slip.amenities,
          options: mapOptionsWithTranslatedLabels(
            prev.boatSpaceUnionCache.Slip.amenities.options,
            i18n.boatSpace.amenities
          )
        },
        boatType: {
          ...prev.boatSpaceUnionCache.Slip.boatType,
          options: mapOptionsWithTranslatedLabels(
            prev.boatSpaceUnionCache.Slip.boatType.options,
            i18n.boatSpace.boatType
          )
        }
      },
      Storage: {
        ...prev.boatSpaceUnionCache.Storage,
        storageAmenity: {
          ...prev.boatSpaceUnionCache.Storage.storageAmenity,
          options: mapOptionsWithTranslatedLabels(
            prev.boatSpaceUnionCache.Storage.storageAmenity.options,
            i18n.boatSpace.amenities
          )
        }
      }
    }
  }))
}

export type SearchFormBranches = BoatSpaceType

const initialUnionCacheFormState = (
  i18n: Translations,
  storedSearchState: StoredSearchState | undefined
): StateOf<BoatSpaceUnionCache> => {
  return {
    Slip: initialUnionFormState(i18n, 'Slip', storedSearchState).state,
    Trailer: initialUnionFormState(i18n, 'Trailer', storedSearchState).state,
    Winter: initialUnionFormState(i18n, 'Winter', storedSearchState).state,
    Storage: initialUnionFormState(i18n, 'Storage', storedSearchState).state
  }
}

export const initialUnionFormState = (
  i18n: Translations,
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
          label: i18n.boatSpace.boatType[type],
          value: type
        }))
      },
      amenities: {
        domValues: storedSearchState?.amenities ?? [],
        options: Object.values(branchAmenities).map((amenity) => ({
          domValue: amenity,
          label: i18n.boatSpace.amenities[amenity],
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
          label: i18n.boatSpace.amenities[type],
          value: type
        }))
      }
    }
  }
}
