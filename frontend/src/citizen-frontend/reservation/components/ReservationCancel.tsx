import { Button, Column, Columns } from 'lib-components/dom'
import { GoBackLink } from 'lib-components/links'
import Modal from 'lib-components/modal/Modal'
import React from 'react'
import { useNavigate } from 'react-router'

import { useMutation } from 'lib-common/query'

import { cancelReservationMutation } from '../queries'

export default React.memo(function ReservationCancel({
  reservationId,
  type,
  children
}: {
  reservationId: number
  type: 'link' | 'button'
  children: React.ReactNode
}) {
  const [modalOpen, setModalOpen] = React.useState(false)

  const navigate = useNavigate()
  const { mutateAsync: cancelReservation, isPending } = useMutation(
    cancelReservationMutation
  )

  const onReservationCancel = () => {
    cancelReservation(reservationId)
      .then(() => {
        return navigate('/kuntalainen/venepaikka')
      })
      .catch((error) => {
        console.error('Error cancelling reservation', error)
      })
  }

  const buttons = [
    {
      label: 'Peruuta',
      loading: isPending
    },
    {
      label: 'Jatka',
      type: 'primary' as const,
      loading: isPending,
      action: () => {
        return onReservationCancel()
      }
    }
  ]

  const button =
    type === 'link' ? (
      <GoBackLink action={() => setModalOpen(true)}>{children}</GoBackLink>
    ) : (
      <Button action={() => setModalOpen(true)}>{children}</Button>
    )

  return (
    <>
      {button}
      {modalOpen && (
        <Modal
          close={() => setModalOpen(false)}
          buttons={buttons}
          data-testid="confirm-cancel-reservation-modal"
        >
          <Columns isMultiline>
            <Column>
              <p>
                Olet poistumassa varauslomakkeelta. Huomioi, että paikkavarausta
                tai syötettyjä tietoja ei tallenneta.
              </p>
            </Column>
          </Columns>
        </Modal>
      )}
    </>
  )
})
