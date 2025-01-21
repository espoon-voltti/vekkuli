import React from 'react'

import { FieldErrors } from 'lib-common/form/types'

type FieldErrorContainerProps = {
  showError: boolean
  error: string | FieldErrors<string> | undefined
  translateError: (error: string) => string
  id?: string
}

export default React.memo(function FieldErrorContainer({
  showError,
  error,
  translateError,
  id
}: FieldErrorContainerProps) {
  const errorMessage =
    showError && typeof error == 'string' ? translateError(error) : null

  return (
    <p className="help is-danger" aria-live="polite" id={id}>
      {errorMessage}
    </p>
  )
})
