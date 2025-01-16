import { Loader } from 'lib-components/Loader'
import React from 'react'

import { useMutation, useQueryResult } from 'lib-common/query'

import { canReserveSpaceQuery, reserveSpaceMutation } from '../queries'

import ReserveModal from './ReserveModal'

export type SwitchModalProps = {
  reserveSpaceId: number
}

export default React.memo(function ReserveAction({
  reserveSpaceId
}: SwitchModalProps) {
  const canReserveResult = useQueryResult(canReserveSpaceQuery(reserveSpaceId))
  const { mutateAsync: reserveSpace } = useMutation(reserveSpaceMutation)

  const onReserveSpace = (spaceId: number) => {
    //setSelectedBoatSpace(undefined)
    reserveSpace(spaceId)
      .then((response) => {
        console.error('got response', response)
        return navigate('/kuntalainen/venepaikka/varaa')
      })
      .catch((error) => {
        const errorCode = error?.response?.data?.errorCode ?? 'SERVER_ERROR'
        const errorType = mapErrorCode(errorCode)
        setReserveError(errorType)
      })
  }

  return (
    <Loader results={[canReserveResult]}>
      {(loadedCanReserveResult) => (
        <ReserveModal
          canReserveResult={loadedCanReserveResult}
          reserveSpace={onReserveSpace}
        />
      )}
    </Loader>
  )
})
