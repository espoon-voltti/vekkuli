import { UpdateBoatRequest } from 'citizen-frontend/api-clients/boat'
import { BoatId } from 'citizen-frontend/shared/types'

import { createMutationDisabledDefault } from '../util'

export const deleteBoatDisabled = createMutationDisabledDefault<BoatId, void>()

export const updateBoatDisabled = createMutationDisabledDefault<
  UpdateBoatRequest,
  void
>()
