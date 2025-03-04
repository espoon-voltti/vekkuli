import { ButtonAlignment } from 'lib-components/dom/Buttons.js'
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
  buttonAlignment?: ButtonAlignment
  'data-testid'?: string
}

export default React.memo(function Modal({
  children,
  title,
  close,
  buttons,
  buttonAlignment,
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
          alignment={buttonAlignment}
        />
      </ModalContent>
    </ModalBackground>
  )
})
