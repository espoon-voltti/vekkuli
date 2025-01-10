import { BoatSpaceType } from 'citizen-frontend/shared/types'
import { union, value } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import initialWinterStorageFormState, {
  onWinterStorageFormUpdate,
  winterStorageForm
} from './winterStorage'
import { StoredState } from '../../chooseBoatSpace/formDefinitions'

export const spaceTypeInfoUnionForm = union({
  Slip: value<null>(),
  Trailer: value<null>(),
  Winter: winterStorageForm,
  Storage: value<null>()
})
export type SpaceTypeInfoUnionForm = typeof spaceTypeInfoUnionForm

export function initialSpaceTypeInfoFormState(
  i18n: Translations,
  type: BoatSpaceType,
  storedState?: StoredState
): StateOf<SpaceTypeInfoUnionForm> {
  switch (type) {
    case 'Slip':
      return { branch: type, state: null }
    case 'Trailer':
      return { branch: type, state: null }
    case 'Winter':
      return {
        branch: type,
        state: initialWinterStorageFormState(i18n, storedState)
      }
    case 'Storage':
      return { branch: type, state: null }
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
