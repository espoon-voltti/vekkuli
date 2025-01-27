import { useQueryClient } from '@tanstack/react-query'
import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'

import { unfinishedReservationQuery } from 'citizen-frontend/reservation/queries'
import { useQueryResult } from 'lib-common/query'

import { unfinishedReservationExpirationQuery } from '../queries'

export default React.memo(function ReservationTimer() {
  const remainingTime = useQueryResult(unfinishedReservationExpirationQuery())
  return (
    <Container>
      <Loader results={[remainingTime]} allowFailure>
        {(loadedRemainingSeconds) => (
          <TimeRemaining seconds={loadedRemainingSeconds} />
        )}
      </Loader>
    </Container>
  )
})

const TimeRemaining = React.memo(function TimeRemaining({
  seconds
}: {
  seconds: number
}) {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [remainingTime, setRemainingTime] = useState(seconds || 0)

  useEffect(() => {
    setRemainingTime(seconds)
  }, [seconds])

  useEffect(() => {
    const startTime = new Date().getTime()
    const handleNavigation = async () => {
      await queryClient.resetQueries({
        queryKey: unfinishedReservationQuery().queryKey
      })
      return navigate('/kuntalainen/venepaikka')
    }
    const intervalId = setInterval(() => {
      const elapsedTime = Math.floor((new Date().getTime() - startTime) / 1000)
      const updatedTime = Math.max(remainingTime - elapsedTime, 0)

      if (updatedTime <= 0) {
        clearInterval(intervalId)
        handleNavigation().catch((e) => console.error(e))
      } else {
        setRemainingTime(updatedTime)
      }
    }, 1000)

    return () => clearInterval(intervalId)
  }, [queryClient, navigate, setRemainingTime, remainingTime])

  if (remainingTime <= 0) {
    return null
  }

  const remainingMinutes: number = Math.floor(remainingTime / 60)
  const remainingSeconds: number = remainingTime % 60

  return (
    <div role="timer" id="timer" className="timer has-text-centered p-3">
      Sinulla on
      <span className="has-text-weight-bold">
        {' '}
        {remainingMinutes} minuuttia
      </span>{' '}
      ja
      <span className="has-text-weight-bold">
        {' '}
        {remainingSeconds} sekuntia
      </span>{' '}
      aikaa vahvistaa venepaikkavaraus maksamalla.
    </div>
  )
})
