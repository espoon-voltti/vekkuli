import React from 'react'

export default React.memo(function Section({
  children
}: {
  children: React.ReactNode
}) {
  return <section className="section">{children}</section>
})
