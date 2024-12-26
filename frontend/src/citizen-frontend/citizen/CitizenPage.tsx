import { Loader } from 'lib-components/Loader'
import Section from 'lib-components/dom/Section'
import React, { useContext } from 'react'

import { Result } from 'lib-common/api'

import { AuthContext, User } from '../auth/state'

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
  return (
    <Section>
      <Loader results={[user]}>
        {(currentUser) =>
          currentUser && (
            <>
              <Header user={currentUser} />
              <CitizenInformation user={currentUser} />
              <Reservations />
              <Boats />
              <ExpiredReservations />
            </>
          )
        }
      </Loader>
    </Section>
  )
})
