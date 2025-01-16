import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import {
  formatDimensions,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'
import { BoatSpace } from 'citizen-frontend/shared/types'

type BoatSpaceInformationProps = {
  boatSpace: BoatSpace
}

export default React.memo(function BoatSpaceInformation({
  boatSpace
}: BoatSpaceInformationProps) {
  const i18n = useTranslation()
  return (
    <ul className="no-bullets">
      <li>
        {formatPlaceIdentifier(
          boatSpace.section,
          boatSpace.placeNumber,
          boatSpace.locationName
        )}
      </li>
      <li>
        {formatDimensions({
          width: boatSpace.width,
          length: boatSpace.length
        })}
      </li>
      {boatSpace.amenity && (
        <li>{i18n.boatSpace.amenities[boatSpace.amenity]}</li>
      )}
    </ul>
  )
})
