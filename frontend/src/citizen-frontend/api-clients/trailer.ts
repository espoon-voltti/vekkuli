import { uri } from 'lib-common/uri'

import { client } from '../api-client'
import { UpdateCitizenTrailerInput } from '../api-types/trailer'

export async function updateCitizenTrailer(
  input: UpdateCitizenTrailerInput
): Promise<void> {
  await client.request<void>({
    url: uri`/current/update-trailer`.toString(),
    method: 'POST',
    data: input
  })
}
