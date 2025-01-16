import React from 'react'

export default React.memo(function ScreenReaderOnly({
  children
}: {
  children: React.ReactNode | React.ReactNode[]
}) {
  return (
    <div className="is-sr-only" role="alert">
      {children}
    </div>
  )
})
