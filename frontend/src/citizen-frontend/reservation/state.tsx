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

import { AuthContext } from 'citizen-frontend/auth/state.js'
import { Failure, Loading, Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import { UnfinishedBoatSpaceReservation } from '../api-types/reservation'

import { unfinishedReservationQuery } from './queries'

export interface Reservation extends UnfinishedBoatSpaceReservation {}

type ReservationState = {
  reservation: Result<UnfinishedBoatSpaceReservation>
  refreshReservationStatus: () => void
}

const defaultState: ReservationState = {
  reservation: Loading.of(),
  refreshReservationStatus: () => undefined
}

const unauthenticatedReservationStatus =
  Failure.of<UnfinishedBoatSpaceReservation>({
    message: 'not logged in'
  })

export const ReservationStateContext =
  createContext<ReservationState>(defaultState)

export const ReservationStateContextProvider = React.memo(
  function ReservationStateContextProvider({
    children
  }: {
    children: ReactNode
  }) {
    const { isLoggedIn } = useContext(AuthContext)
    const isAuthenticated = isLoggedIn.getOrElse(false) === true
    const isUnauthenticated =
      !isLoggedIn.isLoading && isLoggedIn.getOrElse(false) !== true

    const reservationStatus = useQueryResult(unfinishedReservationQuery(), {
      enabled: isAuthenticated
    })
    const queryClient = useQueryClient()
    const refreshReservationStatus = useCallback(
      () =>
        queryClient.invalidateQueries({
          queryKey: unfinishedReservationQuery().queryKey
        }),
      [queryClient]
    )

    const value = useMemo(
      () => ({
        reservation: isUnauthenticated
          ? unauthenticatedReservationStatus
          : reservationStatus,
        refreshReservationStatus
      }),
      [reservationStatus, refreshReservationStatus, isUnauthenticated]
    )
    return (
      <ReservationStateContext.Provider value={value}>
        {children}
      </ReservationStateContext.Provider>
    )
  }
)
