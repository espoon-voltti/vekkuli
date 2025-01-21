import React, { useContext, useEffect } from 'react'
import { useLocation, useNavigate } from 'react-router'

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

  useEffect(() => {
    reservation.mapAll({
      loading: () => null,
      failure: () => {
        equalOrNavigate(getExpectedPath(null, pathname), pathname, navigate)
      },
      success: (reservation, isReloading) => {
        if (!isReloading) {
          equalOrNavigate(
            getExpectedPath(reservation, pathname),
            pathname,
            navigate
          )
        }
      }
    })
  }, [reservation, pathname, navigate])

  return reservation.mapAll({
    loading: () => null,
    failure: () => children,
    success: (_reservation, isReloading) => (isReloading ? null : children)
  })
})

function equalOrNavigate(
  uri: string,
  pathname: string,
  navigate: ReturnType<typeof useNavigate>
) {
  if (pathname !== uri) {
    Promise.resolve(navigate(uri)).catch(() => {
      console.error(
        'failed to redirect to a page matching the reservation state'
      )
    })
  }
}

function getExpectedPath(
  reservation: Reservation | null,
  current: string
): string {
  if (reservation !== null) {
    return getExpectedPathForReservation(reservation)
  }

  if (current.startsWith('/kuntalainen/venepaikka/vahvistus')) {
    return current
  }

  return '/kuntalainen/venepaikka'
}

function getExpectedPathForReservation(reservation: Reservation): string {
  switch (reservation.status) {
    case 'Info':
      switch (reservation.creationType) {
        case 'Renew':
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
}
