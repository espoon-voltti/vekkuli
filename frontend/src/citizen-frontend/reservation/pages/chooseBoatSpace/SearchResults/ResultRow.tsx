import React from 'react'

import { Space } from 'citizen-frontend/api-types/free-spaces'

import { PriceFormat, SpaceSize } from './utils'

interface ResultRowProps {
  space: Space
  onReserveSpace: (spaceId: number) => void
}

export const ResultRow = React.memo(function ResultRow({
  space,
  onReserveSpace
}: ResultRowProps) {
  return (
    <tr>
      <td>
        <SpaceSize size={space.size} />
      </td>
      <td>{space.amenity}</td>
      <td>
        <PriceFormat price={space.price} />
      </td>
      <td>{space.identifier}</td>
      <td>
        <button
          className="button is-primary reserve-button"
          onClick={() => onReserveSpace(space.id)}
        >
          Varaa
        </button>
      </td>
    </tr>
  )
})
