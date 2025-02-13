import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'

import {BoatSpaceType} from "../../../shared/types";

interface StorageInfoProps {
    boatSpaceType: BoatSpaceType
}

export const StorageInfo = React.memo(function StorageInfo({
  boatSpaceType,
}: StorageInfoProps) {
  const i18n = useTranslation()
  return (boatSpaceType === 'Winter' || boatSpaceType === 'Storage') ? (
      <div className="block">
          <p className="body">{i18n.reservation.searchPage.filters.storageInfo}</p>
      </div>
  ) : null
})
