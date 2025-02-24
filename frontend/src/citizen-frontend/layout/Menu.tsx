import { Dropdown } from 'lib-components/dom'
import React, { useContext } from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ChevronDown } from 'lib-icons'

import { AuthContext, User } from '../auth/state'
import { getLoginUri, getLogoutUri } from '../config'

export default React.memo(function Menu() {
  const i18n = useTranslation()
  const { user } = useContext(AuthContext)
  const currentUser = user.getOrElse(undefined)

  return currentUser === undefined ? (
    <a data-testid="loginButton" className="link" href={getLoginUri()}>
      {i18n.header.login}
    </a>
  ) : (
    <UserMenu user={currentUser} />
  )
})

const UserMenu = React.memo(function UserMenu({ user }: { user: User }) {
  const i18n = useTranslation()
  return (
    <Dropdown id="user-menu">
      {{
        label: (
          <>
            <span className="is-primary-color">
              {user.firstName} {user.lastName}
            </span>
            <span className="icon is-small">
              <ChevronDown />
            </span>
          </>
        ),
        menuItems: (
          <a href={getLogoutUri()} role="menuitem" className="dropdown-item">
            {i18n.header.logout}
          </a>
        )
      }}
    </Dropdown>
  )
})
