import {
  StorageType,
  storageTypes,
  Trailer
} from 'citizen-frontend/shared/types'
import { positiveNumber, string } from 'lib-common/form/fields'
import { mapped, object, oneOf, required, union } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { StoredSearchState } from '../../useStoredSearchState'

export type StorageAmenity = 'Buck' | 'Trailer'
const buckStorageTypes = storageTypes.filter((type) => type !== 'Trailer')
export const storageTypeForm = oneOf<StorageType>()
export type StorageTypeForm = typeof storageTypeForm

export const trailerInfoForm = object({
  length: required(positiveNumber()),
  width: required(positiveNumber()),
  registrationNumber: required(string())
})
export type TrailerInfoForm = typeof trailerInfoForm

const storageTypeUnionForm = union({
  Trailer: trailerInfoForm,
  Buck: storageTypeForm
})

export type StorageTypeUnionForm = typeof storageTypeUnionForm

export const allYearStorageForm = mapped(
  object({
    storageInfo: storageTypeUnionForm
  }),
  ({ storageInfo }) => {
    return {
      storageType:
        storageInfo.branch === 'Buck' ? storageInfo.value : 'Trailer',
      trailerInfo:
        storageInfo.branch === 'Trailer'
          ? {
              length: storageInfo.value.length,
              width: storageInfo.value.width,
              registrationNumber: storageInfo.value.registrationNumber
            }
          : null
    }
  }
)
export type AllYearStorageForm = typeof allYearStorageForm

export default function initialFormState(
  branch: StorageAmenity,
  i18n: Translations,
  storedState?: StoredSearchState,
  initialTrailer?: Trailer
): StateOf<AllYearStorageForm> {
  return {
    storageInfo: initialStorageInfoState(
      branch,
      i18n,
      storedState,
      initialTrailer
    )
  }
}

function initialStorageInfoState(
  branch: StorageAmenity,
  i18n: Translations,
  storedState?: StoredSearchState,
  initialTrailer?: Trailer
): StateOf<StorageTypeUnionForm> {
  switch (branch) {
    case 'Trailer':
      return {
        branch,
        state: initialTrailerInfoState(storedState, initialTrailer)
      }
    case 'Buck':
      return {
        branch,
        state: initialStorageTypeState(i18n)
      }
  }
}

function initialTrailerInfoState(
  storedState?: StoredSearchState,
  initialTrailer?: Trailer
): StateOf<TrailerInfoForm> {
  return {
    registrationNumber: initialTrailer?.registrationNumber ?? '',
    width:
      storedState?.width ??
      initialTrailer?.registrationNumber ??
      positiveNumber.empty().value,
    length:
      storedState?.length ??
      initialTrailer?.registrationNumber ??
      positiveNumber.empty().value
  }
}

const initialStorageTypeState = (
  i18n: Translations
): StateOf<StorageTypeForm> => ({
  domValue: 'Buck',
  options: buckStorageTypes.map((type) => ({
    domValue: type,
    label: i18n.boatSpace.winterStorageType[type],
    value: type
  }))
})
