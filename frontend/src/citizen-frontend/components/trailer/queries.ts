import { UpdateStorageTypeRequest } from 'citizen-frontend/api-types/reservation'

import { createMutationDisabledDefault } from '../util'

export const updateStorageTypeDisabled = createMutationDisabledDefault<
  UpdateStorageTypeRequest,
  void
>()
