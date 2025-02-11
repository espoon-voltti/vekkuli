import { Container } from 'lib-components/dom'
import React, { useState } from 'react'

import { UpdateTrailerRequest } from 'citizen-frontend/api-clients/trailer'
import { ExistingBoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { ReservationId } from 'citizen-frontend/shared/types'
import { MutationDescription } from 'lib-common/query'

import Reservation from './Reservation'
import TerminateModal from './TerminateModal'
import TerminateModalFailure from './TerminateModalFailure'
import TerminateModalSuccess from './TerminateModalSuccess'
import { terminateReservationDisabled } from './queries'

export default React.memo(function Reservations({
  reservations,
  updateTrailerMutation,
  terminateMutation = terminateReservationDisabled
}: {
  reservations: ExistingBoatSpaceReservation[]
  terminateMutation?: MutationDescription<ReservationId, void>
  updateTrailerMutation: MutationDescription<UpdateTrailerRequest, void>
}) {
  const terminationDisabled = terminateMutation === terminateReservationDisabled
  const [reservationPendingTermination, setReservationPendingTermination] =
    useState<ExistingBoatSpaceReservation | null>(null)
  const [reservationTerminateSuccess, setReservationTerminateSuccess] =
    useState(false)
  const [reservationTerminateFailed, setReservationTerminateFailed] =
    useState(false)

  return (
    <>
      {reservations && reservations.length > 0 && (
        <Container isBlock data-testid="reservation-list">
          <h3>Paikkavaraukset</h3>
          <div className="reservation-list form-section">
            {reservations.map((reservation) => (
              <Reservation
                key={reservation.id}
                reservation={reservation}
                onTerminate={
                  terminationDisabled
                    ? undefined
                    : () => setReservationPendingTermination(reservation)
                }
                updateTrailerMutation={updateTrailerMutation}
              />
            ))}
          </div>
        </Container>
      )}
      {reservationPendingTermination && (
        <TerminateModal
          reservation={reservationPendingTermination}
          terminateMutation={terminateMutation}
          onCancel={() => {
            setReservationPendingTermination(null)
          }}
          onFailure={() => {
            setReservationTerminateFailed(true)
            setReservationPendingTermination(null)
          }}
          onSuccess={() => {
            setReservationTerminateSuccess(true)
            setReservationPendingTermination(null)
          }}
        />
      )}
      {reservationTerminateSuccess && (
        <TerminateModalSuccess
          close={() => setReservationTerminateSuccess(false)}
        />
      )}
      {reservationTerminateFailed && (
        <TerminateModalFailure
          close={() => setReservationTerminateFailed(false)}
        />
      )}
    </>
  )
})
