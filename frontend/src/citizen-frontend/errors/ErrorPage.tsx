import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import React from 'react'
import { isRouteErrorResponse, useNavigate, useRouteError } from 'react-router'

import { Error400, Error403, Error404, Error500 } from 'lib-icons'

export default React.memo(function ErrorPage() {
  const error = useRouteError()
  if (isRouteErrorResponse(error)) {
    return (
      <ErrorContainer>
        <SelectedErrorComponent statusCode={error.status} />
      </ErrorContainer>
    )
  }
  return (
    <ErrorContainer>
      <ErrorComponent500 />
    </ErrorContainer>
  )
})

const SelectedErrorComponent = React.memo(function SelectedErrorComponent({
  statusCode
}: {
  statusCode: number
}) {
  switch (statusCode) {
    case 400:
      return <ErrorComponent400 />
    case 403:
      return <ErrorComponent403 />
    case 404:
      return <ErrorComponent404 />
    default:
      return <ErrorComponent500 />
  }
})

const ErrorContainer = React.memo(function ErrorContainer({
  children
}: {
  children: React.ReactNode
}) {
  const navigate = useNavigate()
  return (
    <div className="is-overlay has-text-primary has-background-white is-flex is-justify-content-center is-align-items-center">
      <div>
        <Columns isMultiline>{children}</Columns>
        <Buttons centered>
          <Button type="primary" action={() => navigate('/')}>
            Etusivulle
          </Button>
        </Buttons>
      </div>
    </div>
  )
})

const ErrorComponent400 = React.memo(function ErrorComponent400() {
  return (
    <>
      <Column isFull textCentered>
        <Error400 />
      </Column>
      <Column isFull textCentered>
        <h1>Voi ei! Karahdit karille.</h1>
      </Column>
    </>
  )
})

const ErrorComponent403 = React.memo(function ErrorComponent403() {
  return (
    <>
      <Column isFull textCentered>
        <Error403 />
      </Column>
      <Column isFull textCentered>
        Voi ei! Veneväylä on tukossa.
      </Column>
    </>
  )
})

const ErrorComponent404 = React.memo(function ErrorComponent404() {
  return (
    <>
      <Column isFull textCentered>
        <Error404 />
      </Column>
      <Column isFull textCentered>
        <h2 className="is-size-4 has-text-weight-bold">
          Voi ei! Väylä hukassa.
        </h2>
      </Column>
    </>
  )
})

const ErrorComponent500 = React.memo(function ErrorComponent500() {
  return (
    <>
      <Column isFull textCentered>
        <Error500 />
      </Column>
      <Column isFull textCentered>
        Voi ei! Myrsky yllätti järjestelmän. Yritä myöhemmin uudestaan.
      </Column>
    </>
  )
})
