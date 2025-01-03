import { Loader } from 'lib-components/Loader'
import Section from 'lib-components/dom/Section'
import React, { useContext } from 'react'

import { AuthContext, User } from 'citizen-frontend/auth/state'
import { Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import {
  citizenActiveReservationsQuery,
  citizenExpiredReservationsQuery,
  citizenOrganizationQuery
} from './queries'
import Header from './sections/Header'
import Boats from './sections/boats/Boats'
import CitizenInformation from './sections/citizenInformation'
import ExpiredReservations from './sections/reservations/ExpiredReservations'
import Reservations from './sections/reservations/Reservations'

export default React.memo(function CitizenPage() {
  const { user } = useContext(AuthContext)
  return (
    <Section>
      <Content user={user} />
    </Section>
  )
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
    <Section>
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
              <Header user={currentUser} />
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
    </Section>
  )
})
