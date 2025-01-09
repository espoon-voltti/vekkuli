import Container from 'lib-components/dom/Container'
import GoBackLink from 'lib-components/links/GoBackLink'
import React from 'react'

import { User } from 'citizen-frontend/auth/state'

export default React.memo(function Header({ user }: { user: User }) {
  return (
    <Container isBlock>
      <GoBackLink />
      <h2>
        {user.firstName} {user.lastName}
      </h2>
    </Container>
  )
})
