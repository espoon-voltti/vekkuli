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
