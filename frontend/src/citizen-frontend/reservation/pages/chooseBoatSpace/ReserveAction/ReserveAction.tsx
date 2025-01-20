import { Loader } from 'lib-components/Loader'
import React from 'react'
import { useNavigate } from 'react-router'

import { useMutation, useQueryResult } from 'lib-common/query'

import { canReserveSpaceQuery, reserveSpaceMutation } from '../queries'

import CanReserveResult from './CanReserveResult'
import { mapErrorCode, useReserveActionContext } from './state'

export default React.memo(function ReserveAction() {
  const { targetSpaceId, setError } = useReserveActionContext()
  const navigate = useNavigate()
  const canReserveResult = useQueryResult(canReserveSpaceQuery(targetSpaceId))
  const { mutateAsync: reserveSpace } = useMutation(reserveSpaceMutation)

  const onReserveSpace = () => {
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
