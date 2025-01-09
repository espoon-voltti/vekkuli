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
    switch (currentReservation.status) {
      case 'Info':
      case 'Renewal':
        equalOrRedirect('/kuntalainen/venepaikka/varaa', pathname)
        break
      case 'Payment':
        equalOrRedirect('/kuntalainen/venepaikka/maksa', pathname)
        break
      case 'Confirmed':
        equalOrRedirect('/kuntalainen/venepaikka/vahvistus', pathname)
        break
    }
  }
  return <>{children}</>
})

function equalOrRedirect(uri: string, pathname: string) {
  if (pathname !== uri) window.location.replace(uri)
  return null
}
