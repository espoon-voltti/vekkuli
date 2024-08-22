package fi.espoo.vekkuli.config

object BoatSpaceConfig {
    const val SESSION_TIME_IN_SECONDS = 20 * 60
    const val PAYMENT_TIMEOUT = 24 * 60 * 60
    const val BOAT_RESERVATION_ALV_PERCENTAGE = 10.0

    const val BUOY_WIDTH_ADJUSTMENT_CM = 40
    const val BUOY_LENGTH_ADJUSTMENT_CM = 100
    const val BEAM_WIDTH_ADJUSTMENT_CM = 40
    const val BEAM_LENGTH_ADJUSTMENT_CM = 100
    const val WALK_BEAM_WIDTH_ADJUSTMENT_CM = 75
    const val WALK_BEAM_LENGTH_ADJUSTMENT_CM = 100
    const val REAR_BUOY_WIDTH_ADJUSTMENT_CM = 50
    const val REAR_BUOY_LENGTH_ADJUSTMENT_CM = 300
    const val BOAT_LENGTH_THRESHOLD_CM = 1500

    const val EMAIL_SENDER = "varaukset@espoo.fi"
}
