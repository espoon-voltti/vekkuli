import React, { useState } from 'react'

import { BoundFormState } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders } from './utils'

interface TextFieldProps extends Omit<BaseFieldProps, 'onChange' | 'value'> {
  bind?: BoundFormState<string>
  value?: string | string[]
}

export default React.memo(function TextField({
  id,
  name,
  label,
  bind,
  readonly,
  required,
  value,
  showErrorsBeforeTouched
}: TextFieldProps) {
  const { showAllErrors } = useFormErrorContext()
  const { state, set, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const showError =
    (showErrorsBeforeTouched || touched || showAllErrors === true) && !isValid()

  const readOnlyValue = state !== undefined ? state : value
  const errorFieldId = id && `error-${id}`

  return (
    <div className="field">
      <div className="control">
        <label className="label" htmlFor={id}>
          {label}
          {required && ' *'}
        </label>
        {readonly ? (
          <ReadOnly value={readOnlyValue} />
        ) : (
          <>
            <input
              className="input"
              type="text"
              id={id}
              name={name}
              value={state}
              aria-required={required}
              aria-invalid={showError}
              aria-describedby={errorFieldId}
              onChange={(e) => set(e.target.value)}
              onBlur={() => setTouched(true)}
            />
            <FieldErrorContainer
              showError={showError}
              error={validationError()}
              translateError={translateError}
              id={errorFieldId}
            />
          </>
        )}
      </div>
    </div>
  )
})
