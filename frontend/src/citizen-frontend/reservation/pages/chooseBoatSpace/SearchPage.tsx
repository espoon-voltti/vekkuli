import { Loader } from 'lib-components/Loader'
import { Column, Columns, Container, MainSection } from 'lib-components/dom'
import React, { useContext, useState } from 'react'
import { useNavigate } from 'react-router'

import { PlaceWithSpaces } from 'citizen-frontend/api-types/free-spaces'
import { AuthContext } from 'citizen-frontend/auth/state'
import { citizenActiveReservationsQuery } from 'citizen-frontend/citizen/queries'
import SwitchModal from 'citizen-frontend/citizen/sections/reservations/SwitchModal'
import { useTranslation } from 'citizen-frontend/localization'
import { useForm } from 'lib-common/form/hooks'
import { useMutation, useQueryResult } from 'lib-common/query'
import MapImage from 'lib-customizations/vekkuli/assets/map-of-locations.png'

import StepIndicator from '../../StepIndicator'
import { ReservationStateContext } from '../../state'

import ErrorModal, { ErrorCode } from './ErrorModal'
import LoginBeforeReservingModal from './LoginBeforeReservingModal'
import ReservationSeasons from './ReservationSeasons'
import SearchFilters from './SearchFilters'
import SearchResult from './SearchResults'
import {
  initialFormState,
  SearchFormBranches,
  searchFreeSpacesForm
} from './formDefinitions'
import {
  freeSpacesQuery,
  reserveSpaceMutation,
  starSwitchSpaceMutation
} from './queries'
import useStoredSearchState from '../useStoredSearchState'

export default React.memo(function SearchPage() {
  const i18n = useTranslation()
  const navigate = useNavigate()
  const { isLoggedIn } = useContext(AuthContext)
  const userLoggedIn = isLoggedIn.getOrElse(false)
  const { reservation } = useContext(ReservationStateContext)

  const [searchState, setSearchState] = useStoredSearchState()

  const [selectedBoatSpace, setSelectedBoatSpace] = useState<
    PlaceWithSpaces | undefined
  >(undefined)
  const bind = useForm(
    searchFreeSpacesForm,
    () => initialFormState(i18n, searchState),
    i18n.components.validationErrors,
    {
      onUpdate: (prev, next) => {
        const prevBranch = prev.boatSpaceType.domValue as SearchFormBranches
        const nextBranch = next.boatSpaceType.domValue as SearchFormBranches
        if (prevBranch !== nextBranch) {
          return {
            ...next,
            boatSpaceUnionForm: {
              branch: nextBranch,
              state: prev.boatSpaceUnionCache[nextBranch]
            },
            boatSpaceUnionCache: {
              ...prev.boatSpaceUnionCache,
              [prevBranch]: prev.boatSpaceUnionForm.state
            }
          }
        }
        return next
      }
    }
  )

  const [isLoginModalOpen, setIsLoginModalOpen] = React.useState(false)
  const [reserveError, setReserveError] = React.useState<
    ErrorCode | undefined
  >()
  const freeSpaces = useQueryResult(
    freeSpacesQuery(bind.isValid() ? bind.value() : undefined),
    {
      enabled: bind.isValid()
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
    setSelectedBoatSpace(undefined)
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
  const { mutateAsync: switchPlace } = useMutation(starSwitchSpaceMutation)
  const onSwitch = (spaceId: number, reservationId: number) => {
    // eslint-disable-next-line no-console
    console.log(`switching ${reservationId} to ${spaceId}`)
    switchPlace({ reservationId, spaceId })
      .then((response) => {
        console.error('got response', response)
        return navigate('/kuntalainen/venepaikka/vaihda')
      })
      .catch((error) => {
        const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
        const errorType = mapErrorCode(errorCode)
        setReserveError(errorType)
      })
  }
  const selectBoatSpace = (spaceId: number) => {
    // TODO: check if reservation is reservable
  }

  const onReserveButtonPress = (spaceId: number) => {
    setSearchState({
      width: bind.value().width.toString(),
      length: bind.value().length.toString(),
      amenities: bind.value().amenities,
      boatType: bind.value().boatType,
      harbor: bind.value().harbor,
      spaceType: bind.state.boatSpaceType.domValue
    })
    return userLoggedIn ? selectBoatSpace(spaceId) : setIsLoginModalOpen(true)
  }

  return (
    <Loader results={[reservation]} allowFailure>
      {(reservation) =>
        !reservation && (
          <>
            <MainSection>
              <StepIndicator step="chooseBoatSpace" />
              <Container>
                <h2>Espoon kaupungin venepaikkojen vuokraus</h2>
                <ReservationSeasons />
                <Columns>
                  <Column isTwoFifths>
                    <SearchFilters bind={bind} />
                    <div className="mt-xl">
                      <img src={MapImage} alt="Espoon venesatamat" />
                    </div>
                  </Column>
                  <Column>
                    <SearchResult
                      placesWithSpaces={searchResult.places}
                      count={searchResult.count}
                      showInfoBox={!bind.isValid()}
                      onReserveSpace={onReserveButtonPress}
                    />
                  </Column>
                </Columns>
              </Container>
            </MainSection>
            {isLoginModalOpen && (
              <LoginBeforeReservingModal
                close={() => setIsLoginModalOpen(false)}
              />
            )}
            {!!reserveError && (
              <ErrorModal
                error={reserveError}
                close={() => setReserveError(undefined)}
              />
            )}
            {selectedBoatSpace !== undefined && (
              <SwitchModal
                close={() => setSelectedBoatSpace(undefined)}
                currentSpace={selectedBoatSpace}
                onSwitch={onSwitch}
                onReserveSpace={onReserveSpace}
                reservations={fetchedUserReservations}
              />
            )}
          </>
        )
      }
    </Loader>
  )
})

const mapErrorCode = (errorCode: string): ErrorCode => {
  switch (errorCode) {
    case 'MaxReservations':
      return 'MAX_RESERVATIONS'
    case 'NotPossible':
      return 'NOT_POSSIBLE'
    default:
      return 'SERVER_ERROR'
  }
}
