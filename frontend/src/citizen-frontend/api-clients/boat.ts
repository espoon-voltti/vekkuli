import { client } from 'citizen-frontend/api-client'
import { UpdateBoatInput } from 'citizen-frontend/api-types/boat'
import { BoatId } from 'citizen-frontend/shared/types'
import { uri } from 'lib-common/uri'

export type UpdateBoatRequest = {
  boatId: BoatId
  input: UpdateBoatInput
}

export async function updateBoat({
  boatId,
  input
}: UpdateBoatRequest): Promise<void> {
  await client.request<void>({
    url: uri`/boats/${boatId}`.toString(),
    method: 'PATCH',
    data: input
  })
}

export async function deleteBoat(boatId: number): Promise<void> {
  await client.request({
    url: uri`/boats/${boatId}`.toString(),
    method: 'DELETE'
  })
}
