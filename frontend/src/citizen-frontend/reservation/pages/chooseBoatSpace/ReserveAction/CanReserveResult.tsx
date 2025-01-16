import React, { useEffect, useRef } from 'react'

import { CanReserveReservation } from 'citizen-frontend/api-types/reservation'

import ReserveModal from './ReserveModal'

export type CanReserveResultProps = {
  canReserveResult: CanReserveReservation
  reserveSpace: () => void
}

export default React.memo(function CanReserveResult({
  canReserveResult,
  reserveSpace
}: CanReserveResultProps) {
  const hasStartedReservation = useRef(false)
  const shouldReserveDirectly =
    canReserveResult.status === 'CanReserve' &&
    !canReserveResult.switchableReservations.length
  useEffect(() => {
    if (shouldReserveDirectly && !hasStartedReservation.current) {
      hasStartedReservation.current = true
      reserveSpace()
    }
  }, [reserveSpace, shouldReserveDirectly, hasStartedReservation])

  return (
    !shouldReserveDirectly && (
      <ReserveModal
        canReserveResult={canReserveResult}
        reserveSpace={reserveSpace}
      />
    )
  )
})
