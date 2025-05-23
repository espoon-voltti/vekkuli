import React, { useContext, useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router'

import { BoatSpaceReservation } from '../api-types/reservation'

import { Reservation, ReservationStateContext } from './state'

interface Props {
  children?: React.ReactNode
}

export default React.memo(function ReservationStateRedirect({
  children
}: Props) {
  const { reservation } = useContext(ReservationStateContext)
  const navigate = useNavigate()
  const { pathname } = useLocation()
  const [done, setDone] = useState(false)

  useEffect(() => {
    if (done) {
      return
    }

    reservation.mapAll({
      loading: () => null,
      failure: () => {
        setDone(true)
        equalOrNavigate(getExpectedPath(null, pathname), pathname, navigate)
      },
      success: (reservation) => {
        setDone(true)
        equalOrNavigate(
          getExpectedPath(reservation, pathname),
          pathname,
          navigate
        )
      }
    })
  }, [done, reservation, pathname, navigate])

  return reservation.mapAll({
    loading: () => null,
    failure: () => children,
    success: (_reservation, isReloading) => (isReloading ? null : children)
  })
})

function equalOrNavigate(
  expected: string | null,
  current: string,
  navigate: ReturnType<typeof useNavigate>
) {
  if (expected !== null && current !== expected) {
    Promise.resolve(navigate(expected)).catch(() => {
      console.error(
        'failed to redirect to a page matching the reservation state'
      )
    })
  }
}

function getExpectedPath(
  reservation: Reservation | null,
  current: string
): string | null {
  if (reservation !== null) {
    return getExpectedPathForReservation(reservation.reservation)
  }

  if (current.startsWith('/kuntalainen/venepaikka/vahvistus')) {
    return current
  }

  if (current.startsWith('/kuntalainen/venepaikka/vaihda/')) {
    return current
  }

  return '/kuntalainen/venepaikka'
}

export function getExpectedPathForReservation(
  reservation: BoatSpaceReservation
): string | null {
  switch (reservation.status) {
    case 'Info':
      switch (reservation.creationType) {
        case 'Renewal':
          return '/kuntalainen/venepaikka/jatka'
        case 'New':
          return '/kuntalainen/venepaikka/varaa'
        case 'Switch':
          return '/kuntalainen/venepaikka/vaihda'
      }
      break
    case 'Payment':
      return '/kuntalainen/venepaikka/maksa'
    case 'Confirmed':
      return `/kuntalainen/venepaikka/vahvistus/${reservation.id}`
  }

  return null
}
