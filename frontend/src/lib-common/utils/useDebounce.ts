// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { useEffect, useState } from 'react'

export const useDebounce = <T>(
  value: T,
  delay: number,
  triggerOnFirstChange = false
): T => {
  const [debouncedValue, setDebouncedValue] = useState(value)
  const [isWaiting, setIsWaiting] = useState(false)

  useEffect(() => {
    if (!isWaiting && triggerOnFirstChange) {
      setDebouncedValue(value)
    }

    setIsWaiting(true)
    const handler = setTimeout(() => {
      setDebouncedValue(value)
      setIsWaiting(false)
    }, delay)

    return () => {
      clearTimeout(handler)
    }
  }, [value]) // eslint-disable-line react-hooks/exhaustive-deps

  return debouncedValue
}
