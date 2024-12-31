import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ErrorGeneric } from 'lib-icons'

export type ErrorCode = 'MAX_RESERVATIONS' | 'SERVER_ERROR' | 'NOT_POSSIBLE'

export type ErrorModalProps = {
  close: () => void
  error: ErrorCode
}

export default React.memo(function ErrorModal({
  close,
  error
}: ErrorModalProps) {
  const i18n = useTranslation()
  const buttons = [
    {
      label: 'Ok',
      type: 'primary' as const
    }
  ]

  return (
    <Modal close={close} buttons={buttons} buttonsCentered>
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <ErrorGeneric />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            {i18n.reservation.errors.startReservation.title}
          </h2>
          <p>{i18n.reservation.errors.startReservation[error]}</p>
        </Column>
      </Columns>
    </Modal>
  )
})
