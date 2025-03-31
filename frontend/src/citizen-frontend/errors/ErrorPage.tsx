import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import React from 'react'
import { isRouteErrorResponse, useNavigate, useRouteError } from 'react-router'

import { getLoginUri } from 'citizen-frontend/config.js'
import { Lang } from 'citizen-frontend/localization'
import { Error400, Error403, Error404, Error500, ErrorGeneric } from 'lib-icons'

const langs: Lang[] = ['fi', 'sv', 'en']

export default React.memo(function ErrorPage() {
  const error = useRouteError()
  if (isRouteErrorResponse(error)) {
    return (
      <ErrorOverlay>
        <SelectedErrorComponent statusCode={error.status} />
      </ErrorOverlay>
    )
  }
  return (
    <ErrorOverlay>
      <ErrorComponent500 />
    </ErrorOverlay>
  )
})

export function ErrorElement({ statusCode }: { statusCode: number }) {
  return (
    <div className="error-container has-text-primary has-background-white is-flex is-justify-content-center is-align-items-center">
      <Columns isMultiline>
        <SelectedErrorComponent statusCode={statusCode} />
      </Columns>
    </div>
  )
}

const SelectedErrorComponent = React.memo(function SelectedErrorComponent({
  statusCode
}: {
  statusCode: number
}) {
  switch (statusCode) {
    case 400:
      return <ErrorComponent400 />
    case 401:
      return <ErrorComponent401 />
    case 403:
      return <ErrorComponent403 />
    case 404:
      return <ErrorComponent404 />
    default:
      return <ErrorComponent500 />
  }
})

const ErrorOverlay = React.memo(function ErrorOverlay({
  children
}: {
  children: React.ReactNode
}) {
  const navigate = useNavigate()
  return (
    <div className="error-container is-overlay has-text-primary has-background-white is-flex is-justify-content-center is-align-items-center">
      <div>
        <Columns isMultiline>{children}</Columns>
        <Buttons alignment="center">
          <Button type="primary" action={() => navigate('/')}>
            {translations.buttonLabel}
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
        {langs.map((lang, index) => (
          <h2
            key={lang}
            className={`list-item ${index > 0 ? 'is-size-5' : 'is-size-4'}`}
          >
            {translations[lang].error400}
          </h2>
        ))}
      </Column>
    </>
  )
})

const ErrorComponent401 = React.memo(function ErrorComponent400() {
  const navigate = useNavigate()
  return (
    <>
      <Column isFull textCentered>
        <ErrorGeneric />
      </Column>
      <Column isFull textCentered>
        {langs.map((lang, index) => (
          <h2
            key={lang}
            className={`list-item ${index > 0 ? 'is-size-5' : 'is-size-4'}`}
          >
            {translations[lang].error401}
          </h2>
        ))}
      </Column>
      <Column>
        <Buttons alignment="center">
          <a
            data-testid="loginButton"
            className="button is-primary"
            href={getLoginUri()}
          >
            {translations.loginButtonLabel}
          </a>
        </Buttons>
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
        {langs.map((lang, index) => (
          <h2
            key={lang}
            className={`list-item ${index > 0 ? 'is-size-6' : 'is-size-5'}`}
          >
            {translations[lang].error403}
          </h2>
        ))}
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
        {langs.map((lang, index) => (
          <h2
            key={lang}
            className={`list-item has-text-weight-bold ${index > 0 ? 'is-size-5' : 'is-size-4'}`}
          >
            {translations[lang].error404}
          </h2>
        ))}
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
        {langs.map((lang, index) => (
          <h2
            key={lang}
            className={`list-item ${index > 0 ? 'is-size-6' : 'is-size-5'}`}
          >
            {translations[lang].error500}
          </h2>
        ))}
      </Column>
    </>
  )
})

// Translations here as the language context is not available in the error page
const translations = {
  buttonLabel: 'Etusivulle / Till startsidan / To front page',
  loginButtonLabel: 'Kirjaudu sisään / Logga in / Log in',
  fi: {
    error400: 'Voi ei! Karahdit karille.',
    error401: 'Voi ei! Et pääse veneeseen ilman pääsylupaa.',
    error403: 'Voi ei! Veneväylä on tukossa.',
    error404: 'Voi ei! Väylä hukassa.',
    error500: 'Voi ei! Myrsky yllätti järjestelmän. Yritä myöhemmin uudestaan.'
  },
  sv: {
    error400: 'Oj nej! Du har gått på grund.',
    error401: 'Oj nej! Du får inte gå ombord utan tillstånd.',
    error403: 'Oj nej! Båtleder är blockerad.',
    error404: 'Oj nej! Leden är borta.',
    error500: 'Oj nej! En storm överraskade systemet. Försök igen senare.'
  },
  en: {
    error400: 'Oh no! You have run aground.',
    error401: 'Oh no! You can’t board without permission.',
    error403: 'Oh no! The boat route is blocked.',
    error404: 'Oh no! The route is lost.',
    error500:
      'Oh no! A storm caught the system off guard. Please try again later.'
  }
}
