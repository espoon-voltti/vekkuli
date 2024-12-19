import React, { useState } from 'react'

import { BoundFormState } from 'lib-common/form/hooks'

import { OneOfState } from '../../lib-common/form/form'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders } from './utils'

interface SelectFieldProps<T> extends Omit<BaseFieldProps, 'onChange'> {
  isFullWidth?: boolean
  bind?: BoundFormState<OneOfState<T>>
}

function SelectField_<T>({
  id,
  name,
  required,
  label,
  isFullWidth,
  bind,
  readonly,
  value,
  showErrorsBeforeTouched
}: SelectFieldProps<T>) {
  const { state, update, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const showError = (showErrorsBeforeTouched || touched) && !isValid()
  const readOnlyValue =
    state !== undefined
      ? state.options.find((o) => o.domValue === state.domValue)?.label
      : value
  return (
    <div className="field">
      <div className="control">
        <label className="label" htmlFor={id}>
          {label}
        </label>
        {readonly ? (
          <ReadOnly value={readOnlyValue} />
        ) : (
          <div className={`select${isFullWidth ? ' is-fullwidth' : ''}`}>
            <select
              id={id}
              name={name}
              required={required}
              value={state?.domValue}
              onBlur={() => setTouched(true)}
              onChange={(e) => {
                e.preventDefault()
                update((prev) => ({ ...prev, domValue: e.target.value }))
              }}
            >
              {state?.options.map((opt) => (
                <option key={opt.domValue} value={opt.domValue}>
                  {opt.label}
                </option>
              ))}
            </select>
            <FieldErrorContainer
              showError={showError}
              error={validationError()}
              translateError={translateError}
            />
          </div>
        )}
      </div>
    </div>
  )
}

export const SelectField = React.memo(SelectField_) as typeof SelectField_
