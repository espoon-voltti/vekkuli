import { Loader } from 'lib-components/Loader'
import { MainSection } from 'lib-components/dom'
import React, { useContext } from 'react'

import { AuthContext, User } from 'citizen-frontend/auth/state'
import Boats from 'citizen-frontend/components/boat/Boats'
import { Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import useRouteParams from '../../lib-common/useRouteParams'
import {
  organizationActiveReservationsQuery,
  organizationExpiredReservationsQuery
} from '../citizen/queries'
import { organizationBoatsQuery } from '../shared/queries'

import OrganizationInformation from './organizationInformation/OrganizationInformation'
import {
  citizenOrganizationQuery,
  updateOrganizationBoatMutation
} from './queries'
import ExpiredReservations from './reservations/ExpiredReservations'
import Reservations from './reservations/Reservations'

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
  const { organizationId } = useRouteParams(['organizationId'])

  const activeReservations = useQueryResult(
    organizationActiveReservationsQuery(organizationId)
  )
  const expiredReservations = useQueryResult(
    organizationExpiredReservationsQuery(organizationId)
  )
  const boats = useQueryResult(organizationBoatsQuery(organizationId))

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
          boats
        ) => {
          const organization = loadedOrganizations.find(
            (org) => org.id === organizationId
          )
          return (
            currentUser &&
            organization && (
              <>
                <OrganizationInformation organization={organization} />
                <Reservations reservations={loadedActiveReservations} />
                <Boats
                  boats={boats}
                  activeReservations={loadedActiveReservations}
                  updateMutation={updateOrganizationBoatMutation}
                />
                <ExpiredReservations reservations={loadedExpiredReservations} />
              </>
            )
          )
        }}
      </Loader>
    </MainSection>
  )
})
