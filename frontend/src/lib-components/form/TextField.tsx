import React, { useState } from 'react'

import { BoundFormState } from 'lib-common/form/hooks'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders } from './utils'

interface TextFieldProps
  extends Omit<BaseFieldProps, 'onChange' | 'name' | 'value'> {
  bind?: BoundFormState<string>
  name?: string
  value?: string | string[]
}

export default React.memo(function TextField({
  id,
  name,
  label,
  bind,
  readonly,
  value,
  showErrorsBeforeTouched
}: TextFieldProps) {
  const { state, set, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const showError = (showErrorsBeforeTouched || touched) && !isValid()

  return (
    <div className="field">
      <div className="control">
        <label className="label" htmlFor={id}>
          {label}
        </label>
        {readonly ? (
          <ReadOnly value={value} />
        ) : (
          <>
            <input
              className="input"
              type="text"
              id={id}
              name={name}
              value={state}
              onChange={(e) => set(e.target.value)}
              onBlur={() => setTouched(true)}
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
