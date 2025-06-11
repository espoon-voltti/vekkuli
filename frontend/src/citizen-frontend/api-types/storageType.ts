import { StorageType } from 'citizen-frontend/shared/types'

import { UpdateTrailerInput } from './trailer'

export type UpdateTrailerInputWithId = UpdateTrailerInput & {
  id?: number
}

export type UpdateStorageTypeInput = {
  storageType: StorageType
  trailerInfo?: UpdateTrailerInputWithId
}
