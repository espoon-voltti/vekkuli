// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { useQueryClient } from '@tanstack/react-query'
import React, { createContext, ReactNode, useCallback, useMemo } from 'react'

import { Loading, Result } from 'lib-common/api'
import { useQueryResult } from 'lib-common/query'

import { BoatSpaceReservation } from '../api-types/reservation'

import { unfinishedReservationQuery } from './queries'

export interface Reservation extends BoatSpaceReservation {}

type ReservationState = {
  reservation: Result<Reservation>
  refreshReservationStatus: () => void
}

const defaultState: ReservationState = {
  reservation: Loading.of(),
  refreshReservationStatus: () => undefined
}

export const ReservationStateContext =
  createContext<ReservationState>(defaultState)

export const ReservationStateContextProvider = React.memo(
  function ReservationStateContextProvider({
    children
  }: {
    children: ReactNode
  }) {
    const reservationStatus = useQueryResult(unfinishedReservationQuery())
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
        reservation: reservationStatus,
        refreshReservationStatus
      }),
      [reservationStatus, refreshReservationStatus]
    )
    return (
      <ReservationStateContext.Provider value={value}>
        {children}
      </ReservationStateContext.Provider>
    )
  }
)
