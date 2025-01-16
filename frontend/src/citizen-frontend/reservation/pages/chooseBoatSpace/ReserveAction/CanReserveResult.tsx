import React, { useEffect, useRef, useState } from 'react'

import { CanReserveReservation } from 'citizen-frontend/api-types/reservation'

import ReserveModal from './ReserveModal'

export type CanReserveResultProps = {
  canReserveResult: CanReserveReservation
  reserveSpaceId: number
  reserveSpace: () => void
}

export default React.memo(function CanReserveResult({
  canReserveResult,
  reserveSpaceId,
  reserveSpace
}: CanReserveResultProps) {
  const hasReserved = useRef(false)
  const shouldReserveDirectly =
    canReserveResult.status === 'CanReserve' &&
    !canReserveResult.switchableReservations.length
  const [showModal, setShowModal] = useState(!shouldReserveDirectly)
  useEffect(() => {
    if (shouldReserveDirectly && !hasReserved.current) {
      hasReserved.current = true
      reserveSpace()
    }
  }, [reserveSpace, shouldReserveDirectly, hasReserved])

  return (
    showModal && (
      <ReserveModal
        canReserveResult={canReserveResult}
        reserveSpaceId={reserveSpaceId}
        reserveSpace={reserveSpace}
        closeModal={() => {
          setShowModal(false)
        }}
      />
    )
  )
})
