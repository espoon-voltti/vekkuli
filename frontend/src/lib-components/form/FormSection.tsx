import React from 'react'

export default React.memo(function FormSection({
  children,
  ...rest
}: {
  children: React.ReactNode
  'data-testid'?: string
}) {
  return (
    <div className="form-section" {...rest}>
      {children}
    </div>
  )
})
