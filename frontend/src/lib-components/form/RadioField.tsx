import classNames from 'classnames'
import React, { useState } from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { OneOfState } from 'lib-common/form/form'
import { BoundFormState } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders, getI18nLabel } from './utils'

export interface RadioOption {
  value: string
  label?: string
  info?: string
}

interface RadioFieldProps<T> extends Omit<BaseFieldProps, 'onChange'> {
  bind?: BoundFormState<OneOfState<T>>
  options?: RadioOption[]
  noErrorContainer?: boolean
  horizontal?: boolean
}

function RadioField_<T>({
  id,
  name,
  label,
  bind,
  readonly,
  value,
  showErrorsBeforeTouched,
  noErrorContainer,
  horizontal
}: RadioFieldProps<T>) {
  const i18n = useTranslation()
  const { state, update, isValid, validationError, translateError } =
    bindOrPlaceholders(bind)
  const [touched, setTouched] = useState(false)
  const { showAllErrors } = useFormErrorContext()
  const showError =
    (showErrorsBeforeTouched || touched || showAllErrors === true) && !isValid()

  return (
    <div className="field">
      {!label ? null : <label className="label">{label}</label>}
      {readonly ? (
        <ReadOnly value={value} />
      ) : (
        <div className={classNames('control', { columns: horizontal })}>
          {state?.options.map((option) => (
            <RadioFieldInput
              key={option.domValue}
              id={`${id}-${option.domValue}`}
              onChange={() =>
                update((prev) => ({ ...prev, domValue: option.domValue }))
              }
              name={name}
              value={option.domValue}
              onBlur={() => setTouched(true)}
              label={getI18nLabel(option.label, i18n)}
              selected={option.domValue === state?.domValue}
              info={getI18nLabel(option.info, i18n)}
              horizontal={horizontal}
              disabled={option.disabled}
            />
          ))}
          {!noErrorContainer && (
            <FieldErrorContainer
              showError={showError}
              error={validationError()}
              translateError={translateError}
            />
          )}
        </div>
      )}
    </div>
  )
}

export const RadioField = React.memo(RadioField_) as typeof RadioField_

interface RadioFieldInputProps
  extends Omit<BaseFieldProps, 'value'>,
    RadioOption {
  id: string
  name?: string
  selected: boolean
  horizontal?: boolean
  disabled?: boolean
}

const RadioFieldInput = React.memo(function RadioFieldInput({
  id,
  name,
  value,
  label,
  info,
  selected,
  onChange,
  horizontal,
  disabled
}: RadioFieldInputProps) {
  return (
    <label
      className={classNames('radio', {
        'has-text-top-aligned': !horizontal,
        'column is-narrow': horizontal,
        'is-disabled': disabled
      })}
      htmlFor={id}
    >
      <input
        type="radio"
        id={id}
        name={name}
        value={value}
        checked={selected}
        disabled={disabled}
        onChange={(e) => {
          e.stopPropagation()
          if (onChange) onChange(value)
        }}
      />
      <div className="label-text">
        <p className="body">{label}</p>
        {!!info && <p className="mt-s information-text">{info}</p>}
      </div>
    </label>
  )
})
