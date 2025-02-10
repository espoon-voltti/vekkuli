import React, { useState } from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { OneOfState } from 'lib-common/form/form'
import { BoundFormState } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders, getI18nLabel } from './utils'

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
  const i18n = useTranslation()
  const { state, update, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const { showAllErrors } = useFormErrorContext()
  const showError =
    (showErrorsBeforeTouched || touched || showAllErrors === true) && !isValid()
  const readOnlyValue =
    state !== undefined
      ? state.options.find((o) => o.domValue === state.domValue)?.label
      : value
  return (
    <div className="field">
      <div className="control">
        <label className="label" htmlFor={id}>
          {label}
          {required && ' *'}
        </label>
        {readonly ? (
          <ReadOnly value={getI18nLabel(readOnlyValue, i18n)} />
        ) : (
          <>
            <div className={`select${isFullWidth ? ' is-fullwidth' : ''}`}>
              <select
                id={id}
                name={name}
                required={required}
                value={state?.domValue}
                aria-invalid={showError}
                onBlur={() => setTouched(true)}
                onChange={(e) => {
                  e.preventDefault()
                  update((prev) => ({ ...prev, domValue: e.target.value }))
                }}
              >
                {state?.options.map((opt) => (
                  <option key={opt.domValue} value={opt.domValue}>
                    {getI18nLabel(opt.label, i18n)}
                  </option>
                ))}
              </select>
            </div>
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

export const SelectField = React.memo(SelectField_) as typeof SelectField_
