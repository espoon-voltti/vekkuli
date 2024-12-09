import { BoatSpaceType } from '../reservation/pages/chooseBoatSpace/config'
import { BoatSpaceAmenity, BoatType } from '../shared/types'

export interface Place {
  name: string
  id: number
}

export interface PlaceWithSpaces {
  place: Place
  spaces: Space[]
}

export interface Size {
  width: number
  length: number
}

export interface Space {
  size: Size
  price: number
  id: number
  amenity: string
  identifier: string
}

export interface FreeSpacesResponse {
  placesWithFreeSpaces: PlaceWithSpaces[]
  count: number
}

export interface SearchFreeSpacesParams {
  boatType: BoatType
  spaceType: BoatSpaceType
  amenities: BoatSpaceAmenity[]
  harbor: string[]
  width: number
  length: number
}
