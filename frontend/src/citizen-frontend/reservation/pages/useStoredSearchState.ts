import {
  BoatSpaceAmenity,
  BoatSpaceType,
  boatSpaceTypes,
  BoatType,
  HarborId,
  harbors
} from 'citizen-frontend/shared/types'
import { StateOf } from 'lib-common/form/types'
import useLocalStorage from 'lib-common/utils/useLocalStorage'

import {
  SearchForm,
  SearchSpaceParamsForm
} from './chooseBoatSpace/formDefinitions'

export type StoredSearchState = {
  branch: BoatSpaceType
  Slip: BranchSearchState
  Trailer: BranchSearchState
  Winter: BranchSearchState
  Storage: BranchSearchState
}

export type BranchSearchState = {
  boatType: BoatType
  width?: number
  length?: number
  amenities: BoatSpaceAmenity[]
  harbors: HarborId[]
}

function useStoredSearchState(): [
  StoredSearchState,
  (newState: StoredSearchState | null) => void
] {
  const [storedValue, setStoredValue] = useLocalStorage(
    'berthSearchState',
    JSON.stringify({}),
    (v): v is string => typeof v === 'string'
  )

  const state = JSON.parse(storedValue) as StoredSearchState

  const setState = (newState: StoredSearchState | null) => {
    setStoredValue(JSON.stringify(newState))
  }

  return [state, setState]
}

export default useStoredSearchState

export function transformFromStateToStoredState(
  state: StateOf<SearchForm>
): StoredSearchState {
  const branch = state.boatSpaceType.domValue as BoatSpaceType

  return boatSpaceTypes.reduce(
    (acc, key) => ({
      ...acc,
      [key]: convertSpaceStateToBranchSearchState(
        branch === key
          ? state.boatSpaceUnionForm.state
          : state.boatSpaceUnionCache[key]
      )
    }),
    { branch }
  ) as StoredSearchState
}

function convertSpaceStateToBranchSearchState(
  spaceState: StateOf<SearchSpaceParamsForm>
): BranchSearchState {
  const amenities = spaceState.storageAmenity.domValue
    ? [spaceState.storageAmenity.domValue]
    : spaceState.amenities.domValues
  return {
    boatType: spaceState.boatType.domValue as BoatType,
    width: parseFloat(spaceState.width) || undefined,
    length: parseFloat(spaceState.length) || undefined,
    amenities: amenities as BoatSpaceAmenity[],
    harbors: harbors
      .filter((h) => {
        return spaceState.harbor.domValues.includes(h)
      })
      .map((h) => h)
  }
}
