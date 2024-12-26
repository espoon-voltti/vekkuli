import { string } from 'lib-common/form/fields'
import { object, required } from 'lib-common/form/form'

import { Citizen } from '../../../../shared/types'

export const reserverForm = object({
  email: required(string()),
  phone: required(string())
})
export type ReserverForm = typeof reserverForm

export default function initialFormState(reserver: Citizen) {
  return {
    reserver: {
      email: reserver.email,
      phone: reserver.phone
    }
  }
}
