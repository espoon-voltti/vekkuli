import React from 'react'

export default React.memo(function ReadOnlyParagraphs({
  value,
  dataTestId
}: {
  value: string | string[] | undefined,
  dataTestId?: string
}) {
  if (value === undefined) return ''
  if (Array.isArray(value)) {
    return value.map((v, i) => <p key={i} data-testid={dataTestId}>{v}</p>)
  }
  return <p data-testid={dataTestId}>{value}</p>
})
