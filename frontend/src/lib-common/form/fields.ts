// SPDX-FileCopyrightText: 2017-2023 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import DateRange from 'lib-common/date/date-range'
import FiniteDateRange from 'lib-common/date/finite-date-range'
import LocalDate from 'lib-common/date/local-date'
import LocalTime from 'lib-common/date/local-time'
import TimeInterval from 'lib-common/date/time-interval'
import TimeRange from 'lib-common/date/time-range'

import { mapped, object, required, transformed, value } from './form'
import {
  AnyForm,
  FieldErrors,
  Form,
  ShapeOf,
  StateOf,
  ValidationError,
  ValidationResult,
  ValidationSuccess
} from './types'

export type FieldType<F extends () => AnyForm> = Form<
  unknown,
  string,
  StateOf<ReturnType<F>>,
  ShapeOf<ReturnType<F>>
>

export const string = () => mapped(value<string>(), (s) => s.trim())
export const whitespaceTrimmedString = () =>
  mapped(value<string>(), (s) => s.replace(/\s+/g, ''))
export const boolean = () => value<boolean>()
export const number = () => value<number>()

export const positiveNumber = Object.assign(
  () =>
    transformed(value<string>(), (state) => {
      if (state === '') return ValidationSuccess.of(undefined)
      const n = parseFloat(state)
      return isNaN(n) || n <= 0
        ? ValidationError.of('positiveNumber')
        : ValidationSuccess.of(n)
    }),
  {
    empty: () => ({ value: '' })
  }
)
export interface CheckboxListConfig {
  minSelections?: number | undefined
  maxSelections?: number | undefined
}

export const checkboxList = (config?: CheckboxListConfig) =>
  transformed(value<string[]>(), (selectedValues) => {
    // Ensure all selected values are within the valid values
    if (config?.minSelections && selectedValues.length < config.minSelections) {
      return ValidationError.of('selectedValuesMin')
    }

    if (config?.maxSelections && selectedValues.length > config.maxSelections) {
      return ValidationError.of('selectedValuesMax')
    }

    return ValidationSuccess.of(selectedValues)
  })

export type CheckboxListField = FieldType<typeof checkboxList>

export interface LocalDateConfig {
  minDate?: LocalDate | undefined
  maxDate?: LocalDate | undefined
}

const validateLocalDate = (
  value: string,
  config: LocalDateConfig | undefined
): ValidationResult<
  LocalDate,
  'timeFormat' | 'dateTooEarly' | 'dateTooLate'
> => {
  const date = LocalDate.parseFiOrNull(value)
  if (date === null) return ValidationError.of('timeFormat')
  if (config?.minDate && date.isBefore(config.minDate)) {
    return ValidationError.of('dateTooEarly')
  }
  if (config?.maxDate && date.isAfter(config.maxDate)) {
    return ValidationError.of('dateTooLate')
  }
  return ValidationSuccess.of(date)
}

export const localDate = Object.assign(
  () =>
    transformed(
      object({
        value: string(),
        config: value<LocalDateConfig | undefined>()
      }),
      ({ value, config }) => {
        if (value === '') return ValidationSuccess.of(undefined)
        return validateLocalDate(value, config)
      }
    ),
  {
    empty: () => ({ value: '', config: undefined }),
    fromDate: (date: LocalDate | null, config?: LocalDateConfig) => ({
      value: date?.format() ?? '',
      config
    })
  }
)

// localDate.fromDate(birthDate)

export type LocalDateField = FieldType<typeof localDate>

export const localDateRange = Object.assign(
  () =>
    transformed(
      object({
        start: string(),
        end: string(),
        config: value<LocalDateConfig | undefined>()
      }),
      ({
        start,
        end,
        config
      }): ValidationResult<
        FiniteDateRange | undefined,
        'required' | 'timeFormat' | 'dateTooEarly' | 'dateTooLate'
      > => {
        if (start === '' && end === '') return ValidationSuccess.of(undefined)

        const startDateResult = validateLocalDate(start, config)
        const endDateResult = validateLocalDate(end, config)
        if (!startDateResult.isValid || !endDateResult.isValid) {
          const errors: FieldErrors<
            'required' | 'timeFormat' | 'dateTooEarly' | 'dateTooLate'
          > = {}
          if (start === '') {
            errors.start = 'required'
          } else if (!startDateResult.isValid) {
            errors.start = startDateResult.error
          }
          if (end === '') {
            errors.end = 'required'
          } else if (!endDateResult.isValid) {
            errors.end = endDateResult.error
          }
          return ValidationError.fromFieldErrors(errors)
        }

        const startDate = startDateResult.value
        const endDate = endDateResult.value

        if (endDate.isBefore(startDate)) {
          return ValidationError.of('timeFormat')
        }

        return ValidationSuccess.of(new FiniteDateRange(startDate, endDate))
      }
    ),
  {
    empty: (config?: LocalDateConfig) => ({ start: '', end: '', config }),
    fromRange: (
      range: FiniteDateRange | null | undefined,
      config?: LocalDateConfig
    ) => ({
      start: range?.start.format() ?? '',
      end: range?.end.format() ?? '',
      config
    }),
    fromDates: (
      start: LocalDate | null | undefined,
      end: LocalDate | null | undefined,
      config?: LocalDateConfig
    ) => ({
      start: start?.format() ?? '',
      end: end?.format() ?? '',
      config
    })
  }
)

export type LocalDateRangeField = FieldType<typeof localDateRange>

export const openEndedLocalDateRange = Object.assign(
  () =>
    transformed(
      object({
        start: string(),
        end: string(),
        config: value<LocalDateConfig | undefined>()
      }),
      ({ start, end, config }) => {
        if (start === '' && end === '') return ValidationSuccess.of(undefined)

        const startDateResult = validateLocalDate(start, config)
        const endDateResult =
          end === ''
            ? ValidationSuccess.of(null)
            : validateLocalDate(end, config)
        if (!startDateResult.isValid || !endDateResult.isValid) {
          const errors: FieldErrors<
            'required' | 'timeFormat' | 'dateTooEarly' | 'dateTooLate'
          > = {}
          if (start === '') {
            errors.start = 'required'
          } else if (!startDateResult.isValid) {
            errors.start = startDateResult.error
          }
          if (!endDateResult.isValid) {
            errors.end = endDateResult.error
          }
          return ValidationError.fromFieldErrors(errors)
        }

        const startDate = startDateResult.value
        const endDate = endDateResult.value

        if (endDate !== null && endDate.isBefore(startDate)) {
          return ValidationError.of('timeFormat')
        }

        return ValidationSuccess.of(new DateRange(startDate, endDate))
      }
    ),
  {
    empty: (config?: LocalDateConfig) => ({ start: '', end: '', config }),
    fromRange: (
      range: DateRange | null | undefined,
      config?: LocalDateConfig
    ) => ({
      start: range?.start.format() ?? '',
      end: range?.end?.format() ?? '',
      config
    }),
    fromDates: (
      start: LocalDate | null | undefined,
      end: LocalDate | null | undefined,
      config?: LocalDateConfig
    ) => ({
      start: start?.format() ?? '',
      end: end?.format() ?? '',
      config
    })
  }
)

export const localTime = () =>
  transformed(
    string(),
    (s): ValidationResult<LocalTime | undefined, 'timeFormat'> => {
      if (s === '') return ValidationSuccess.of(undefined)
      const parsed = LocalTime.tryParse(s)
      if (parsed === undefined) {
        return ValidationError.of('timeFormat')
      }
      return ValidationSuccess.of(parsed)
    }
  )

export type LocalTimeField = FieldType<typeof localTime>

export const localTimeRange = ({
  allowMidnightEnd = true
}: { allowMidnightEnd?: boolean } = {}) =>
  transformed(
    object({
      startTime: localTime(),
      endTime: localTime()
    }),
    ({
      startTime,
      endTime
    }): ValidationResult<TimeRange | undefined, 'timeFormat'> => {
      if (startTime === undefined && endTime === undefined) {
        return ValidationSuccess.of(undefined)
      }
      if (!allowMidnightEnd && endTime && endTime.isEqual(LocalTime.MIDNIGHT)) {
        return ValidationError.of('timeFormat')
      }
      const result =
        startTime !== undefined && endTime !== undefined
          ? TimeRange.tryCreate(startTime, endTime)
          : undefined
      if (result === undefined) {
        return ValidationError.of('timeFormat')
      }
      return ValidationSuccess.of(result)
    }
  )

export const requiredLocalTimeRange = () =>
  transformed(
    object({
      startTime: required(localTime()),
      endTime: required(localTime())
    }),
    ({
      startTime,
      endTime
    }): ValidationResult<TimeRange | undefined, 'timeFormat'> => {
      if (startTime === undefined && endTime === undefined) {
        return ValidationSuccess.of(undefined)
      }
      if (
        startTime === undefined ||
        endTime === undefined ||
        !endTime.isAfter(startTime)
      ) {
        return ValidationError.of('timeFormat')
      } else {
        return ValidationSuccess.of(TimeRange.tryCreate(startTime, endTime))
      }
    }
  )

export type LocalTimeRangeField = FieldType<typeof localTimeRange>

export const openEndedLocalTimeRange = () =>
  transformed(
    object({
      startTime: localTime(),
      endTime: localTime()
    }),
    ({
      startTime,
      endTime
    }): ValidationResult<TimeInterval | undefined, 'timeFormat'> => {
      if (startTime === undefined && endTime === undefined) {
        return ValidationSuccess.of(undefined)
      }
      if (
        startTime === undefined ||
        (endTime !== undefined && !endTime.isAfter(startTime))
      ) {
        return ValidationError.of('timeFormat')
      } else {
        return ValidationSuccess.of(
          new TimeInterval(startTime, endTime ?? null)
        )
      }
    }
  )
