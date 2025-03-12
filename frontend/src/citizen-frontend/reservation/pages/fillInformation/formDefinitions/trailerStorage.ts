import { formatInputNumberValue } from 'citizen-frontend/shared/formatters'
import { Trailer } from 'citizen-frontend/shared/types'
import { positiveNumber, string } from 'lib-common/form/fields'
import { mapped, object, required } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'

import { StoredSearchState } from '../../useStoredSearchState'

export const trailerInfoForm = object({
  length: required(positiveNumber()),
  width: required(positiveNumber()),
  registrationNumber: required(string())
})

export type TrailerInfoForm = typeof trailerInfoForm

export const trailerStorageForm = mapped(
  object({
    trailerInfo: trailerInfoForm
  }),
  ({ trailerInfo }) => {
    return {
      storageType: 'Trailer' as const,
      trailerInfo: {
        length: trailerInfo.length,
        width: trailerInfo.width,
        registrationNumber: trailerInfo.registrationNumber
      }
    }
  }
)

export type TrailerStorageForm = typeof trailerStorageForm

export default function initialFormState(
  storedState?: StoredSearchState,
  initialTrailer?: Trailer
): StateOf<TrailerStorageForm> {
  return {
    trailerInfo: initialTrailerInfoState(storedState, initialTrailer)
  }
}

function initialTrailerInfoState(
  storedState?: StoredSearchState,
  initialTrailer?: Trailer
): StateOf<TrailerInfoForm> {
  let width = initialTrailer?.width.toString() ?? positiveNumber.empty().value
  let length = initialTrailer?.length.toString() ?? positiveNumber.empty().value

  if (storedState && storedState.Trailer !== undefined) {
    width = formatInputNumberValue(storedState.Trailer.width)
    length = formatInputNumberValue(storedState.Trailer.length)
  }
  return {
    registrationNumber: initialTrailer?.registrationNumber ?? '',
    width: width,
    length: length
  }
}
