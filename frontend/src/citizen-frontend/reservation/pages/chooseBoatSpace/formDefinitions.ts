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
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

const searchSpaceParamsForm = object({
  boatType: oneOf<BoatType>(),
  width: required(positiveNumber()),
  length: required(positiveNumber()),
  amenities: multiSelect<BoatSpaceAmenity>(),
  harbor: multiSelect<Harbor>()
})

type SearchSpaceParamsForm = typeof searchSpaceParamsForm

const boatSpaceUnionForm = union({
  Slip: searchSpaceParamsForm,
  Trailer: searchSpaceParamsForm,
  Winter: searchSpaceParamsForm,
  Storage: searchSpaceParamsForm
})

export type SearchFormUnion = typeof boatSpaceUnionForm

const boatSpaceUnionCache = object({
  Slip: value<StateOf<SearchSpaceParamsForm>>(),
  Trailer: value<StateOf<SearchSpaceParamsForm>>(),
  Winter: value<StateOf<SearchSpaceParamsForm>>(),
  Storage: value<StateOf<SearchSpaceParamsForm>>()
})

export type BoatSpaceUnionCache = typeof boatSpaceUnionCache

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
      amenities: output.boatSpaceUnionForm.value.amenities || [],
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

export function initialFormState(i18n: Translations): StateOf<SearchForm> {
  return {
    boatSpaceType: {
      domValue: 'Slip',
      options: boatSpaceTypes.map((type) => ({
        domValue: type,
        label: i18n.boatSpace.boatSpaceType[type].label,
        info: i18n.boatSpace.boatSpaceType[type].info,
        value: type
      }))
    },
    boatSpaceUnionForm: initialUnionFormState(i18n, 'Slip'),
    boatSpaceUnionCache: initialUnionCacheFormState(i18n)
  }
}

export type SearchFormBranches = BoatSpaceType

const initialUnionCacheFormState = (
  i18n: Translations
): StateOf<BoatSpaceUnionCache> => {
  return {
    Slip: initialUnionFormState(i18n, 'Slip').state,
    Trailer: initialUnionFormState(i18n, 'Trailer').state,
    Winter: initialUnionFormState(i18n, 'Winter').state,
    Storage: initialUnionFormState(i18n, 'Storage').state
  }
}

export const initialUnionFormState = (
  i18n: Translations,
  branch: BoatSpaceType
): StateOf<SearchFormUnion> => {
  let branchAmenities: BoatSpaceAmenity[] = []
  let branchHarbors: Harbor[] = []
  let branchBoatTypes: BoatType[] = []

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
  }

  return {
    branch: branch,
    state: {
      boatType: {
        domValue: 'OutboardMotor',
        options: branchBoatTypes.map((type) => ({
          domValue: type,
          label: i18n.boatSpace.boatType[type],
          value: type
        }))
      },
      amenities: {
        domValues: [],
        options: Object.values(branchAmenities).map((amenity) => ({
          domValue: amenity,
          label: i18n.boatSpace.amenities[amenity],
          value: amenity
        }))
      },
      harbor: {
        domValues: [],
        options: branchHarbors.map((harbor) => ({
          domValue: harbor.value,
          label: harbor.label,
          value: harbor
        }))
      },
      width: positiveNumber.empty().value,
      length: positiveNumber.empty().value
    }
  }
}
