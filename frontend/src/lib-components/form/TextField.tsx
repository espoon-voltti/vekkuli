import React, { useState } from 'react'

import { BoundFormState } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'
import { FieldErrors } from 'lib-common/form/types'

import { BaseFieldProps } from './BaseField'
import FieldErrorContainer from './FieldErrorContainer'
import ReadOnly from './ReadOnly'
import { bindOrPlaceholders } from './utils'
import { EditLink } from 'lib-components/links'
import { Columns } from 'lib-components/dom'

interface TextFieldProps extends Omit<BaseFieldProps, 'onChange' | 'value'> {
  bind?: BoundFormState<string>
  value?: string | string[]
  editAction?: () => void
}

export default React.memo(function TextField({
  id,
  name,
  label,
  bind,
  readonly,
  required,
  value,
  showErrorsBeforeTouched,
  ariaLabel,
  dataTestId,
  editAction
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
        <div className="is-display-flex">
          <label
            className={`label${editAction ? ' mb-none mr-m' : ''}`}
            htmlFor={id}
          >
            {label}
            {required && ' *'}
          </label>
          {editAction && <EditLink action={editAction} />}
        </div>
        <InputOrReadOnly
          readonly={readonly}
          dataTestId={dataTestId}
          readOnlyValue={readOnlyValue}
          id={id}
          name={name}
          state={state || ''}
          set={set}
          ariaLabel={ariaLabel}
          required={required}
          showError={showError}
          validationError={validationError}
          translateError={translateError}
          errorFieldId={errorFieldId}
          setTouched={setTouched}
        />
      </div>
    </div>
  )
})

type InputOrRealOnlyProps = {
  readonly?: boolean
  dataTestId?: string
  readOnlyValue?: string | string[]
  id?: string
  name?: string
  state: string
  set: (value: string) => void
  ariaLabel?: string
  required?: boolean
  showError: boolean
  validationError:
    | (() => string | FieldErrors<string> | undefined)
    | (() => string)
  translateError: (error: string) => string
  errorFieldId?: string
  setTouched: (touched: boolean) => void
}

export function InputOrReadOnly({
  readonly,
  dataTestId,
  readOnlyValue,
  id,
  name,
  state,
  set,
  ariaLabel,
  required,
  showError,
  validationError,
  translateError,
  errorFieldId,
  setTouched
}: InputOrRealOnlyProps) {
  return (
    <>
      {readonly ? (
        <ReadOnly dataTestId={dataTestId} value={readOnlyValue || '-'} />
      ) : (
        <>
          <input
            className="input"
            type="text"
            id={id}
            name={name}
            value={state}
            aria-label={ariaLabel}
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
    </>
  )
}
