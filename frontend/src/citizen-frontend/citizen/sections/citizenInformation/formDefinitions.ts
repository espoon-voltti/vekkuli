import { User } from 'citizen-frontend/auth/state'
import { string } from 'lib-common/form/fields'
import { object, required } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'

export const citizenInformationForm = object({
  email: required(string()),
  phone: required(string())
})
export type CitizenInformationForm = typeof citizenInformationForm

export function initialFormState(user: User): StateOf<CitizenInformationForm> {
  return {
    email: user.email,
    phone: user.phone
  }
}
