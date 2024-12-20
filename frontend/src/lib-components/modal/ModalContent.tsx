import React, { ReactNode } from 'react'

type ModalContentProps = {
  children?: ReactNode | ReactNode[]
}
export default React.memo(function ModalContent({
  children
}: ModalContentProps) {
  return <div className="modal-content mv-m">{children}</div>
})
