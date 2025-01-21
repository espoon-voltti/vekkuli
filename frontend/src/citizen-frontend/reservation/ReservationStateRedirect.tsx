import React, { useContext, useState } from 'react'

import { ReservationStateContext } from './state'

interface Props {
  children?: React.ReactNode
}

export default React.memo(function ReservationStateRedirect({
  children
}: Props) {
  const { reservation } = useContext(ReservationStateContext)
  const [redirectCheckDone, setRedirectCheckDone] = useState(false)

  if (redirectCheckDone) {
    return children
  }

  return reservation.mapAll({
    loading: () => null,
    failure: () => {
      setRedirectCheckDone(true)
      equalOrRedirect('/kuntalainen/venepaikka')
      return children
    },
    success: (reservation, isReloading) => {
      if (isReloading) {
        return null
      }

      setRedirectCheckDone(true)
      switch (reservation.status) {
        case 'Info':
          switch (reservation.creationType) {
            case 'Renew':
            case 'New':
              equalOrRedirect('/kuntalainen/venepaikka/varaa')
              break
            case 'Switch':
              equalOrRedirect('/kuntalainen/venepaikka/vaihda')
              break
          }
          break
        case 'Payment':
          equalOrRedirect('/kuntalainen/venepaikka/maksa')
          break
        case 'Confirmed':
          equalOrRedirect(`/kuntalainen/venepaikka/vahvistus/${reservation.id}`)
          break
      }

      return children
    }
  })
})

function equalOrRedirect(uri: string) {
  if (window.location.pathname !== uri) {
    window.location.replace(uri)
  }
  return null
}
