import { updateTrailer } from 'citizen-frontend/api-clients/trailer'
import { mutation } from 'lib-common/query'

export const updateTrailerMutation = mutation({
  api: updateTrailer
})
