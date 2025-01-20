import React, { useEffect, useRef } from 'react'

import { CanReserveReservation } from 'citizen-frontend/api-types/reservation'

import ReserveModal from './ReserveModal'
import { useReserveActionContext } from './state'

export type CanReserveResultProps = {
  canReserveResult: CanReserveReservation
  reserveSpace: () => void
}

export default React.memo(function CanReserveResult({
  canReserveResult,
  reserveSpace
}: CanReserveResultProps) {
  const { setError } = useReserveActionContext()
  const hasStartedReservation = useRef(false)
  const shouldReserveDirectly =
    (canReserveResult.status === 'CanReserve' ||
      canReserveResult.status === 'CanReserveOnlyForOrganization') &&
    !canReserveResult.switchableReservations.length
  useEffect(() => {
    if (shouldReserveDirectly && !hasStartedReservation.current) {
      hasStartedReservation.current = true
      reserveSpace()
    }
    if (canReserveResult.status === 'CanNotReserve') {
      setError('NOT_POSSIBLE')
    }
  }, [
    reserveSpace,
    shouldReserveDirectly,
    hasStartedReservation,
    canReserveResult.status,
    setError
  ])

  return (
    !shouldReserveDirectly && (
      <ReserveModal
        canReserveResult={canReserveResult}
        reserveSpace={reserveSpace}
      />
    )
  )
})
