import { multiSelect, object } from '../../../lib-common/form/form'
import { Translations } from '../../../lib-customizations/vekkuli/citizen'
import { StateOf } from '../../../lib-common/form/types'

export const showBoatsForm = object({
  show: multiSelect<boolean>()
})

export const initShowBoatsForm = (
  i18n: Translations
): StateOf<typeof showBoatsForm> => ({
  show: {
    domValues: [],
    options: [
      {
        domValue: 'show',
        label: 'Näytä myös veneet joita ei ole liitetty venepaikkoihin',
        value: true
      }
    ]
  }
})
