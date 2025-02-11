import { Loader } from 'lib-components/Loader'
import React from 'react'
import { useNavigate } from 'react-router'

import { useMutation, useQueryResult } from 'lib-common/query'

import {
  canReserveSpaceQuery,
  reserveSpaceMutation,
  startSwitchSpaceMutation
} from '../queries'

import CanReserveResult from './CanReserveResult'
import { mapErrorCode, useReserveActionContext } from './state'

export default React.memo(function ReserveAction() {
  const { targetSpaceId, setError, switchInfo } = useReserveActionContext()
  const navigate = useNavigate()
  const canReserveResult = useQueryResult(canReserveSpaceQuery(targetSpaceId))
  const { mutateAsync: reserveSpace } = useMutation(reserveSpaceMutation)
  const { mutateAsync: switchPlace } = useMutation(startSwitchSpaceMutation)
  const onReserveSpace = () => {
    if (switchInfo?.id) {
      switchPlace({
        reservationId: switchInfo.id,
        spaceId: targetSpaceId
      })
        .then(() => {
          return navigate('/kuntalainen/venepaikka/vaihda')
        })
        .catch((error) => {
          const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
          console.error(errorCode)
          const errorType = mapErrorCode(errorCode)
          setError(errorType)
        })
    } else {
      reserveSpace(targetSpaceId)
        .then(() => {
          return navigate('/kuntalainen/venepaikka/varaa')
        })
        .catch((error) => {
          const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
          console.error(errorCode)
          const errorType = mapErrorCode(errorCode)
          setError(errorType)
        })
    }
  }

  return (
    <Loader results={[canReserveResult]}>
      {(loadedCanReserveResult) => (
        <CanReserveResult
          canReserveResult={loadedCanReserveResult}
          reserveSpace={onReserveSpace}
        />
      )}
    </Loader>
  )
})
