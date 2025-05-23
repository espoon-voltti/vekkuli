import React, { createContext, useContext, useState } from 'react'

import { SwitchReservationInformation } from '../../../../api-types/reservation'

import ErrorModal, { ErrorCode } from './ErrorModal'

interface ReserveActionContextType {
  isLoading: boolean
  setIsLoading: React.Dispatch<React.SetStateAction<boolean>>
  targetSpaceId: number
  setTargetSpaceId: React.Dispatch<React.SetStateAction<number>>
  onClose: () => void
  error: ErrorCode | undefined
  setError: React.Dispatch<React.SetStateAction<ErrorCode | undefined>>
  switchInfo?: SwitchReservationInformation
}

const ReserveActionContext = createContext<
  ReserveActionContextType | undefined
>(undefined)

export const useReserveActionContext = () => {
  const defaultContext: ReserveActionContextType = {
    isLoading: false,
    setIsLoading: () => null,
    targetSpaceId: NaN,
    setTargetSpaceId: () => null,
    onClose: () => null,
    error: undefined,
    setError: () => null,
    switchInfo: undefined
  }

  return useContext(ReserveActionContext) ?? defaultContext
}

export type ReserveActionProviderProps = {
  spaceId: number
  children?: React.ReactNode
  onClose: () => void
  switchInfo?: SwitchReservationInformation
}

export const ReserveActionProvider = ({
  children,
  spaceId,
  onClose,
  switchInfo
}: ReserveActionProviderProps): React.JSX.Element => {
  const [isLoading, setIsLoading] = useState(false)
  const [targetSpaceId, setTargetSpaceId] = useState(spaceId)
  const [error, setError] = React.useState<ErrorCode | undefined>()

  return (
    <ReserveActionContext.Provider
      value={{
        isLoading,
        setIsLoading,
        targetSpaceId,
        setTargetSpaceId,
        onClose,
        switchInfo,
        error,
        setError
      }}
    >
      {!error && children}
      {!!error && <ErrorModal error={error} close={onClose} />}
    </ReserveActionContext.Provider>
  )
}

export const mapErrorCode = (errorCode: string): ErrorCode => {
  switch (errorCode) {
    case 'MaxReservations':
      return 'MAX_RESERVATIONS'
    case 'NotPossible':
      return 'NOT_POSSIBLE'
    case 'NotAvailable':
      return 'NOT_AVAILABLE'
    default:
      return 'SERVER_ERROR'
  }
}
