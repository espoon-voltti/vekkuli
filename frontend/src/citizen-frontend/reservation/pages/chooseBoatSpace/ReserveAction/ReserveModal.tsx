import { Loader } from 'lib-components/Loader'
import { Button, Column, Columns } from 'lib-components/dom'
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
      label: i18n.reservation.cancelReservation
    }
  ]
  const canReserveAsNew =
    canReserveResult.status === 'CanReserve' ||
    canReserveResult.status === 'CanReserveOnlyForOrganization'

  return (
    <Loader results={[boatSpaceResult]}>
      {(loadedBoatSpaceResult) => (
        <Modal
          close={onClose}
          buttons={modalButtons}
          buttonAlignment="right"
          data-testid="reserve-modal"
        >
          <Columns isMultiline>
            <Column isFull>
              <SectionTitle>
                {i18n.reservation.searchPage.modal.reservingBoatSpace}
              </SectionTitle>
            </Column>
            <Column isFull>
              <Columns>
                <Column isHalf>
                  <BoatSpaceInformation boatSpace={loadedBoatSpaceResult} />
                </Column>
                {canReserveAsNew && (
                  <Column isHalf>
                    <Button type="primary" action={reserveSpace} isFullWidth>
                      {i18n.reservation.searchPage.modal.reserveNewSpace}
                    </Button>
                  </Column>
                )}
              </Columns>
            </Column>
            <Column isFull>
              <SectionTitle>
                {i18n.reservation.searchPage.modal.currentPlaces}
              </SectionTitle>
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
                  <Column isFull key={organizationReservation.organizationName}>
                    <SectionTitle>
                      {i18n.reservation.searchPage.modal.organizationCurrentPlaces(
                        organizationReservation.organizationName
                      )}
                    </SectionTitle>
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

function SectionTitle({ children }: { children: React.ReactNode }) {
  return <p className="is-size-4 has-text-weight-medium my-2">{children}</p>
}
