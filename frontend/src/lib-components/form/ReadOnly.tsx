import React from 'react'

export default React.memo(function ReadOnlyParagraphs({
  value
}: {
  value: string | string[] | undefined
}) {
  if (value === undefined) return ''
  if (Array.isArray(value)) {
    return value.map((v, i) => <p key={i}>{v}</p>)
  }
  return <p>{value}</p>
})
