import { Trailer } from 'citizen-frontend/shared/types'
import { number, positiveNumber, string } from 'lib-common/form/fields'
import { object, required } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'

export const trailerInformationForm = object({
  id: required(number()),
  length: required(positiveNumber()),
  width: required(positiveNumber()),
  registrationNumber: required(string())
})
export type TrailerInformationForm = typeof trailerInformationForm

export function initialFormState(
  trailer: Trailer
): StateOf<TrailerInformationForm> {
  return {
    id: trailer.id,
    length: trailer.length.toString(),
    width: trailer.width.toString(),
    registrationNumber: trailer.registrationNumber
  }
}
