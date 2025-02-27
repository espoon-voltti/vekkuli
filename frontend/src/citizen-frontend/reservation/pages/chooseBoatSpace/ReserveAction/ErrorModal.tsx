import { Column, Columns, ScreenReaderOnly } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ErrorGeneric } from 'lib-icons'

export type ErrorCode =
  | 'MAX_RESERVATIONS'
  | 'SERVER_ERROR'
  | 'NOT_POSSIBLE'
  | 'NOT_AVAILABLE'

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

  const title = i18n.reservation.errors.startReservation.title
  const body = i18n.reservation.errors.startReservation[error]

  return (
    <Modal close={close} buttons={buttons} buttonsCentered>
      <ScreenReaderOnly>
        {title}
        {'. '}
        {body}
      </ScreenReaderOnly>
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <ErrorGeneric />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">{title}</h2>
        </Column>
        <Column isFull textCentered>
          <p>{body}</p>
        </Column>
      </Columns>
    </Modal>
  )
})
