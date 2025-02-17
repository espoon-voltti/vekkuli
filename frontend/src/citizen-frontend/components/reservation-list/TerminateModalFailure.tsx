import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Failure } from 'lib-icons'

export type TerminateModalProps = {
  close: () => void
}

export default React.memo(function TerminateModalFailure({
  close
}: TerminateModalProps) {
  const i18n = useTranslation()
  const buttons = [
    {
      label: 'Ok',
      type: 'danger' as const
    }
  ]

  return (
    <Modal
      close={close}
      buttons={buttons}
      buttonsCentered
      data-testid="terminate-reservation-failure-modal"
    >
      <Columns isVCentered isMultiline>
        <Column isFull textCentered>
          <Failure />
        </Column>
        <Column isFull textCentered>
          <h2 className="has-text-centered mb-none">
            {i18n.citizenPage.reservation.modal.termination.terminationFailed}
          </h2>
        </Column>
      </Columns>
    </Modal>
  )
})
