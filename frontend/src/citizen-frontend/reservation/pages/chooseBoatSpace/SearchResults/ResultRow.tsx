import React from 'react'

import { Space } from 'citizen-frontend/api-types/free-spaces'
import { useTranslation } from 'citizen-frontend/localization'

import { PriceFormat, SpaceSize } from './utils'

interface ResultRowProps {
  space: Space
  placeName: string
  onReserveSpace: (spaceId: number) => void
}

export const ResultRow = React.memo(function ResultRow({
  space,
  placeName,
  onReserveSpace
}: ResultRowProps) {
  const i18n = useTranslation()
  return (
    <tr>
      <td>{space.amenity !== 'Buoy' && <SpaceSize size={space.size} />}</td>
      <td>{i18n.boatSpace.amenities[space.amenity]} </td>
      <td>
        <PriceFormat price={space.price} />
      </td>
      <td>{space.identifier}</td>
      <td>
        <button
          className="button is-primary reserve-button"
          onClick={() => onReserveSpace(space.id)}
          aria-label={`${i18n.boatSpace.reserve} : ${placeName} ${space.identifier}`}
        >
          {i18n.boatSpace.reserve}
        </button>
      </td>
    </tr>
  )
})
