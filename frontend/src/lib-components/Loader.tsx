import React from 'react'

import { Result } from 'lib-common/api'
import { Error500 } from 'lib-icons'

import { Column, Columns, Container } from './dom'

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
  if (results.some((r) => r.isLoading)) return <LoadingComponent />
  if (results.some((r) => r.isFailure) && !allowFailure)
    return <ErrorComponent />
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

const LoadingComponent = React.memo(function LoadingComponent() {
  return (
    <div className="has-text-centered pv-xl">
      <div className="is-primary-color button is-loading is-transparent" />
    </div>
  )
})

const ErrorComponent = React.memo(function ErrorComponent() {
  return (
    <Container>
      <Columns isMultiline>
        <Column isFull textCentered>
          <Error500 />
        </Column>
        <Column isFull textCentered>
          Voi ei! Myrsky yllätti järjestelmän. Yritä myöhemmin uudestaan.
        </Column>
      </Columns>
    </Container>
  )
})
