import { Loader } from 'lib-components/Loader'
import { Column, Columns, Container, MainSection } from 'lib-components/dom'
import React, { useContext, useState } from 'react'

import { AuthContext } from 'citizen-frontend/auth/state'
import { useTranslation } from 'citizen-frontend/localization'
import { useForm } from 'lib-common/form/hooks'
import { useQueryResult } from 'lib-common/query'
import MapImage from 'lib-customizations/vekkuli/assets/map-of-locations.png'

import { SwitchReservationInformation } from 'citizen-frontend/api-types/reservation'
import StepIndicator from '../../StepIndicator'
import { InfoBox } from '../../components/InfoBox'
import { ReservationStateContext } from '../../state'
import useStoredSearchState from '../useStoredSearchState'

import LoginBeforeReservingModal from './LoginBeforeReservingModal'
import ReservationSeasons from './ReservationSeasons'
import ReserveAction from './ReserveAction/ReserveAction'
import { ReserveActionProvider } from './ReserveAction/state'
import SearchFilters from './SearchFilters'
import SearchResult from './SearchResults'
import {
  initialFormState,
  SearchFormBranches,
  searchFreeSpacesForm
} from './formDefinitions'
import { freeSpacesQuery } from './queries'

type SearchPageProps = {
  switchInfo?: SwitchReservationInformation | undefined
}

export default React.memo(function SearchPage({ switchInfo }: SearchPageProps) {
  const i18n = useTranslation()

  const { isLoggedIn } = useContext(AuthContext)
  const userLoggedIn = isLoggedIn.getOrElse(false)
  const { reservation: unfinishedReservation } = useContext(
    ReservationStateContext
  )

  const [searchState, setSearchState] = useStoredSearchState()
  const [selectedBoatSpace, setSelectedBoatSpace] = useState<
    number | undefined
  >(undefined)

  const bind = useForm(
    searchFreeSpacesForm,
    () => initialFormState(searchState, switchInfo),
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

  const onReserveButtonPress = (spaceId: number) => {
    setSearchState({
      width: bind.value().width.toString(),
      length: bind.value().length.toString(),
      amenities: bind.value().amenities,
      boatType: bind.value().boatType,
      harbor: bind.value().harbor,
      spaceType: bind.state.boatSpaceType.domValue
    })
    return userLoggedIn
      ? setSelectedBoatSpace(spaceId)
      : setIsLoginModalOpen(true)
  }

  return (
    <Loader results={[unfinishedReservation]} allowFailure>
      {(reservation) =>
        !reservation && (
          <>
            <MainSection ariaLabel={i18n.reservation.steps.chooseBoatSpace}>
              <StepIndicator step="chooseBoatSpace" />
              <Container>
                <h2>{i18n.reservation.searchPage.title}</h2>
                <ReservationSeasons />
                {!!switchInfo && (
                  <InfoBox
                    text={i18n.reservation.formPage.info.switch}
                    fullWidth
                  />
                )}
                <Columns>
                  <Column isTwoFifths>
                    <SearchFilters bind={bind} />
                    <div className="mt-xl">
                      <img
                        src={MapImage}
                        alt={i18n.reservation.searchPage.image.harbors.altText}
                      />
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
            {selectedBoatSpace !== undefined && (
              <ReserveActionProvider
                spaceId={selectedBoatSpace}
                switchInfo={switchInfo}
                onClose={() => setSelectedBoatSpace(undefined)}
              >
                <ReserveAction />
              </ReserveActionProvider>
            )}
          </>
        )
      }
    </Loader>
  )
})
