import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { InfoBox } from 'citizen-frontend/reservation/components/InfoBox'
import { parsePrice } from 'citizen-frontend/shared/formatters'

export default React.memo(function SwitchPriceInfoBox({
  priceDifference
}: {
  priceDifference: string
}) {
  const i18n = useTranslation()

  const revisedPrice = parsePrice(priceDifference)

  const text = () => {
    if (revisedPrice > 0)
      return i18n.reservation.paymentInfo.moreExpensive(priceDifference)
    else if (revisedPrice < 0) return i18n.reservation.paymentInfo.lessExpensive
    return i18n.reservation.paymentInfo.equal
  }

  return <InfoBox text={text()} />
})
