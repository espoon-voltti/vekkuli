import React from 'react'

export default React.memo(function Button({
  children
}: {
  children: React.ReactNode
}) {
  return <div className="buttons">{children}</div>
})
