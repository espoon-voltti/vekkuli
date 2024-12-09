// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { useQueryClient } from '@tanstack/react-query'
import React, {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useMemo
} from 'react'

import { Loading, Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import { CitizenUserDetails } from '../api-types/auth'

import { authStatusQuery } from './queries'

export interface User extends CitizenUserDetails {}

type AuthState = {
  apiVersion: string | undefined
  user: Result<User | undefined>
  isLoggedIn: Result<boolean | undefined>
  refreshAuthStatus: () => void
}

const defaultState: AuthState = {
  apiVersion: undefined,
  user: Loading.of(),
  isLoggedIn: Loading.of(),
  refreshAuthStatus: () => undefined
}

export const AuthContext = createContext<AuthState>(defaultState)

export const AuthContextProvider = React.memo(function AuthContextProvider({
  children
}: {
  children: ReactNode
}) {
  const authStatus = useQueryResult(authStatusQuery())
  const queryClient = useQueryClient()
  const refreshAuthStatus = useCallback(
    () =>
      queryClient.invalidateQueries({
        queryKey: authStatusQuery().queryKey
      }),
    [queryClient]
  )

  const value = useMemo(
    () => ({
      apiVersion: authStatus.map((a) => a.apiVersion).getOrElse(undefined),
      user: authStatus.map((a) =>
        a.loggedIn
          ? {
              ...a.user.details
            }
          : undefined
      ),
      isLoggedIn: authStatus.map((a) => (a.loggedIn ? true : undefined)),
      refreshAuthStatus
    }),
    [authStatus, refreshAuthStatus]
  )
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
})

export const useUser = (): User | undefined => {
  const authContext = useContext(AuthContext)
  return authContext.user.getOrElse(undefined)
}
