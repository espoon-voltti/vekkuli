import { Translations } from 'citizen-frontend/localization'
import {
  buckTypes,
  StorageType,
  storageTypes,
  Trailer,
  trailerTypes
} from 'citizen-frontend/shared/types'
import { optionalNumber, positiveNumber, string } from 'lib-common/form/fields'
import { mapped, object, oneOf, required, union } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'

export const storageTypeForm = oneOf<StorageType>()

export const trailerInfoForm = object({
  id: optionalNumber(),
  length: required(positiveNumber()),
  width: required(positiveNumber()),
  registrationNumber: required(string())
})

export type TrailerInfoForm = typeof trailerInfoForm

export const trailerStorageForm = mapped(
  object({
    storageType: storageTypeForm,
    trailerInfo: trailerInfoForm
  }),
  ({ trailerInfo }) => {
    return {
      storageType: trailerTypes,
      trailerInfo: {
        id: trailerInfo.id || undefined,
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
      storageType: buckTypes
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
    trailerInfo: {
      id: trailer?.id || undefined,
      length: trailer?.length.toString() || '',
      width: trailer?.width.toString() || '',
      registrationNumber: trailer?.registrationNumber || ''
    }
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
