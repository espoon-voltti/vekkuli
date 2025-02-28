import {
  StorageType,
  storageTypes
} from 'citizen-frontend/shared/types'
import { positiveNumber, string } from 'lib-common/form/fields'
import { mapped, object, oneOf, required, union } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'


export type StorageAmenity = 'Buck' | 'Trailer'
const buckStorageTypes = storageTypes.filter((type) => type !== 'Trailer')
export const storageTypeForm = oneOf<StorageType>()
export type StorageTypeForm = typeof storageTypeForm

export const trailerInfoForm = object({
  length: required(positiveNumber()),
  width: required(positiveNumber()),
  registrationNumber: required(string())
})

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
  i18n: Translations
): StateOf<AllYearStorageForm> {
  return {
    storageInfo: initialStorageInfoState(
      branch,
      i18n
    )
  }
}

function initialStorageInfoState(
  branch: StorageAmenity,
  i18n: Translations
): StateOf<StorageTypeUnionForm> {
  switch (branch) {
    case 'Trailer':
      return {
        branch,
        state: {
          registrationNumber: '',
          width: positiveNumber.empty().value,
          length: positiveNumber.empty().value
        }
      }
    case 'Buck':
      return {
        branch,
        state: initialStorageTypeState(i18n)
      }
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
