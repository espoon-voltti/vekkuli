import React from 'react'

import ScreenReaderOnly from './ScreenReaderOnly'

export default React.memo(function MainSection({
  dataTestId,
  children,
  ariaLabel
}: {
  dataTestId?: string
  children: React.ReactNode
  ariaLabel?: string
}) {
  return (
    <section
      id="main"
      role="main"
      tabIndex={-1}
      className="section"
      data-testid={dataTestId}
      aria-label={ariaLabel}
    >
      {!!ariaLabel && <ScreenReaderOnly>{ariaLabel}</ScreenReaderOnly>}
      {children}
    </section>
  )
})
