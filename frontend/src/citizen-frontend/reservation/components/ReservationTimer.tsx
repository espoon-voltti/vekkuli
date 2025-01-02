import { Loader } from 'lib-components/Loader'
import { Container } from 'lib-components/dom'
import React, { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router'

import { useQueryResult } from '../../../lib-common/query'
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

/*
<script>
                (function () {
let interval = undefined;
htmx.onLoad(() => {
let startingTime = 1152;
const timerElement = document.getElementById("timer");
const timerMessageTemplate =
  /*[[#{boatApplication.timer}]] "Sinulla on {0} ja {1} aikaa vahvistaa venepaikkavaraus maksamalla.";
const minutesText = /*[[#{boatApplication.minutes}]] "minuuttia";
const secondsText = /*[[#{boatApplication.seconds}]] "sekuntia";

const formatTimeMessage = (time) => {
  const minutes = Math.floor(time / 60);
  const seconds = time % 60;
  const minutesPart = `<span class="has-text-weight-bold">${String(minutes)} ${minutesText}</span>`;
  const secondsPart = `<span class="has-text-weight-bold">${String(seconds).padStart(2, "0")} ${secondsText}</span>`;
  return timerMessageTemplate
    .replace("{0}", minutesPart)
    .replace("{1}", secondsPart);
};

const startTimer = () => {
  if (interval) {
    return;
  }
  timerElement.innerHTML = formatTimeMessage(startingTime);
  interval = setInterval(() => {
    if (startingTime > 0) {
      startingTime--;
      timerElement.innerHTML = formatTimeMessage(startingTime);
    } else {
      clearInterval(interval);
      interval = undefined;
      // Redirect when the timer reaches zero
      window.location.replace(
        window.location.protocol +
          "//" +
          window.location.host +
          "/kuntalainen/venepaikat"
      );
    }
  }, 1000);
};

startTimer();
});
})();
            </script>
*/
