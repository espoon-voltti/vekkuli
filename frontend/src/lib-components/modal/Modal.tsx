import React from 'react'

import ModalBackground from './ModalBackground'
import ModalButtons, { ModalButton } from './ModalButtons'
import ModalContent from './ModalContent'
import ModalContentWrapper from './ModalContentWrapper'
import ModalTitle from './ModalTitle'

type ModalProperties = {
  title?: string
  children: React.ReactNode
  close: () => void
  buttons?: ModalButton[]
  buttonsCentered?: boolean
  'data-testid'?: string
}

export default React.memo(function Modal({
  children,
  title,
  close,
  buttons,
  buttonsCentered,
  'data-testid': dataTestId
}: ModalProperties) {
  return (
    <ModalBackground close={close}>
      <ModalContent data-testid={dataTestId}>
        <ModalTitle title={title} />
        <ModalContentWrapper>{children}</ModalContentWrapper>
        <ModalButtons
          buttons={buttons}
          close={close}
          centered={buttonsCentered}
        />
      </ModalContent>
    </ModalBackground>
  )
})
