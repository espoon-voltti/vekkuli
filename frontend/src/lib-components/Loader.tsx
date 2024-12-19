import React from 'react'

import { Result } from '../lib-common/api'

interface LoaderProps<T> {
  children: (value: T) => React.ReactNode
  result: Result<T>
}

function LoaderR<T>({ children, result }: LoaderProps<T>) {
  if (result.isLoading) return <div>Loading...</div>
  if (result.isFailure) return <div>Error</div>
  return <>{children(result.value)}</>
}

export const Loader = React.memo(LoaderR) as typeof LoaderR
