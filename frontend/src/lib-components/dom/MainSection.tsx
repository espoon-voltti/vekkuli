import React from 'react'

export default React.memo(function MainSection({
    dataTestId,
  children
}: {
  dataTestId?: String
  children: React.ReactNode
}) {
  return (
    <section id="main" className="section" data-testid={dataTestId}>
      {children}
    </section>
  )
})
