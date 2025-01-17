import { Button, Column, Columns } from 'lib-components/dom'
import React, { useEffect } from 'react'
import { useNavigate } from 'react-router'

import { SwitchableReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import { useMutation } from 'lib-common/query'

import BoatSpaceInformation from '../../../components/BoatSpaceInformation'
import { startSwitchSpaceMutation } from '../queries'

import { mapErrorCode, useReserveActionContext } from './state'

export type SwitchReservationProps = {
  reservation: SwitchableReservation
}

export default React.memo(function SwitchReservation({
  reservation
}: SwitchReservationProps) {
  const i18n = useTranslation()
  const { targetSpaceId, isLoading, setIsLoading, setError } =
    useReserveActionContext()
  const { mutateAsync: switchPlace, isPending } = useMutation(
    startSwitchSpaceMutation
  )

  useEffect(() => {
    setIsLoading(isPending)
  }, [setIsLoading, isPending])

  const navigate = useNavigate()
  const onSwitch = () => {
    // eslint-disable-next-line no-console
    console.log(`switching ${reservation.id} to ${targetSpaceId}`)
    switchPlace({
      reservationId: reservation.id,
      spaceId: targetSpaceId
    })
      .then((response) => {
        console.info('switch place response', response)
        return navigate('/kuntalainen/venepaikka/vaihda')
      })
      .catch((error) => {
        const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
        console.error(errorCode)
        const errorType = mapErrorCode(errorCode)
        setError(errorType)
      })
  }

  return (
    <Column isFull>
      <Columns>
        <Column isHalf>
          <BoatSpaceInformation boatSpace={reservation.boatSpace} />
        </Column>
        <Column isNarrow>
          <Button
            type="primary"
            data-testid={`switch-button-${reservation.id}`}
            action={onSwitch}
            loading={isLoading}
          >
            {i18n.reservation.searchPage.modal.switchCurrentPlace}
          </Button>
        </Column>
      </Columns>
    </Column>
  )
})
