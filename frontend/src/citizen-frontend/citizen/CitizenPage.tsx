import { Loader } from 'lib-components/Loader'
import { MainSection } from 'lib-components/dom'
import React, { useContext } from 'react'

import { AuthContext, User } from 'citizen-frontend/auth/state'
import Boats from 'citizen-frontend/components/boat-list/Boats'
import { citizenBoatsQuery } from 'citizen-frontend/shared/queries'
import { Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import {
  citizenActiveReservationsQuery,
  citizenExpiredReservationsQuery,
  citizenOrganizationQuery,
  updateCitizenBoatMutation
} from './queries'
import CitizenInformation from './sections/citizenInformation'
import ExpiredReservations from './sections/reservations/ExpiredReservations'
import Reservations from './sections/reservations/Reservations'

export default React.memo(function CitizenPage() {
  const { user } = useContext(AuthContext)
  return <Content user={user} />
})

const Content = React.memo(function Content({
  user
}: {
  user: Result<User | undefined>
}) {
  const organizations = useQueryResult(citizenOrganizationQuery())
  const activeReservations = useQueryResult(citizenActiveReservationsQuery())
  const boats = useQueryResult(citizenBoatsQuery())
  const expiredReservations = useQueryResult(citizenExpiredReservationsQuery())

  return (
    <MainSection>
      <Loader
        results={[
          user,
          organizations,
          activeReservations,
          expiredReservations,
          boats
        ]}
      >
        {(
          currentUser,
          loadedOrganizations,
          loadedActiveReservations,
          loadedExpiredReservations,
          loadedBoats
        ) =>
          currentUser && (
            <>
              <CitizenInformation
                user={currentUser}
                organizations={loadedOrganizations}
              />
              <Reservations reservations={loadedActiveReservations} />
              <Boats
                boats={loadedBoats}
                activeReservations={loadedActiveReservations}
                updateMutation={updateCitizenBoatMutation}
              />
              <ExpiredReservations reservations={loadedExpiredReservations} />
            </>
          )
        }
      </Loader>
    </MainSection>
  )
})
