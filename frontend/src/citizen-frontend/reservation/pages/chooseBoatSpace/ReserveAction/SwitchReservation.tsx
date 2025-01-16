import { Button, Column, Columns } from 'lib-components/dom'
import React, { useEffect } from 'react'
import { useNavigate } from 'react-router'

import { SwitchableReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import { useMutation } from 'lib-common/query'

import BoatSpaceInformation from '../../../components/BoatSpaceInformation'
import { startSwitchSpaceMutation } from '../queries'

export type SwitchReservationProps = {
  reservation: SwitchableReservation
  reserveSpaceId: number
  setLoading: (loading: boolean) => void
}

export default React.memo(function SwitchReservation({
  reservation,
  reserveSpaceId,
  setLoading
}: SwitchReservationProps) {
  const i18n = useTranslation()
  const { mutateAsync: switchPlace, isPending } = useMutation(
    startSwitchSpaceMutation
  )
  const navigate = useNavigate()
  const onSwitch = () => {
    // eslint-disable-next-line no-console
    console.log(`switching ${reservation.id} to ${reserveSpaceId}`)
    switchPlace({
      reservationId: reservation.id,
      spaceId: reserveSpaceId
    })
      .then((response) => {
        console.info('switch place response', response)
        return navigate('/kuntalainen/venepaikka/vaihda')
      })
      .catch((error) => {
        const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
        console.error(errorCode)
        //const errorType = mapErrorCode(errorCode)
        //setReserveError(errorType)
      })
  }

  useEffect(() => {
    setLoading(isPending)
  }, [setLoading, isPending])

  return (
    <Column isFull>
      <Columns>
        <Column isHalf>
          <BoatSpaceInformation boatSpace={reservation.boatSpace} />
        </Column>
        <Column isNarrow>
          <Button type="primary" action={onSwitch} loading={isPending}>
            {i18n.reservation.searchPage.modal.switchCurrentPlace}
          </Button>
        </Column>
      </Columns>
    </Column>
  )
})
