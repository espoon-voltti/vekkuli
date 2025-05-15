import {
  BoatSpaceAmenity,
  BoatSpaceType,
  BoatType,
  HarborId
} from '../shared/types'

export interface Place {
  address: string
  id: HarborId
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
  amenity: BoatSpaceAmenity
  identifier: string
}

export interface FreeSpacesResponse {
  placesWithFreeSpaces: PlaceWithSpaces[]
  count: number
}

export interface SearchFreeSpacesParams {
  boatType?: BoatType
  spaceType: BoatSpaceType
  amenities: BoatSpaceAmenity[]
  harbor: string[]
  width: number
  length: number
}
