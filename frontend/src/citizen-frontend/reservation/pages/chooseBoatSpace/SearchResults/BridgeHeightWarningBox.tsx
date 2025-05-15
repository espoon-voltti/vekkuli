import React from 'react'

import { Translations, useTranslation } from 'citizen-frontend/localization'
import { InfoBox } from 'citizen-frontend/reservation/components/InfoBox'
import { HarborId } from 'citizen-frontend/shared/types'

const BridgeHeightLimitedLocations = {
  Laajalahti: '3',
  Otsolahti: '4'
} as const

const bridgeHeightLimitedLocationIds: string[] = Object.values(
  BridgeHeightLimitedLocations
)

const getBridgeHeightWarningByLocationId = (
  locationId: HarborId,
  i18n: Translations
) => {
  switch (locationId) {
    case BridgeHeightLimitedLocations.Laajalahti:
      return i18n.boatSpace.heightWarning.bridges('3,5')
    case BridgeHeightLimitedLocations.Otsolahti:
      return i18n.boatSpace.heightWarning.bridge('3,0')
    default:
      return ''
  }
}

export const BridgeHeightWarningBox = React.memo(
  function BridgeHeightWarningBox({ placeId }: { placeId: HarborId }) {
    const i18n = useTranslation()

    if (!bridgeHeightLimitedLocationIds.includes(placeId)) {
      return null
    }

    return (
      <InfoBox
        fullWidth
        isWarning
        text={getBridgeHeightWarningByLocationId(placeId, i18n)}
      />
    )
  }
)
