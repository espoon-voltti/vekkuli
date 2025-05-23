import React, { ReactNode } from 'react'

type ModalContentProps = {
  children?: ReactNode | ReactNode[]
  'data-testid'?: string
}
export default React.memo(function ModalContent({
  children,
  ...rest
}: ModalContentProps) {
  return (
    <div
      className="modal-content mv-m"
      role="dialog"
      aria-modal="true"
      {...rest}
    >
      {children}
    </div>
  )
})
