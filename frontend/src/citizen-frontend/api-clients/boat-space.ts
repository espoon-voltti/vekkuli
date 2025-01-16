import { client } from 'citizen-frontend/api-client'
import { BoatSpace } from 'citizen-frontend/shared/types'
import { uri } from 'lib-common/uri'

export async function getBoatSpace(spaceId: number): Promise<BoatSpace> {
  const { data: json } = await client.request<BoatSpace>({
    url: uri`/boat-space/${spaceId}`.toString(),
    method: 'GET'
  })
  return json
}
