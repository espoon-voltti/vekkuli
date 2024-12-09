import React from 'react'
import { isRouteErrorResponse, useRouteError } from 'react-router'

export default React.memo(function ErrorPage() {
  const error = useRouteError()
  if (isRouteErrorResponse(error)) {
    return (
      <div>
        <h1>
          {error.status} {error.statusText}
        </h1>
        <p>{error.data || 'Tapahtui virhe.'}</p>
      </div>
    )
  }
  return <h1>Hups, tuntematon virhe.</h1>
})
