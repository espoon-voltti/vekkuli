import { Loader } from 'lib-components/Loader'
import { Column, Columns } from 'lib-components/dom'
import Modal from 'lib-components/modal/Modal'
import { ModalButton } from 'lib-components/modal/ModalButtons'
import React from 'react'

import { CanReserveReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import BoatSpaceInformation from 'citizen-frontend/reservation/components/BoatSpaceInformation'
import { useQueryResult } from 'lib-common/query'

import SwitchReservation from './SwitchReservation'
import { boatSpaceQuery } from './queries'
import { useReserveActionContext } from './state'

export type SwitchModalProps = {
  canReserveResult: CanReserveReservation
  reserveSpace: () => void
}

export default React.memo(function ReserveModal({
  canReserveResult,
  reserveSpace
}: SwitchModalProps) {
  const { targetSpaceId, onClose } = useReserveActionContext()
  const i18n = useTranslation()
  const boatSpaceResult = useQueryResult(boatSpaceQuery(targetSpaceId))
  const modalButtons: ModalButton[] = [
    {
      label: i18n.common.cancel
    }
  ]

  if (
    canReserveResult.status === 'CanReserve' ||
    canReserveResult.status === 'CanReserveOnlyForOrganization'
  )
    modalButtons.push({
      label: i18n.reservation.searchPage.modal.reserveNewSpace,
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
          close={onClose}
          buttons={modalButtons}
          data-testid="reserve-modal"
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
              />
            ))}
            {canReserveResult.switchableOrganizationReservations.map(
              (organizationReservation) => (
                <>
                  <Column isFull>
                    <h3>
                      {i18n.reservation.searchPage.modal.organizationCurrentPlaces(
                        organizationReservation.organizationName
                      )}
                    </h3>
                  </Column>
                  {organizationReservation.reservations.map((reservation) => (
                    <SwitchReservation
                      key={reservation.id}
                      reservation={reservation}
                    />
                  ))}
                </>
              )
            )}
          </Columns>
        </Modal>
      )}
    </Loader>
  )
})
