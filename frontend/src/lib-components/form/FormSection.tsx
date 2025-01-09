import React from 'react'

export default React.memo(function FormSection({
  children
}: {
  children: React.ReactNode
}) {
  return <div className="form-section">{children}</div>
})
