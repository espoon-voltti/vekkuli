import classNames from 'classnames'
import React from 'react'

import { BoundFormState } from 'lib-common/form/hooks'

import { MultiSelectState } from '../../lib-common/form/form'

import { BaseFieldProps } from './BaseField'

interface CheckboxFieldProps<T>
  extends Omit<BaseFieldProps, 'onChange' | 'label'> {
  bind: BoundFormState<MultiSelectState<T>>
  label?: string
  isFullWidth?: boolean
}

function CheckboxFieldR<T>({
  id,
  name,
  label,
  bind: { state, update },
  isFullWidth
}: CheckboxFieldProps<T>) {
  const onOnChange = (val: string, checked: boolean) => {
    const isSelected = state.domValues.includes(val)
    if (checked && !isSelected) {
      update((prev) => ({ ...prev, domValues: [...state.domValues, val] }))
    } else if (!checked && isSelected) {
      update((prev) => ({
        ...prev,
        domValues: prev.domValues.filter((v: T) => v !== val)
      }))
    }
  }
  return (
    <div>
      {!label ? null : (
        <label className="label" htmlFor={id}>
          {label}
        </label>
      )}
      <div className="field columns is-multiline is-mobile">
        {state.options.map((option) => (
          <CheckboxFieldInput
            key={option.domValue}
            id={`${id}-${option.domValue}`}
            onChange={(checked) => onOnChange(option.domValue, checked)}
            name={name}
            selected={state.domValues.includes(option.domValue)}
            value={option.domValue}
            label={option.label}
            isFullWidth={isFullWidth}
          />
        ))}
      </div>
    </div>
  )
}

export const CheckboxField = React.memo(CheckboxFieldR) as typeof CheckboxFieldR

interface CheckboxFieldInputProps
  extends Omit<BaseFieldProps<boolean>, 'value'> {
  id: string
  name: string
  value: string
  selected: boolean
  isFullWidth?: boolean
}

const CheckboxFieldInput = React.memo(function CheckboxFieldInput({
  id,
  name,
  value,
  label,
  selected,
  onChange,
  isFullWidth
}: CheckboxFieldInputProps) {
  return (
    <div
      className={classNames('column', 'pb-none', {
        'is-half': !isFullWidth,
        'is-full': isFullWidth
      })}
    >
      <label className="checkbox">
        <input
          name={name}
          id={id}
          value={value}
          checked={selected}
          type="checkbox"
          onChange={(e) => {
            e.stopPropagation()
            onChange?.(e.target.checked)
          }}
        />
        <span>{label}</span>
      </label>
    </div>
  )
})
