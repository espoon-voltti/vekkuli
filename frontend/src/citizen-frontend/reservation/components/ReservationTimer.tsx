import { useQueryClient } from '@tanstack/react-query'
import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'

import { useTranslation } from 'citizen-frontend/localization'
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

const boldText = (text: string) => {
  return `<span class="has-text-weight-bold">${text}</span>`
}

const TimeRemaining = React.memo(function TimeRemaining({
  seconds
}: {
  seconds: number
}) {
  const i18n = useTranslation()
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

  const formattedMinutes = boldText(`
    ${remainingMinutes} ${
      remainingMinutes === 1
        ? i18n.reservation.timer.minute
        : i18n.reservation.timer.minutes
    }`)
  const formattedSeconds = boldText(`
    ${remainingSeconds} ${
      remainingSeconds === 1
        ? i18n.reservation.timer.second
        : i18n.reservation.timer.seconds
    }`)

  const formattedText = i18n.reservation.timer.title
    .replace('${minutes}', formattedMinutes)
    .replace('${seconds}', formattedSeconds)

  return (
    <div
      role="timer"
      id="timer"
      className="timer has-text-centered p-3"
      data-testid="reservation-timer"
    >
      <div dangerouslySetInnerHTML={{ __html: formattedText }} />
    </div>
  )
})
