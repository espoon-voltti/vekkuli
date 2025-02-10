import { Translations } from 'citizen-frontend/localization'
import { BoundForm } from 'lib-common/form/hooks'
import { AnyForm } from 'lib-common/form/types'

export function bindOrPlaceholders<T extends AnyForm>(bind?: BoundForm<T>) {
  if (bind) {
    return bind
  }
  return {
    state: undefined,
    set: () => undefined,
    update: () => undefined,
    isValid: () => true,
    validationError: () => '',
    translateError: () => ''
  }
}

export function getI18nLabel(
  labelValue: string | ((i18n: Translations) => string) | undefined,
  i18n: Translations
) {
  return labelValue instanceof Function ? labelValue(i18n) : labelValue
}
