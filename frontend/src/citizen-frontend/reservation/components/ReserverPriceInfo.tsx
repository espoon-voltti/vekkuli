import React from 'react'

import { BlueInfoCircle } from '../../../lib-icons'
import { useTranslation } from '../../localization'
import { parsePrice } from '../../shared/formatters'
import { ReservationInfoForReservation } from '../ReservationInfoForReservation'

export default React.memo(function ReserverPriceInfo({
  reservationInfoForReservation
}: {
  reservationInfoForReservation: ReservationInfoForReservation
}) {
  const {
    discountPercentage,
    revisedPriceWithDiscountInEuro,
    revisedPriceInEuro,
    creationType
  } = reservationInfoForReservation
  const i18n = useTranslation()
  const numericPrice = parsePrice(revisedPriceInEuro)

  const getSwitchInfoText = () => {
    if (numericPrice > 0) {
      return i18n.reservation.paymentInfo.moreExpensive(revisedPriceInEuro)
    } else if (numericPrice < 0) {
      return i18n.reservation.paymentInfo.lessExpensive
    }
    return i18n.reservation.paymentInfo.equal
  }

  const getDiscountText = () => {
    const { reserverType, name, discountPercentage } =
      reservationInfoForReservation
    return i18n.reservation.reserverDiscountInfo(
      reserverType,
      name ?? '',
      discountPercentage,
      revisedPriceWithDiscountInEuro
    )
  }

  const switchText = creationType === 'Switch' ? getSwitchInfoText() : null
  const discountText = discountPercentage > 0 ? getDiscountText() : null
  const showDiscountText =
    discountText && (!switchText || (switchText && numericPrice > 0))

  return switchText || discountText ? (
    <div
      id="empty-dimensions-warning"
      className="message-box column is-four-fifths"
    >
      <div className="column is-narrow">
        <span className="icon">
          <BlueInfoCircle />
        </span>
      </div>
      <p data-testid="reservation-info-text" className="column">
        {!!switchText && <span>{switchText}</span>}
        {!!showDiscountText && <span>{discountText}</span>}
      </p>
    </div>
  ) : (
    <></>
  )
})
