import { client } from 'citizen-frontend/api-client'
import { UpdateTrailerInput } from 'citizen-frontend/api-types/trailer'
import { TrailerId } from 'citizen-frontend/shared/types'
import { uri } from 'lib-common/uri'

export type UpdateTrailerRequest = {
  trailerId: TrailerId
  input: UpdateTrailerInput
}

export async function updateTrailer({
  trailerId,
  input
}: UpdateTrailerRequest): Promise<void> {
  await client.request<void>({
    url: uri`/trailer/${trailerId}`.toString(),
    method: 'PATCH',
    data: input
  })
}
