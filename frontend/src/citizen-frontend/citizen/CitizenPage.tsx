import { Loader } from 'lib-components/Loader'
import { MainSection } from 'lib-components/dom'
import React, { useContext } from 'react'

import { AuthContext, User } from 'citizen-frontend/auth/state'
import { Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import {
  citizenActiveReservationsQuery,
  citizenExpiredReservationsQuery,
  citizenOrganizationQuery
} from './queries'
import Boats from './sections/boats/Boats'
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
  const expiredReservations = useQueryResult(citizenExpiredReservationsQuery())

  return (
    <MainSection>
      <Loader
        results={[user, organizations, activeReservations, expiredReservations]}
      >
        {(
          currentUser,
          loadedOrganizations,
          loadedActiveReservations,
          loadedExpiredReservations
        ) =>
          currentUser && (
            <>
              <CitizenInformation
                user={currentUser}
                organizations={loadedOrganizations}
              />
              <Reservations reservations={loadedActiveReservations} />
              <Boats />
              <ExpiredReservations reservations={loadedExpiredReservations} />
            </>
          )
        }
      </Loader>
    </MainSection>
  )
})
