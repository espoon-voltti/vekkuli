import { StorageType } from 'citizen-frontend/shared/types'

import { UpdateTrailerInput } from './trailer'

export type UpdateStorageTypeInput = {
  storageType: StorageType
  trailerInfo?: UpdateTrailerInput
}
