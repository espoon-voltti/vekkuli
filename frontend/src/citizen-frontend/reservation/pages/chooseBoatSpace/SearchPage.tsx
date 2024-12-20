import React, { useCallback, useContext, useEffect, useState } from 'react'
import { useNavigate } from 'react-router'

import { useForm, useFormFields } from 'lib-common/form/hooks'
import { StateOf } from 'lib-common/form/types'
import { useMutation, useQueryResult } from 'lib-common/query'
import MapImage from 'lib-customizations/vekkuli/assets/map-of-locations.png'

import { AuthContext } from '../../../auth/state'
import { useTranslation } from '../../../localization'
import StepIndicator from '../../StepIndicator'

import ErrorModal, { ErrorCode } from './ErrorModal'
import LoginBeforeReservingModal from './LoginBeforeReservingModal'
import ReservationSeasons from './ReservationSeasons'
import SearchFilters from './SearchFilters'
import SearchResult from './SearchResults'
import {
  initialFormState,
  initialUnionFormState,
  SearchFormBranches,
  SearchFormUnion,
  searchFreeSpacesForm
} from './formDefinitions'
import { freeSpacesQuery, reserveSpaceMutation } from './queries'

export default React.memo(function SearchPage() {
  const i18n = useTranslation()
  const navigate = useNavigate()
  const { isLoggedIn } = useContext(AuthContext)
  const userLoggedIn = isLoggedIn.getOrElse(false)
  const [branchStateStore, setBranchStateStore] =
    useState<Record<SearchFormBranches, StateOf<SearchFormUnion> | undefined>>()

  const form = useForm(
    searchFreeSpacesForm,
    () => initialFormState(i18n),
    i18n.components.validationErrors
  )
  const branch = form.state.boatSpaceType.domValue as SearchFormBranches

  const {
    boatSpaceUnionForm: { update }
  } = useFormFields(form)

  const setFormUnionBranch = useCallback(
    (newBranch: SearchFormBranches) => {
      update((prev) => {
        setBranchStateStore((prevStateStore) => {
          const updatedStateStore = prevStateStore || {
            Slip: undefined,
            Trailer: undefined,
            Winter: undefined,
            Storage: undefined
          }

          return {
            ...updatedStateStore,
            [branch]: { branch, state: prev.state }
          }
        })

        const newBranchState =
          branchStateStore?.[newBranch] ||
          initialUnionFormState(i18n, newBranch)

        return {
          ...newBranchState
        }
      })
    },
    [update, branchStateStore, i18n]
  )

  useEffect(() => {
    setFormUnionBranch(branch)
  }, [branch])

  const [isLoginModalOpen, setIsLoginModalOpen] = React.useState(false)
  const [reserveError, setReserveError] = React.useState<
    ErrorCode | undefined
  >()
  const freeSpaces = useQueryResult(
    freeSpacesQuery(form.isValid() ? form.value() : undefined),
    {
      enabled: form.isValid()
    }
  )

  const searchResult = freeSpaces.isSuccess
    ? {
        places: freeSpaces.value.placesWithFreeSpaces,
        count: freeSpaces.value.count
      }
    : {
        places: [],
        count: 0
      }

  const { mutateAsync: reserveSpace } = useMutation(reserveSpaceMutation)
  const onReserveSpace = (spaceId: number) => {
    reserveSpace(spaceId)
      .then((response) => {
        console.error('got response', response)
        return navigate('/kuntalainen/venepaikka/varaa')
      })
      .catch((error) => {
        const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
        const errorType = mapErrorCode(errorCode)
        setReserveError(errorType)
      })
  }
  const onReserveButtonPress = userLoggedIn
    ? onReserveSpace
    : () => setIsLoginModalOpen(true)
  return (
    <>
      <section className="section">
        <StepIndicator step="chooseBoatSpace" />
        <div className="container">
          <h2>Espoon kaupungin venepaikkojen vuokraus</h2>
          <ReservationSeasons />
          <div className="columns">
            <div className="column is-two-fifths">
              <SearchFilters bind={form} />
              <div className="mt-xl">
                <img src={MapImage} alt="Espoon venesatamat" />
              </div>
            </div>
            <div className="column">
              <SearchResult
                placesWithSpaces={searchResult.places}
                count={searchResult.count}
                showInfoBox={!form.isValid()}
                onReserveSpace={onReserveButtonPress}
              />
            </div>
          </div>
        </div>
      </section>
      {isLoginModalOpen && (
        <LoginBeforeReservingModal close={() => setIsLoginModalOpen(false)} />
      )}
      {!!reserveError && (
        <ErrorModal
          error={reserveError}
          close={() => setReserveError(undefined)}
        />
      )}
    </>
  )
})

const mapErrorCode = (errorCode: string): ErrorCode => {
  switch (errorCode) {
    case 'MaxReservations':
      return 'MAX_RESERVATIONS'
    default:
      return 'SERVER_ERROR'
  }
}
