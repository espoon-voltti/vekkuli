import React, { useState } from 'react'

import { formatNumber } from 'citizen-frontend/shared/formatters'
import { BoundFormState } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders } from './utils'

interface NumberFieldProps extends Omit<BaseFieldProps, 'onChange' | 'value'> {
  bind?: BoundFormState<string>
  step?: number
  min?: number
  max?: number
  value?: number | string
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
  showErrorsBeforeTouched,
  required
}: NumberFieldProps) {
  const { state, set, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const { showAllErrors } = useFormErrorContext()
  const showError =
    (showErrorsBeforeTouched || touched || showAllErrors === true) && !isValid()
  const readOnlyValue = state !== undefined ? state : value
  return (
    <div className="field">
      <div className="control">
        <label className="label" htmlFor={id}>
          {label}
          {required && ' *'}
        </label>
        {readonly ? (
          <ReadOnly value={formatNumber(readOnlyValue)} />
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
