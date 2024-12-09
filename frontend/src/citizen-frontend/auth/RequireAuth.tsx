// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import React, { useContext } from 'react'

import { getLoginUri } from '../config'

import { AuthContext } from './state'

interface Props {
  children?: React.ReactNode
}

export default React.memo(function RequireAuth({ children }: Props) {
  const { isLoggedIn } = useContext(AuthContext)
  if (isLoggedIn.isLoading) return null
  return isLoggedIn.getOrElse(false) ? (
    <>{children}</>
  ) : (
    refreshRedirect(getLoginUri())
  )
})

function refreshRedirect(uri: string) {
  window.location.replace(uri)
  return null
}
