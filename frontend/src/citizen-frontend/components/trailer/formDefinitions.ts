import { Translations } from 'citizen-frontend/localization'
import {
  StorageType,
  storageTypes,
  Trailer
} from 'citizen-frontend/shared/types'
import { positiveNumber, string } from 'lib-common/form/fields'
import { mapped, object, oneOf, required, union } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'

export const storageTypeForm = required(oneOf<StorageType>())

export const trailerInfoForm = object({
  length: required(positiveNumber()),
  width: required(positiveNumber()),
  registrationNumber: required(string())
})

export type TrailerInfoForm = typeof trailerInfoForm

function initialTrailerInfoState(
  initialTrailer?: Trailer
): StateOf<TrailerInfoForm> {
  return {
    registrationNumber: initialTrailer?.registrationNumber ?? '',
    width: initialTrailer?.width.toString() ?? positiveNumber.empty().value,
    length: initialTrailer?.length.toString() ?? positiveNumber.empty().value
  }
}

export const trailerStorageForm = mapped(
  object({
    storageType: storageTypeForm,
    trailerInfo: trailerInfoForm
  }),
  ({ trailerInfo, storageType }) => {
    return {
      storageType: storageType,
      trailerInfo: {
        length: trailerInfo.length,
        width: trailerInfo.width,
        registrationNumber: trailerInfo.registrationNumber
      }
    }
  }
)

export const buckStorageTypeForm = mapped(
  object({
    storageType: storageTypeForm
  }),
  ({ storageType }) => {
    return {
      storageType: storageType
    }
  }
)

export type TrailerStorageForm = typeof trailerStorageForm
export type BuckStorageTypeForm = typeof buckStorageTypeForm

export type StorageTypeInfoForm = TrailerStorageForm | BuckStorageTypeForm

export const storageTypeInfoUnionForm = union({
  Trailer: trailerStorageForm,
  Buck: buckStorageTypeForm,
  BuckWithTent: buckStorageTypeForm
})
export type StorageTypeInfoUnionForm = typeof storageTypeInfoUnionForm

function initialTrailerFormState(
  i18n: Translations,
  storageType: StorageType = 'Trailer',
  trailer?: Trailer
): StateOf<TrailerStorageForm> {
  return {
    storageType: {
      domValue: storageType,
      options: storageTypes
        .map((type) => ({
          domValue: type,
          label: type ? i18n.boatSpace.winterStorageType[type] : '-',
          value: type
        }))
        .sort((a, b) => a.label.localeCompare(b.label))
    },
    trailerInfo: initialTrailerInfoState(trailer)
  }
}

function initialBuckFormState(
  i18n: Translations,
  storageType?: StorageType
): StateOf<BuckStorageTypeForm> {
  return {
    storageType: {
      domValue: storageType || '-',
      options: storageTypes
        .map((type) => ({
          domValue: type,
          label: type ? i18n.boatSpace.winterStorageType[type] : '-',
          value: type
        }))
        .sort((a, b) => a.label.localeCompare(b.label))
    }
  }
}

export function initialFormState(
  i18n: Translations,
  storageType?: StorageType,
  trailer?: Trailer
): StateOf<StorageTypeInfoUnionForm> {
  switch (storageType) {
    case 'Trailer':
      return {
        branch: storageType,
        state: initialTrailerFormState(i18n, storageType, trailer)
      }
    case 'Buck':
    case 'BuckWithTent':
      return {
        branch: storageType,
        state: initialBuckFormState(i18n, storageType)
      }
    default:
      return {
        branch: 'Buck',
        state: initialBuckFormState(i18n, storageType)
      }
  }
}

export function onStorageTypeInfoFormUpdate({
  prev,
  next,
  trailer
}: {
  prev: StateOf<StorageTypeInfoUnionForm>
  next: StateOf<StorageTypeInfoUnionForm>
  trailer?: Trailer
}): StateOf<StorageTypeInfoUnionForm> {
  const prevType = prev.state.storageType.domValue as StorageType
  const nextType = next.state.storageType.domValue as StorageType
  if (prevType !== nextType) {
    switch (nextType) {
      case 'Trailer':
        return {
          branch: nextType,
          state: {
            storageType: next.state.storageType,
            trailerInfo: initialTrailerInfoState(trailer)
          }
        }
      case 'Buck':
      case 'BuckWithTent':
        return {
          branch: nextType,
          state: {
            storageType: next.state.storageType
          }
        }
    }
  }

  return next
}
