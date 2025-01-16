import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import { ModalButton } from 'lib-components/modal/ModalButtons'
import React from 'react'

import { CanReserveReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import BoatSpaceInformation from 'citizen-frontend/reservation/components/BoatSpaceInformation'

import { useQueryResult } from '../../../../../lib-common/query'
import { Loader } from '../../../../../lib-components/Loader'

import SwitchReservation from './SwitchReservation'
import { boatSpaceQuery } from './queries'

export type SwitchModalProps = {
  canReserveResult: CanReserveReservation
  reserveSpaceId: number
  reserveSpace: () => void
  closeModal: () => void
}

export default React.memo(function ReserveModal({
  canReserveResult,
  reserveSpaceId,
  reserveSpace,
  closeModal
}: SwitchModalProps) {
  const i18n = useTranslation()
  const boatSpaceResult = useQueryResult(boatSpaceQuery(reserveSpaceId))
  const modalButtons: ModalButton[] = [
    {
      label: i18n.common.cancel
    }
  ]

  if (canReserveResult.status === 'CanReserve')
    modalButtons.push({
      label: i18n.reservation.searchPage.modal.reserveAnotherPlace,
      type: 'primary',
      action: () => {
        return reserveSpace()
      }
    })

  return (
    <Loader results={[boatSpaceResult]}>
      {(loadedBoatSpaceResult) => (
        <Modal
          title={i18n.reservation.searchPage.modal.reservingBoatSpace}
          close={closeModal}
          buttons={modalButtons}
        >
          <Columns isMultiline>
            <Column isFull>
              <BoatSpaceInformation boatSpace={loadedBoatSpaceResult} />
            </Column>
            <Column isFull>
              <h3>{i18n.reservation.searchPage.modal.currentPlaces}</h3>
            </Column>
            {canReserveResult.switchableReservations.map((reservation) => (
              <SwitchReservation
                key={reservation.id}
                reservation={reservation}
                reserveSpaceId={reserveSpaceId}
                setLoading={() => null}
              />
            ))}
          </Columns>
        </Modal>
      )}
    </Loader>
  )
})
