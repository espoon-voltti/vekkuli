import React from 'react'

import {BlueInfoCircle} from '../../../lib-icons'
import {useTranslation} from '../../localization'
import {formatCentsToEuros} from '../../shared/formatters'
import {ReservationPriceInfo} from "../pages/fillInformation/helpers";

export default React.memo(function ReserverPriceInfo({
    reservationPriceInfo
    }: {
    reservationPriceInfo: ReservationPriceInfo
}) {
    const {discountPercentage, discountedPriceInCents, priceInCents, creationType} = reservationPriceInfo
    const i18n = useTranslation()

    const getSwitchInfoText = () => {
      if (priceInCents > 0) {
        return i18n.reservation.paymentInfo.moreExpensive(formatCentsToEuros(priceInCents))
      } else if (priceInCents < 0) {
        return i18n.reservation.paymentInfo.lessExpensive
      }
      return i18n.reservation.paymentInfo.equal
    }

    const getDiscountText = () => {
      const {reserverType, reserverName, discountPercentage} = reservationPriceInfo
      return i18n.reservation.reserverDiscountInfo(
          reserverType,
          reserverName || '',
          discountPercentage,
          formatCentsToEuros(discountedPriceInCents)
      )
    }

    const switchText = creationType === 'Switch' ? getSwitchInfoText() : null
    const discountText = discountPercentage > 0 ? getDiscountText() : null
    const showDiscountText = discountText && (!switchText || switchText && priceInCents > 0)

    return switchText || discountText ? (
        <div
            id="empty-dimensions-warning"
            className="reservation-info column is-four-fifths"
        >
            <div className="column is-narrow">
                <span className="icon">
                    <BlueInfoCircle/>
                </span>
            </div>
            <p className="column">
                {switchText && (<span>{switchText}</span>)}
                {showDiscountText && (<span>{discountText}</span>)}
            </p>
        </div>
    ) : (
        <></>
    )

})
