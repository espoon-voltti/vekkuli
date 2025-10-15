// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { useEffect, useRef, useState } from 'react'

export const useDebounce = <T>(
  value: T,
  delay: number,
  triggerOnFirstChange = false
): T => {
  const [debouncedValue, setDebouncedValue] = useState(value)
  const firstChangeFiredRef = useRef(false)
  const timerRef = useRef<number | null>(null)

  useEffect(() => {
    if (timerRef.current != null) {
      clearTimeout(timerRef.current)
    }

    const shouldFireImmediately =
      triggerOnFirstChange && !firstChangeFiredRef.current

    const effectiveDelay = shouldFireImmediately ? 0 : delay

    timerRef.current = window.setTimeout(
      () => {
        setDebouncedValue(value)
        if (shouldFireImmediately) {
          firstChangeFiredRef.current = true
        }
      },
      // Make sure the delay is never negative
      Math.max(0, effectiveDelay)
    )

    return () => {
      if (timerRef.current != null) {
        clearTimeout(timerRef.current)
      }
    }
  }, [value, delay, triggerOnFirstChange])

  return debouncedValue
}
