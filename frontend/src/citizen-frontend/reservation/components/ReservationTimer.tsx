import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React, { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router'

import { useQueryResult } from 'lib-common/query'

import { unfinishedReservationExpirationQuery } from '../queries'

export default React.memo(function ReservationTimer() {
  const remainingTime = useQueryResult(unfinishedReservationExpirationQuery())
  return (
    <Container>
      <Loader results={[remainingTime]} allowFailure>
        {(loadedTime) => <TimeRemaining time={loadedTime} />}
      </Loader>
    </Container>
  )
})

const TimeRemaining = React.memo(function TimeRemaining({
  time
}: {
  time: number
}) {
  const navigate = useNavigate()
  const startTime = useRef(new Date().getTime())
  const [remainingTime, setRemainingTime] = useState(time || 0)

  useEffect(() => {
    const handleNavigation = async () => {
      return navigate('/kuntalainen/venepaikka')
    }
    const intervalId = setInterval(() => {
      const elapsedTime = Math.floor(
        (new Date().getTime() - startTime.current) / 1000
      )
      const updatedTime = Math.max(time - elapsedTime, 0)

      setRemainingTime(updatedTime)
      if (updatedTime <= 0) {
        handleNavigation().catch((e) => console.error(e))
      }
    }, 1000)

    return () => clearInterval(intervalId)
  }, [navigate, startTime, setRemainingTime, time])

  if (remainingTime <= 0) {
    return null
  }

  const minutes = Math.floor(remainingTime / 60)
  const seconds = remainingTime % 60

  return (
    <div role="timer" id="timer" className="timer has-text-centered p-3">
      Sinulla on
      <span className="has-text-weight-bold"> {minutes} minuuttia</span> ja
      <span className="has-text-weight-bold"> {seconds} sekuntia</span> aikaa
      vahvistaa venepaikkavaraus maksamalla.
    </div>
  )
})
