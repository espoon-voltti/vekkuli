import classNames from 'classnames'
import React, { useContext } from 'react'

import { ChevronDown } from 'lib-icons'

import { AuthContext, User } from '../auth/state'
import { getLoginUri, getLogoutUri } from '../config'

export default React.memo(function Menu() {
  const { user } = useContext(AuthContext)
  const currentUser = user.getOrElse(undefined)

  return currentUser === undefined ? (
    <a id="loginButton" className="link" href={getLoginUri()}>
      Kirjaudu sisään
    </a>
  ) : (
    <UserMenu user={currentUser} />
  )
})

const UserMenu = React.memo(function UserMenu({ user }: { user: User }) {
  const [isOpen, setIsOpen] = React.useState(false)
  const toggleOpen = () => setIsOpen(!isOpen)
  return (
    <div>
      <div className={classNames('dropdown', { 'is-active': isOpen })}>
        <div className="dropdown-trigger" onClick={toggleOpen}>
          <a
            aria-haspopup="true"
            aria-controls="dropdown-menu"
            className="is-icon-link is-reverse"
          >
            <span>
              {user.firstName} {user.lastName}
            </span>
            <span className="icon is-small">
              <ChevronDown />
            </span>
          </a>
        </div>
        {!isOpen ? null : (
          <div className="dropdown-menu" id="dropdown-menu" role="menu">
            <div className="dropdown-content">
              <a href={getLogoutUri()} className="dropdown-item">
                Kirjaudu ulos
              </a>
            </div>
          </div>
        )}
      </div>
    </div>
  )
})
