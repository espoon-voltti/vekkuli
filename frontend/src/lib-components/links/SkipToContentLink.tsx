import React from 'react'

export default React.memo(function SkipToContentLink({
  children,
  target
}: {
  children: React.ReactNode
  target: string
}) {
  return (
    <a
      href={`#${target}`}
      className="button is-primary skip-to-main-content-link"
    >
      {children}
    </a>
  )
})
