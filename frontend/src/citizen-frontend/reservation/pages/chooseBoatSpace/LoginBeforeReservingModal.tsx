import { ScreenReaderOnly } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { getLoginUri } from 'citizen-frontend/config'
import { useTranslation } from 'citizen-frontend/localization'

export type LoginBeforeReservingModalProps = {
  close: () => void
}

export default React.memo(function LoginBeforeReservingModal({
  close
}: LoginBeforeReservingModalProps) {
  const i18n = useTranslation()
  const buttons = [
    {
      label: i18n.common.cancel
    },
    {
      label: i18n.reservation.auth.continue,
      type: 'primary' as const,
      action: () => window.location.replace(getLoginUri())
    }
  ]

  const title = i18n.reservation.auth.reservingBoatSpace
  const body = i18n.reservation.auth.reservingRequiresAuth

  return (
    <Modal close={close} buttons={buttons} data-testid="login-before-reserving">
      <ScreenReaderOnly>
        {title}
        {'. '}
        {body}
      </ScreenReaderOnly>
      <p>{title}</p>
      <p>{body}</p>
    </Modal>
  )
})
