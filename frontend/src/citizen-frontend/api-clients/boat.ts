import { uri } from '../../lib-common/uri'
import { client } from '../api-client'
import { UpdateCitizenBoatInput } from '../api-types/boat'

export async function updateCitizenBoat(
  input: UpdateCitizenBoatInput
): Promise<void> {
  await client.request<void>({
    url: uri`/current/update-boat`.toString(),
    method: 'POST',
    data: input
  })
}

export async function deleteBoat(boatId: number): Promise<void> {
  await client.request({
    url: uri`/current/boats/${boatId}`.toString(),
    method: 'DELETE'
  })
}
