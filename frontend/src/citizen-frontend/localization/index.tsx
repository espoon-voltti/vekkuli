import { LocalizationContextProvider } from './state'

export { useLang, useTranslation } from './state'
export { langs } from 'lib-customizations/vekkuli/citizen'
export type { Lang, Translations } from 'lib-customizations/vekkuli/citizen'

export const Localization = LocalizationContextProvider
