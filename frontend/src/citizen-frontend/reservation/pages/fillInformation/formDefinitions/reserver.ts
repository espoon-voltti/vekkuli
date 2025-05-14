import { Citizen } from 'citizen-frontend/shared/types'
import { whitespaceTrimmedString } from 'lib-common/form/fields'
import { object, required } from 'lib-common/form/form'

export const reserverForm = object({
  email: required(whitespaceTrimmedString()),
  phone: required(whitespaceTrimmedString())
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
