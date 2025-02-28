import { useIsMutating } from '@tanstack/react-query'
import { Button } from 'lib-components/dom'
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
  const isLoading = useIsMutating() > 0
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
        <Button
          action={() => onReserveSpace(space.id)}
          type="primary"
          aria-label={`${i18n.boatSpace.reserve} : ${placeName} ${space.identifier}`}
          loading={isLoading}
        >
          {i18n.boatSpace.reserve}
        </Button>
      </td>
    </tr>
  )
})
