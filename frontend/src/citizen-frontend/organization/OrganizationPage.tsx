import { Loader } from 'lib-components/Loader'
import Section from 'lib-components/dom/Section'
import React, { useContext } from 'react'

import { AuthContext, User } from 'citizen-frontend/auth/state'
import { Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import { citizenOrganizationQuery } from './queries'
import Reservations from './reservations/Reservations'
import Boats from './boats/Boats'
import ExpiredReservations from './reservations/ExpiredReservations'
import {
  organizationActiveReservationsQuery,
  organizationExpiredReservationsQuery
} from '../citizen/queries'
import OrganizationInformation from './organizationInformation/OrganizationInformation'
import { organizationBoatsQuery } from '../shared/queries'

export default function OrganizationPage() {
  const { user } = useContext(AuthContext)
  return <Content user={user} />
}

const Content = React.memo(function Content({
  user
}: {
  user: Result<User | undefined>
}) {
  const organizations = useQueryResult(citizenOrganizationQuery())
  const orgId = '8b220a43-86a0-4054-96f6-d29a5aba17e7'
  const activeReservations = useQueryResult(
    organizationActiveReservationsQuery(orgId)
  )
  const expiredReservations = useQueryResult(
    organizationExpiredReservationsQuery(orgId)
  )
  const boats = useQueryResult(organizationBoatsQuery(orgId))

  return (
    <Section>
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
          boats
        ) =>
          currentUser && (
            <>
              <OrganizationInformation organization={loadedOrganizations[0]} />
              <Reservations reservations={loadedActiveReservations} />
              <Boats boats={boats} reservations={loadedActiveReservations} />
              <ExpiredReservations reservations={loadedExpiredReservations} />
            </>
          )
        }
      </Loader>
    </Section>
  )
})
