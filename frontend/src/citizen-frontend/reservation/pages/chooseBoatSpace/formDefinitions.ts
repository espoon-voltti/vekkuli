import { positiveNumber } from 'lib-common/form/fields'
import {
  mapped,
  multiSelect,
  object,
  oneOf,
  required,
  union
} from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { SearchFreeSpacesParams } from '../../../api-types/free-spaces'
import {
  BoatSpaceAmenity,
  BoatSpaceType,
  boatSpaceTypes,
  BoatType,
  boatTypes,
  Harbor,
  harbors
} from '../../../shared/types'

const sharedUnionDefinition = () =>
  object({
    boatType: required(oneOf<BoatType>()),
    width: required(positiveNumber()),
    length: required(positiveNumber()),
    amenities: multiSelect<BoatSpaceAmenity>(),
    harbor: multiSelect<Harbor>()
  })

const boatSpaceUnionForm = union({
  Slip: sharedUnionDefinition(),
  Trailer: sharedUnionDefinition(),
  Winter: sharedUnionDefinition(),
  Storage: sharedUnionDefinition()
})

export type SearchFormUnion = typeof boatSpaceUnionForm

export const searchFreeSpacesForm = mapped(
  object({
    boatSpaceType: required(oneOf<BoatSpaceType>()),
    boatSpaceUnionForm: boatSpaceUnionForm
  }),
  (output): SearchFreeSpacesParams => {
    return {
      spaceType: output.boatSpaceType,
      boatType: output.boatSpaceUnionForm.value.boatType,
      amenities: output.boatSpaceUnionForm.value.amenities || [],
      harbor:
        output.boatSpaceUnionForm.value.harbor?.map((harbor) => harbor.value) ||
        [],
      width: output.boatSpaceUnionForm.value.width,
      length: output.boatSpaceUnionForm.value.length
    }
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
    boatSpaceUnionForm: initialUnionFormState(i18n, 'Slip')
  }
}

export type SearchFormBranches = BoatSpaceType

export const initialUnionFormState = (
  i18n: Translations,
  branch: BoatSpaceType
): StateOf<SearchFormUnion> => {
  let branchAmenities: BoatSpaceAmenity[] = []
  let branchHarbors: Harbor[] = []
  switch (branch) {
    case 'Slip':
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
        options: boatTypes.map((type) => ({
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
