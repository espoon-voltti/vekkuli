import { client } from 'citizen-frontend/api-client'
import { UpdateCitizenTrailerInput } from 'citizen-frontend/api-types/trailer'
import { uri } from 'lib-common/uri'

export async function updateCitizenTrailer(
  input: UpdateCitizenTrailerInput
): Promise<void> {
  await client.request<void>({
    url: uri`/current/update-trailer`.toString(),
    method: 'POST',
    data: input
  })
}
