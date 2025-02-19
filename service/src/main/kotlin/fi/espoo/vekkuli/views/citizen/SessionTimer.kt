package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SessionTimer {
    @Autowired
    lateinit var messageUtil: MessageUtil

    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(reservationTimeInSeconds: Long): String {
        val d = "$"
        // language=JavaScript
        val timerScript =
            """
            (function () {
              let interval = undefined;
              htmx.onLoad(() => {
                let startingTime = $reservationTimeInSeconds;
                const timerElement = document.getElementById("timer");
                const timerMessageTemplate =
                  /*[[#{boatApplication.timer}]]*/ "Sinulla on {0} ja {1} aikaa vahvistaa venepaikkavaraus maksamalla.";
                const minutesText = /*[[#{boatApplication.minutes}]]*/ "minuuttia";
                const secondsText = /*[[#{boatApplication.seconds}]]*/ "sekuntia";

                const formatTimeMessage = (time) => {
                  const minutes = Math.floor(time / 60);
                  const seconds = time % 60;
                  const minutesPart = `<span class="has-text-weight-bold">$d{String(minutes)} $d{minutesText}</span>`;
                  const secondsPart = `<span class="has-text-weight-bold">$d{String(seconds).padStart(2, "0")} $d{secondsText}</span>`;
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
            """.trimIndent()
        // language=HTML
        return """
            <div class="container p-3" id="sessionTimer" >
                <div role="timer" id="timer" class="timer has-text-centered" ">
                ${t("boatApplication.timer")}
                Sinulla on x minuuttia ja x sekuntia aikaa vahvistaa venepaikkavaraus maksamalla.</div>
                <script>
                    $timerScript
                </script>
            </div>
            """.trimIndent()
    }
}
