import {
  BoatSpaceAmenity,
  BoatSpaceType,
  Trailer
} from 'citizen-frontend/shared/types'
import { union, value } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { StoredSearchState } from '../../useStoredSearchState'

import initialAllYearStorageFormState, {
  allYearStorageForm,
  StorageAmenity
} from './allYearStorage'
import initialTrailerFormState, { trailerStorageForm } from './trailerStorage'
import initialWinterStorageFormState, {
  onWinterStorageFormUpdate,
  winterStorageForm
} from './winterStorage'

export const spaceTypeInfoUnionForm = union({
  Slip: value<null>(),
  Trailer: trailerStorageForm,
  Winter: winterStorageForm,
  Storage: allYearStorageForm
})
export type SpaceTypeInfoUnionForm = typeof spaceTypeInfoUnionForm

export function initialSpaceTypeInfoFormState(
  i18n: Translations,
  type: BoatSpaceType,
  storedState?: StoredSearchState,
  reservationAmenity?: BoatSpaceAmenity,
  initialTrailer?: Trailer
): StateOf<SpaceTypeInfoUnionForm> {
  switch (type) {
    case 'Slip':
      return { branch: type, state: null }
    case 'Trailer':
      return {
        branch: type,
        state: initialTrailerFormState(storedState, initialTrailer)
      }
    case 'Winter':
      return {
        branch: type,
        state: initialWinterStorageFormState(initialTrailer)
      }
    case 'Storage':
      return {
        branch: type,
        state: initialAllYearStorageFormState(
          reservationAmenity as StorageAmenity,
          i18n
        )
      }
  }
}

export function onSpaceTypeInfoUpdate({
  prev,
  next
}: {
  prev: StateOf<SpaceTypeInfoUnionForm>
  next: StateOf<SpaceTypeInfoUnionForm>
}): StateOf<SpaceTypeInfoUnionForm> {
  if (prev.branch === 'Winter' && next.branch === 'Winter') {
    return {
      branch: next.branch,
      state: onWinterStorageFormUpdate({
        prev: prev.state,
        next: next.state
      })
    }
  }

  return next
}
