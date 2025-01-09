// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

export type Env = 'staging' | 'prod'

export const env = (): Env | 'default' => {
  if (window.location.host === 'varaukset.espoo.fi') {
    return 'prod'
  }

  if (window.location.host === 'staging.varaukset.espoo.fi') {
    return 'staging'
  }

  return 'default'
}
