import { User } from 'citizen-frontend/auth/state'
import { whitespaceTrimmedString } from 'lib-common/form/fields'
import { object, required, validated } from 'lib-common/form/form'
import { email, phone } from 'lib-common/form/form-validation'
import { StateOf } from 'lib-common/form/types'

export const citizenInformationForm = object({
  email: validated(required(whitespaceTrimmedString()), email),
  phone: validated(required(whitespaceTrimmedString()), phone)
})

export type CitizenInformationForm = typeof citizenInformationForm

export function initialFormState(user: User): StateOf<CitizenInformationForm> {
  return {
    email: user.email,
    phone: user.phone
  }
}