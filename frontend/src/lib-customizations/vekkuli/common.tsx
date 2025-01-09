// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import isArray from 'lodash/isArray'
import React from 'react'

import { JsonOf } from 'lib-common/json'

import type { CommonCustomizations } from './types'

export const mergeCustomizer = (
  _original: unknown,
  customized: unknown
): unknown =>
  isArray(customized) || React.isValidElement(customized as never)
    ? customized
    : undefined // fall back to default merge logic

declare global {
  interface VekkuliWindowConfig {
    commonCustomizations?: Partial<JsonOf<CommonCustomizations>>
  }
}
