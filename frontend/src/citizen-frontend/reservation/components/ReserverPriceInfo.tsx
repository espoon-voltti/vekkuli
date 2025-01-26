import React from 'react'

import {BlueInfoCircle} from '../../../lib-icons'
import {useTranslation} from '../../localization'
import {parsePrice} from '../../shared/formatters'
import {RevisedPriceForReservation} from "../RevisedPriceForReservation";

export default React.memo(function ReserverPriceInfo({
    revisedPriceForReservation
    }: {
    revisedPriceForReservation: RevisedPriceForReservation
}) {
    const {discountPercentage, revisedPriceWithDiscountInEuro, revisedPriceInEuro, creationType} = revisedPriceForReservation
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
      const {reserverType, name, discountPercentage} = revisedPriceForReservation
      return i18n.reservation.reserverDiscountInfo(
          reserverType,
          name ?? "",
          discountPercentage,
          revisedPriceWithDiscountInEuro
      )
    }

    const switchText = creationType === 'Switch' ? getSwitchInfoText() : null
    const discountText = discountPercentage > 0 ? getDiscountText() : null
    const showDiscountText = discountText && (!switchText || switchText && numericPrice > 0)

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
