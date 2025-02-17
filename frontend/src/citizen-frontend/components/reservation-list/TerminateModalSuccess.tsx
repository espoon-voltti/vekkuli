import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Success } from 'lib-icons'

export type TerminateModalProps = {
  close: () => void
}

export default React.memo(function TerminateModalSuccess({
  close
}: TerminateModalProps) {
  const i18n = useTranslation()
  setTimeout(() => close(), 3000)
  return (
    <Modal close={close} data-testid="terminate-reservation-success-modal">
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <Success />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            {i18n.citizenPage.reservation.modal.termination.success}
          </h2>
        </Column>
      </Columns>
    </Modal>
  )
})
