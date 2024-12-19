import React, { useState } from 'react'

import { BoundFormState } from 'lib-common/form/hooks'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders } from './utils'

interface NumberFieldProps extends Omit<BaseFieldProps, 'onChange' | 'value'> {
  bind?: BoundFormState<string>
  step?: number
  min?: number
  max?: number
  value?: number
}

function NumberFieldR({
  id,
  name,
  label,
  bind,
  step,
  min,
  max,
  readonly,
  value,
  showErrorsBeforeTouched
}: NumberFieldProps) {
  const { state, set, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const showError = (showErrorsBeforeTouched || touched) && !isValid()
  const readOnlyValue = state !== undefined ? state : value
  return (
    <div className="field">
      <div className="control">
        <label className="label" htmlFor={id}>
          {label}
        </label>
        {readonly ? (
          <ReadOnly value={readOnlyValue?.toString()} />
        ) : (
          <>
            <input
              className="input"
              type="number"
              id={id}
              step={step}
              min={min}
              max={max}
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
}

export const NumberField = React.memo(NumberFieldR) as typeof NumberFieldR
