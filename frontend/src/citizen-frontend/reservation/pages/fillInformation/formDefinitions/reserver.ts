import { Citizen } from 'citizen-frontend/shared/types'
import { string } from 'lib-common/form/fields'
import { object, required } from 'lib-common/form/form'

export const reserverForm = object({
  email: required(string()),
  phone: required(string())
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
