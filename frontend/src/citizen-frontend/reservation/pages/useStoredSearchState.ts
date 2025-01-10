import useLocalStorage from 'lib-common/utils/useLocalStorage'

export type StoredSearchState = {
  width?: string
  length?: string
  amenities?: string[]
  spaceType?: string
  boatType?: string
  harbor?: string[]
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
