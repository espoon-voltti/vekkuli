import { Column, Columns, ScreenReaderOnly } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ModalButton } from 'lib-components/modal/ModalButtons'
import { ErrorGeneric } from 'lib-icons'
import { useNavigate } from 'react-router'

export type ErrorCode = 'SERVER_ERROR' | 'UNFINISHED_RESERVATION'

export type ErrorModalProps = {
  close: () => void
  error: ErrorCode
}

function getModalButtons(
  i18n: ReturnType<typeof useTranslation>,
  error: string,
  navigate: ReturnType<typeof useNavigate>
): ModalButton[] {
  let buttons: ModalButton[] = [
    {
      label: i18n.common.ok,
      type: 'secondary' as const
    }
  ]
  if (error == 'UNFINISHED_RESERVATION') {
    buttons = [
      {
        label: i18n.common.cancel,
        type: 'secondary' as const
      },
      {
        label: i18n.citizenPage.reservation.modal.goBackToReservation,
        type: 'primary',
        action: () => navigate('/kuntalainen/venepaikka')
      }
    ]
  }
  return buttons
}

export default React.memo(function ErrorModal({
  close,
  error
}: ErrorModalProps) {
  const navigate = useNavigate()
  const i18n = useTranslation()
  const buttons = getModalButtons(i18n, error, navigate)
  const title = i18n.reservation.errors.fillInformation.title
  const body = i18n.reservation.errors.fillInformation[error]

  return (
    <Modal
      close={close}
      buttons={buttons}
      buttonAlignment="center"
      data-testid="error-modal"
    >
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
