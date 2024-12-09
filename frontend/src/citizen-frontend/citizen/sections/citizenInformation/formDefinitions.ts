import { string } from 'lib-common/form/fields'
import { object, required } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'

const citizenInformationForm = object({
  email: required(string()),
  phone: required(string())
})
export type CitizenInformationForm = typeof citizenInformationForm

export function initialFormState(): StateOf<CitizenInformationForm> {
  return {
    email: '',
    phone: ''
  }
}
