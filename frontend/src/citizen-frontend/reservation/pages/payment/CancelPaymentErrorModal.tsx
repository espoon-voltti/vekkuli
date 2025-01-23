import { Column, Columns, ScreenReaderOnly } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import { ModalButton } from 'lib-components/modal/ModalButtons'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ErrorGeneric } from 'lib-icons'

export type CancelPaymentErrorModalProps = {
  close: () => void
}

export default React.memo(function CancelPaymentErrorModal({
  close
}: CancelPaymentErrorModalProps) {
  const i18n = useTranslation()
  const title = i18n.reservation.errors.cancelPayment.title
  const body = i18n.reservation.errors.cancelPayment.SERVER_ERROR
  const buttons: ModalButton[] = [
    {
      label: i18n.common.ok,
      type: 'secondary' as const
    }
  ]
  return (
    <Modal
      close={close}
      buttons={buttons}
      buttonsCentered
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
