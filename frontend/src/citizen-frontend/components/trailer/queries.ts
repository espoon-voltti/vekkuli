import { UpdateTrailerRequest } from 'citizen-frontend/api-clients/trailer'

import { createMutationDisabledDefault } from '../util'

export const updateTrailerDisabled = createMutationDisabledDefault<
  UpdateTrailerRequest,
  void
>()
