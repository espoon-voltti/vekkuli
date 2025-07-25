import React, { useState } from 'react'

import LocalDate from 'lib-common/date/local-date'
import { BoundFormState } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders } from './utils'

export interface TextFieldProps
  extends Omit<BaseFieldProps, 'onChange' | 'value'> {
  bind?: BoundFormState<string>
  value?: LocalDate
}

export default React.memo(function DateField({
  id,
  name,
  label,
  bind,
  readonly,
  value,
  showErrorsBeforeTouched,
  required
}: TextFieldProps) {
  const { state, set, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const { showAllErrors } = useFormErrorContext()
  const showError =
    (showErrorsBeforeTouched || touched || showAllErrors === true) && !isValid()
  return (
    <div className="field">
      <div className="control">
        <label className="label" htmlFor={id}>
          {label}
        </label>
        {readonly ? (
          <ReadOnly value={value?.format()} />
        ) : (
          <>
            <input
              className="input"
              type="text"
              id={id}
              name={name}
              value={state}
              aria-invalid={showError}
              aria-required={required}
              onBlur={() => setTouched(true)}
              onChange={(e) => set(e.target.value)}
            />
            <FieldErrorContainer
              showError={showError}
              error={validationError()}
              translateError={translateError}
            />
          </>
        )}
      </div>
    </div>
  )
})
