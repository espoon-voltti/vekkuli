import { multiSelect, object, validated } from 'lib-common/form/form'
import { Translations } from 'lib-customizations/vekkuli/citizen'

export const userAgreementForm = object({
  certified: validated(multiSelect<boolean>(), (certified) => {
    return certified !== undefined && certified?.length > 0
      ? undefined
      : 'certify'
  }),
  terms: validated(multiSelect<boolean>(), (terms) => {
    return terms !== undefined && terms?.length > 0 ? undefined : 'terms'
  })
})
export type UserAgreementForm = typeof userAgreementForm

export default function initialFormState() {
  return {
    userAgreement: {
      certified: {
        domValues: [],
        options: [
          {
            domValue: 'certify',
            label: (i18n: Translations) => i18n.reservation.certify,
            value: true
          }
        ]
      },
      terms: {
        domValues: [],
        options: [
          {
            domValue: 'agreeToRules',
            label: (i18n: Translations) => i18n.reservation.agreeToRules,
            value: true
          }
        ]
      }
    }
  }
}
