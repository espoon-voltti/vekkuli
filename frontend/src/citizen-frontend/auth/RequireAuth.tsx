// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import React, { useContext } from 'react'
import { useSearchParams } from 'react-router'

import { ErrorElement } from 'citizen-frontend/errors/ErrorPage.js'

import { getLoginUri } from '../config'

import { AuthContext } from './state'

interface Props {
  children?: React.ReactNode
}

export default React.memo(function RequireAuth({ children }: Props) {
  const { isLoggedIn } = useContext(AuthContext)
  const [searchParams] = useSearchParams()

  return isLoggedIn.mapAll<React.ReactNode>({
    loading: () => null,
    failure: () => <LoginFailed />,
    success: (loggedIn) => {
      if (loggedIn) {
        return children
      }

      if (searchParams.get('loginError')) {
        return <LoginFailed />
      }

      return refreshRedirect(getLoginUri())
    }
  })
})

function refreshRedirect(uri: string) {
  window.location.replace(uri)
  return null
}

function LoginFailed() {
  return (
    <div className="container" data-testid="login-error">
      <ErrorElement statusCode={401} />
    </div>
  )
}
