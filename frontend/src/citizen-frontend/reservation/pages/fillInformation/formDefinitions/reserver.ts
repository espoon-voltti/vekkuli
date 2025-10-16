import { Citizen } from 'citizen-frontend/shared/types'
import {
  whitespaceTrimmedString,
  phoneNumberTrimmedString
} from 'lib-common/form/fields'
import { object, required, validated } from 'lib-common/form/form'
import { validEmail, validPhone } from 'lib-common/form/form-validation'

export const reserverForm = object({
  phone: validated(required(phoneNumberTrimmedString()), validPhone),
  email: validated(required(whitespaceTrimmedString()), validEmail)
})
export type ReserverForm = typeof reserverForm

export default function initialFormState(reserver: Citizen | undefined) {
  if (!reserver) {
    return {
      reserver: {
        email: '',
        phone: ''
      }
    }
  }
  return {
    reserver: {
      email: reserver.email,
      phone: reserver.phone
    }
  }
}
