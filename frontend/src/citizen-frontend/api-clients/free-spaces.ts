import { uri } from 'lib-common/uri'

import { client } from '../api-client'
import {
  FreeSpacesResponse,
  SearchFreeSpacesParams
} from '../api-types/free-spaces'

export function deserializeJsonFreeSpacesResponse(
  json: FreeSpacesResponse
): FreeSpacesResponse {
  return {
    placesWithFreeSpaces: json.placesWithFreeSpaces || [],
    count: json.count || 0
  }
}

export async function getFreeSpaces(
  params: SearchFreeSpacesParams | undefined
): Promise<FreeSpacesResponse> {
  const { data: json } = await client.request<FreeSpacesResponse>({
    url: uri`/public/free-spaces/search`.toString(),
    method: 'GET',
    params: params
  })
  return deserializeJsonFreeSpacesResponse(json)
}
