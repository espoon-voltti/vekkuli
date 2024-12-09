// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { BaseAppConfig } from 'lib-customizations/types'

import { env, Env } from './env'

type AppConfigs = {
  default: BaseAppConfig
} & Record<Env, BaseAppConfig>

const citizenConfigs: AppConfigs = {
  default: {},
  staging: {},
  prod: {}
}

const citizenConfig = citizenConfigs[env()]

export { citizenConfig }
