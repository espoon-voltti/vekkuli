import Section from 'lib-components/dom/Section'
import React, { useContext } from 'react'

import { Result } from 'lib-common/api'

import { AuthContext, User } from '../auth/state'

import Header from './sections/Header'
import CitizenInformation from './sections/citizenInformation'
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
  if (user.isLoading) {
    return <div>Loading...</div>
  }

  if (user.isFailure) {
    return <div>Error...</div>
  }
  const currentUser = user.value
  if (!currentUser) {
    return null
  }
  return (
    <Section>
      <Header user={currentUser} />
      <CitizenInformation user={currentUser} />
      <Reservations />
    </Section>
  )
})
