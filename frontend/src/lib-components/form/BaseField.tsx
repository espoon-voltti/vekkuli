import React from 'react'

export interface BaseFieldProps<TValue = string> {
  id?: string
  name?: string
  label?: string
  readOnly?: boolean
  required?: boolean
  infoKey?: string
  value?: TValue
  onChange?: (value: TValue) => void
  onChangeTarget?: (target: EventTarget & HTMLInputElement) => void
  onFocus?: (e: React.FocusEvent<HTMLInputElement>) => void
  onBlur?: (e: React.FocusEvent<HTMLInputElement>) => void
  onKeyPress?: (e: React.KeyboardEvent) => void
  readonly?: boolean
  showErrorsBeforeTouched?: boolean
  ariaRequired?: boolean
  ariaInvalid?: boolean
  ariaDescribedBy?: string
  ariaLabel?: string
  dataTestId?: string
}
