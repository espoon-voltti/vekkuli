import {
  StorageType,
  storageTypes,
  Trailer
} from 'citizen-frontend/shared/types'
import { positiveNumber, string } from 'lib-common/form/fields'
import {
  mapped,
  object,
  oneOf,
  required,
  union,
  value
} from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

export const storageTypeForm = oneOf<StorageType>()
export type StorageTypeForm = typeof storageTypeForm

export const trailerInfoForm = object({
  length: required(positiveNumber()),
  width: required(positiveNumber()),
  registrationNumber: required(string())
})
export type TrailerInfoForm = typeof trailerInfoForm

export const trailerInfoUnionForm = union({
  Trailer: trailerInfoForm,
  Buck: value<null>(),
  BuckWithTent: value<null>()
})
export type TrailerInfoUnionForm = typeof trailerInfoUnionForm

export const winterStorageForm = mapped(
  object({
    storageType: storageTypeForm,
    trailerInfo: trailerInfoUnionForm
  }),
  ({ storageType, trailerInfo }) => {
    return {
      storageType,
      trailerInfo:
        trailerInfo.branch === 'Trailer'
          ? {
              length: trailerInfo.value.length,
              width: trailerInfo.value.width,
              registrationNumber: trailerInfo.value.registrationNumber
            }
          : null
    }
  }
)
export type WinterStorageForm = typeof winterStorageForm

export function onWinterStorageFormUpdate({
  prev,
  next
}: {
  prev: StateOf<WinterStorageForm>
  next: StateOf<WinterStorageForm>
}): StateOf<WinterStorageForm> {
  const prevStorageType = prev.storageType.domValue as StorageType
  const nextStorageType = next.storageType.domValue as StorageType
  // Boat has been changed, we need to update the form values
  if (prevStorageType !== nextStorageType) {
    switch (nextStorageType) {
      case 'Trailer':
        return {
          ...next,
          trailerInfo: initialTrailerInfoState()
        }
      case 'Buck':
      case 'BuckWithTent':
        return {
          ...next,
          trailerInfo: {
            branch: nextStorageType,
            state: null
          }
        }
    }
  }

  return next
}

export default function initialFormState(
  i18n: Translations,
  initialTrailer?: Trailer
): StateOf<WinterStorageForm> {
  return {
    storageType: initialStorageTypeState(i18n),
    trailerInfo: initialTrailerInfoState(initialTrailer)
  }
}

function initialTrailerInfoState(
  initialTrailer?: Trailer
): StateOf<TrailerInfoUnionForm> {
  return {
    branch: 'Trailer',
    state: {
      registrationNumber: initialTrailer?.registrationNumber ?? '',
      width: initialTrailer?.width.toString() ?? positiveNumber.empty().value,
      length: initialTrailer?.length.toString() ?? positiveNumber.empty().value
    }
  }
}

const initialStorageTypeState = (
  i18n: Translations
): StateOf<StorageTypeForm> => ({
  domValue: 'Trailer',
  options: storageTypes.map((type) => ({
    domValue: type,
    label: i18n.boatSpace.winterStorageType[type],
    value: type
  }))
})
