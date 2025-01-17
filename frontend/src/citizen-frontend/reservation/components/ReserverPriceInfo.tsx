import React from 'react'

import {BoatSpaceReservation} from 'citizen-frontend/api-types/reservation'
import {BlueInfoCircle} from "../../../lib-icons";
import {Citizen, NewOrganization, Organization} from "../../shared/types";
import {formatCentsToEuros} from "../../shared/formatters";
import {useTranslation} from "../../localization";

const discountedPrice = (priceInCents: number, discountPercentage: number) => {
    const discountedPriceInCents = priceInCents - (priceInCents * discountPercentage / 100)
    return formatCentsToEuros(discountedPriceInCents)
}

const getReservationDiscountInfo = (
    citizen: Citizen | undefined,
    organization: Organization | NewOrganization | null,
    totalPriceInCents: number): string | null => {
    const i18n = useTranslation()

    if (organization && organization.discountPercentage > 0) {
        const {name, discountPercentage} = organization
        return i18n.reservation.reserverDiscountInfo('Organization', name, discountPercentage, discountedPrice(totalPriceInCents, discountPercentage))
    } else if (citizen && citizen.discountPercentage > 0) {
        const {discountPercentage} = citizen
        return i18n.reservation.reserverDiscountInfo('Citizen', '', discountPercentage, discountedPrice(totalPriceInCents, discountPercentage))
    }
    return null
}

export default React.memo(function ReserverPriceInfo({reservation, organization}: {
    reservation: BoatSpaceReservation,
    organization: Organization | NewOrganization | null
}) {
    const {citizen, totalPriceInCents} = reservation
    const discountInfo = getReservationDiscountInfo(citizen, organization, totalPriceInCents)

    return discountInfo ? (
        <div className="reservation-price-info">
            <span className="icon"><BlueInfoCircle/></span>
            {/*<span>TÄHÄN info paikan vaihdosta, onko kalliimpi kuin vanha, eli maksettava lisää, halvempi = ei hyvitystä
            Erotushinta passattava alennuslaskentaan
            </span>*/}
            <span className="info-content">{discountInfo}</span>
        </div>
    ) : <></>
})
