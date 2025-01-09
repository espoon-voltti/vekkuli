import React from 'react'

type ModalTitleProps = {
  title?: string
}
export default React.memo(function ModalTitle({ title }: ModalTitleProps) {
  if (!title) return null
  return <h3>{title}</h3>
})
