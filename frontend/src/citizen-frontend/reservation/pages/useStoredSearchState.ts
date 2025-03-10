import {
  BoatSpaceAmenity,
  BoatSpaceType,
  BoatType,
  Harbor,
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
  width: number
  length: number
  amenities: BoatSpaceAmenity[]
  harbors: Harbor[]
}

function useStoredSearchState(): [
  StoredSearchState,
  (newState: StoredSearchState) => void
] {
  const [storedValue, setStoredValue] = useLocalStorage(
    'searchState',
    JSON.stringify({}),
    (v): v is string => typeof v === 'string'
  )

  const state = JSON.parse(storedValue) as StoredSearchState

  const setState = (newState: StoredSearchState) => {
    setStoredValue(JSON.stringify(newState))
  }

  return [state, setState]
}

export default useStoredSearchState

export function transformFromStateToStoredState(
  state: StateOf<SearchForm>
): StoredSearchState {
  return {
    branch: state.boatSpaceType.domValue as BoatSpaceType,
    Slip: convertSpaceStateToBranchSearchState(state.boatSpaceUnionCache.Slip),
    Trailer: convertSpaceStateToBranchSearchState(
      state.boatSpaceUnionCache.Trailer
    ),
    Winter: convertSpaceStateToBranchSearchState(
      state.boatSpaceUnionCache.Winter
    ),
    Storage: convertSpaceStateToBranchSearchState(
      state.boatSpaceUnionCache.Storage
    )
  }
}

function convertSpaceStateToBranchSearchState(
  spaceState: StateOf<SearchSpaceParamsForm>
): BranchSearchState {
  const amenities = spaceState.storageAmenity.domValue
    ? [spaceState.storageAmenity.domValue]
    : spaceState.amenities.domValues
  return {
    boatType: spaceState.boatType.domValue as BoatType,
    width: parseFloat(spaceState.width),
    length: parseFloat(spaceState.length),
    amenities: amenities as BoatSpaceAmenity[],
    harbors: harbors.filter((h) => {
      return spaceState.harbor.domValues.includes(h.value)
    })
  }
}
