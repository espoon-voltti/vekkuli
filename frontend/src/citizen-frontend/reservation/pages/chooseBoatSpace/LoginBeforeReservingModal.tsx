import Modal from 'lib-components/modal/Modal'
import React from 'react'

import { getLoginUri } from 'citizen-frontend/config'

export type LoginBeforeReservingModalProps = {
  close: () => void
}

export default React.memo(function LoginBeforeReservingModal({
  close
}: LoginBeforeReservingModalProps) {
  const buttons = [
    {
      label: 'Peruuta'
    },
    {
      label: 'Jatka tunnistautumiseen',
      type: 'primary' as const,
      action: () => window.location.replace(getLoginUri())
    }
  ]

  return (
    <Modal close={close} buttons={buttons}>
      <p>Olet varaamassa paikkaa:</p>
      <p>Venepaikan varaaminen vaatii vahvan tunnistautumisen.</p>
    </Modal>
  )
})
