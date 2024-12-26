import React from 'react'

import { Result } from 'lib-common/api'

interface LoaderProps<T extends unknown[]> {
  children: (...values: { [K in keyof T]: T[K] }) => React.ReactNode
  results: { [K in keyof T]: Result<T[K]> }
  allowFailure?: boolean
}

function LoaderR<T extends unknown[]>({
  children,
  results,
  allowFailure
}: LoaderProps<T>) {
  if (results.some((r) => r.isLoading)) return <div>Loading...</div>
  if (results.some((r) => r.isFailure) && !allowFailure) return <div>Error</div>
  const values = results.map((r) => {
    if (!r.isLoading && !r.isFailure) {
      return r.value
    }
    if (allowFailure) return null
    throw new Error(
      'Unexpected state: All results should be successful at this point.'
    )
  }) as { [K in keyof T]: T[K] }
  return <>{children(...values)}</>
}

export const Loader = React.memo(LoaderR) as typeof LoaderR
