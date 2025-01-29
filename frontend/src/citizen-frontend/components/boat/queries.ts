import { deleteBoat, updateBoat } from 'citizen-frontend/api-clients/boat'
import { queryKeys } from 'citizen-frontend/shared/queries'
import { mutation } from 'lib-common/query'

export const deleteBoatMutation = mutation({
  api: deleteBoat,
  invalidateQueryKeys: () => [queryKeys.citizenBoats()]
})

export const updateBoatMutation = mutation({
  api: updateBoat
})
