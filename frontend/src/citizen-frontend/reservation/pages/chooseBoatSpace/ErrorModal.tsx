import Modal from 'lib-components/modal/Modal'
import React from 'react'

export type ErrorCode = 'MAX_RESERVATIONS' | 'SERVER_ERROR'

export type ErrorModalProps = {
  close: () => void
  error: ErrorCode
}

export default React.memo(function ErrorModal({
  close,
  error
}: ErrorModalProps) {
  const buttons = [
    {
      label: 'Ok'
    }
  ]

  return (
    <Modal close={close} buttons={buttons}>
      <p>Paikan varaamisessa tapahtui virhe</p>
      {error === 'MAX_RESERVATIONS' && (
        <p>Olet jo varannut maksimimäärän paikkoja</p>
      )}
    </Modal>
  )
})
