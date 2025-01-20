import { Button, Column, Columns } from 'lib-components/dom'
import { GoBackLink } from 'lib-components/links'
import Modal from 'lib-components/modal/Modal'
import React from 'react'
import { useNavigate } from 'react-router'

import { useTranslation } from 'citizen-frontend/localization'
import { useMutation } from 'lib-common/query'

import { cancelReservationMutation } from '../queries'

export default React.memo(function ReservationCancel({
  reservationId,
  type,
  children,
  buttonAriaLabel
}: {
  reservationId: number
  type: 'link' | 'button'
  children: React.ReactNode
  buttonAriaLabel?: string
}) {
  const i18n = useTranslation()
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
      label: i18n.common.cancel,
      loading: isPending
    },
    {
      label: i18n.common.continue,
      type: 'primary' as const,
      loading: isPending,
      action: () => {
        return onReservationCancel()
      }
    }
  ]

  const button =
    type === 'link' ? (
      <GoBackLink action={() => setModalOpen(true)} ariaLabel={buttonAriaLabel}>
        {children}
      </GoBackLink>
    ) : (
      <Button ariaLabel={buttonAriaLabel} action={() => setModalOpen(true)}>
        {children}
      </Button>
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
              <p>{i18n.reservation.cancelConfirmation}</p>
              <p>{i18n.reservation.cancelConfirmation2}</p>
            </Column>
          </Columns>
        </Modal>
      )}
    </>
  )
})
