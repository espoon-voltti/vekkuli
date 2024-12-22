import { multiSelect, object, required } from 'lib-common/form/form'
import { Translations } from 'lib-customizations/vekkuli/citizen'

export const userAgreementForm = object({
  agreements: required(multiSelect<boolean>())
})
export type UserAgreementForm = typeof userAgreementForm

export default function initialFormState(i18n: Translations) {
  return {
    userAgreement: {
      agreements: {
        domValues: [],
        options: [
          {
            domValue: 'certify',
            label: i18n.reservation.certify,
            value: true
          },
          {
            domValue: 'agreeToRules',
            label: i18n.reservation.agreeToRules,
            value: true
          }
        ]
      }
    }
  }
}
