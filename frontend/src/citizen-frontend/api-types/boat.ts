import { Boat } from 'citizen-frontend/shared/types'

export type UpdateBoatInput = Omit<Boat, 'id'>
