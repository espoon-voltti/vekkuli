// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import mergeWith from 'lodash/mergeWith'

import { JsonOf } from 'lib-common/json'
import defaultsUntyped from 'lib-customizations/espoo/citizen'

import { mergeCustomizer } from './common'
import en from './defaults/citizen/i18n/en'
import fi from './defaults/citizen/i18n/fi'
import sv from './defaults/citizen/i18n/sv'
import type { CitizenCustomizations } from './types'

const defaults: CitizenCustomizations = defaultsUntyped

declare global {
  interface VekkuliWindowConfig {
    citizenCustomizations?: Partial<JsonOf<CitizenCustomizations>>
  }
}

const overrides =
  typeof window !== 'undefined'
    ? window.vekkuli?.citizenCustomizations
    : undefined

const customizations: CitizenCustomizations = overrides
  ? mergeWith({}, defaults, overrides, mergeCustomizer)
  : defaults

const { appConfig, cityLogo, langs }: CitizenCustomizations = customizations
export { appConfig, cityLogo, langs }

export type Lang = 'fi' | 'sv' | 'en'

export type Translations = typeof fi

export const translations: Record<Lang, Translations> = {
  fi: mergeWith({}, fi, customizations.translations.fi, mergeCustomizer),
  sv: mergeWith({}, sv, customizations.translations.sv, mergeCustomizer),
  en: mergeWith({}, en, customizations.translations.en, mergeCustomizer)
}
