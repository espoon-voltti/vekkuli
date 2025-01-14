import React from 'react'

export default React.memo(function MainSection({
  children
}: {
  children: React.ReactNode
}) {
  return (
    <section id="main" className="section">
      {children}
    </section>
  )
})
