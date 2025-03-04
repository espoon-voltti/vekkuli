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

const alertTimeMinutes = [30, 15, 10, 5, 2, 1, 0.5]
const alertTimeSeconds = alertTimeMinutes.map((time) => time * 60)

const TimeRemaining = React.memo(function TimeRemaining({
  seconds
}: {
  seconds: number
}) {
  const getCurrentTime = () => Date.now() / 1000

  const i18n = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [totalSeconds, setTotalSeconds] = useState(seconds || 0)
  const [elapsedSeconds, setElapsedSeconds] = useState(0)
  const [startTime, setStartTime] = useState(getCurrentTime())
  const [announceText, setAnnounceText] = useState('')
  const [initialAnnounced, setInitialAnnounced] = useState(false)

  useEffect(() => {
    setTotalSeconds(seconds)
    setElapsedSeconds(0)
    setStartTime(getCurrentTime())
  }, [seconds])

  useEffect(() => {
    const handleNavigation = async () => {
      await queryClient.resetQueries({
        queryKey: unfinishedReservationQuery().queryKey
      })
      return navigate('/kuntalainen/venepaikka')
    }

    const intervalId = setInterval(() => {
      const newElapsedSeconds = getCurrentTime() - startTime

      if (newElapsedSeconds > totalSeconds) {
        clearInterval(intervalId)
        handleNavigation().catch((e) => console.error(e))
      } else {
        setElapsedSeconds(newElapsedSeconds)
      }
    }, 1000)

    return () => clearInterval(intervalId)
  }, [queryClient, navigate, startTime, totalSeconds])

  const remainingTime = totalSeconds - elapsedSeconds
  const remainingMinutes: number = Math.floor(remainingTime / 60)
  const remainingSeconds: number = Math.floor(remainingTime % 60)

  const remainingMinutesText = `
    ${remainingMinutes} ${
      remainingMinutes === 1
        ? i18n.reservation.timer.minute
        : i18n.reservation.timer.minutes
    }`

  const remainingSecondsText = `
    ${remainingSeconds} ${
      remainingSeconds === 1
        ? i18n.reservation.timer.second
        : i18n.reservation.timer.seconds
    }`

  const formattedMinutes = boldText(remainingMinutesText)
  const formattedSeconds = boldText(remainingSecondsText)

  const formattedText = i18n.reservation.timer.title
    .replace('${minutes}', formattedMinutes)
    .replace('${seconds}', formattedSeconds)

  const screenReaderText = i18n.reservation.timer.announceTitle.replace(
    '${time}',
    remainingTime < 60 ? remainingSecondsText : remainingMinutesText
  )

  // Announce the remaining time to screen readers at defined (alertTimeSeconds) times.
  // Set the announcement element to empty string after it has been announced (5 seconds),
  // to prevent the user from receiving a stale value if they navigate on the element with
  // the keyboard or screenreader.
  // Do the initial announcement with a delay when navigating to a page with a timer,
  // to allow the page title to be announced first.
  useEffect(() => {
    if (
      alertTimeSeconds.includes(remainingTime) ||
      (!initialAnnounced && seconds - remainingTime >= 5)
    ) {
      setInitialAnnounced(true)
      setAnnounceText(screenReaderText)
      setTimeout(() => setAnnounceText(''), 5000)
    }
  }, [remainingTime, screenReaderText, initialAnnounced, seconds])

  if (remainingTime <= 0) {
    return null
  }

  return (
    <div
      id="timer"
      className="timer has-text-centered p-3"
      data-testid="reservation-timer"
    >
      <div dangerouslySetInnerHTML={{ __html: formattedText }} tabIndex={-1} />
      <div aria-live="assertive" aria-atomic="true" className="is-sr-only">
        {announceText}
      </div>
    </div>
  )
})
