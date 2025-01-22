import React, { useContext } from 'react'
import { useLocation } from 'react-router'

import { ReservationStateContext } from './state'

interface Props {
  children?: React.ReactNode
}

export default React.memo(function ReservationStateRedirect({
  children
}: Props) {
  const { reservation } = useContext(ReservationStateContext)
  const { pathname } = useLocation()
  const currentReservation = reservation.getOrElse(undefined)
  if (reservation.isLoading) return null
  if (reservation.isFailure || currentReservation === undefined) {
    equalOrRedirect('/kuntalainen/venepaikka', pathname)
  } else if (reservation.isSuccess) {
    switch (currentReservation.reservation.status) {
      case 'Info':
        switch (currentReservation.reservation.creationType) {
          case 'Renew':
          case 'New':
            equalOrRedirect('/kuntalainen/venepaikka/varaa', pathname)
            break
          case 'Switch':
            equalOrRedirect('/kuntalainen/venepaikka/vaihda', pathname)
            break
        }
        break
      case 'Payment':
        equalOrRedirect('/kuntalainen/venepaikka/maksa', pathname)
        break
      case 'Confirmed':
        equalOrRedirect(
          `/kuntalainen/venepaikka/vahvistus/${currentReservation.reservation.id}`,
          pathname
        )
        break
    }
  }
  return <>{children}</>
})

function equalOrRedirect(uri: string, pathname: string) {
  if (pathname !== uri) window.location.replace(uri)
  return null
}
