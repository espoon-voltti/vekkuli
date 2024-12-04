package fi.espoo.vekkuli.boatSpace.citizenHome

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.ReservationOperation
import fi.espoo.vekkuli.domain.ReservationPeriod
import fi.espoo.vekkuli.service.SeasonalService
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service

@Service
class CitizenHomeService(
    private val messageUtil: MessageUtil,
    private val timeProvider: TimeProvider,
    private val seasonalService: SeasonalService,
) {
    fun t(
        key: String,
        params: List<String> = emptyList()
    ): String = messageUtil.getMessage(key, params)

    fun getHomeViewParameters(): HomeViewParameters {
        val year = timeProvider.getCurrentDate().year.toString()
        val periods = seasonalService.getReservationPeriods()
        return HomeViewParameters(
            typeSections =
                listOf(
                    getSlipSection(periods, year),
                    getTrailerSection(periods, year),
                    getWinterSection(periods, year),
                    getStorageSection(periods, year)
                )
        )
    }

    private fun getSlipSection(
        periods: List<ReservationPeriod>,
        year: String
    ): HomeViewSection {
        val preConfiguredSeasonTime = "10.6.–14.9.$year"

        val renewPeriodForEspooCitizens =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Slip &&
                    it.isEspooCitizen &&
                    it.operation == ReservationOperation.Renew
            }
        val reservationPeriodForEspooCitizens =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Slip &&
                    it.isEspooCitizen &&
                    it.operation == ReservationOperation.New
            }
        val reservationPeriodForOthers =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Slip &&
                    !it.isEspooCitizen &&
                    it.operation == ReservationOperation.New
            }

        return HomeViewSection(
            title = t("citizenFrontpage.periods.slip.title", listOf(year)),
            season = t("citizenFrontpage.periods.slip.season", listOf(preConfiguredSeasonTime)),
            periods =
                listOf(
                    t(
                        "citizenFrontpage.periods.slip.renewPeriodForEspooCitizens",
                        listOf(formatPeriodDates(renewPeriodForEspooCitizens, year))
                    ),
                    t(
                        "citizenFrontpage.periods.slip.reservationPeriodForEspooCitizens",
                        listOf(formatPeriodDates(reservationPeriodForEspooCitizens, year))
                    ),
                    t(
                        "citizenFrontpage.periods.slip.reservationPeriodForOthers",
                        listOf(formatPeriodDates(reservationPeriodForOthers, year))
                    ),
                )
        )
    }

    private fun getTrailerSection(
        periods: List<ReservationPeriod>,
        year: String
    ): HomeViewSection {
        val preConfiguredSeasonTime = "1.5.$year–30.4.${(year.toInt() + 1)}"
        val renewPeriodForEspooCitizens =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Trailer &&
                    it.isEspooCitizen &&
                    it.operation == ReservationOperation.Renew
            }
        val reservationPeriodForAll =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Trailer &&
                    it.operation == ReservationOperation.New
            }

        return HomeViewSection(
            title = t("citizenFrontpage.periods.trailer.title", listOf(year)),
            season = t("citizenFrontpage.periods.trailer.season", listOf(preConfiguredSeasonTime)),
            periods =
                listOf(
                    t(
                        "citizenFrontpage.periods.trailer.renewPeriodForEspooCitizens",
                        listOf(formatPeriodDates(renewPeriodForEspooCitizens, year))
                    ),
                    t(
                        "citizenFrontpage.periods.trailer.reservationPeriodForAll",
                        listOf(formatPeriodDates(reservationPeriodForAll, year))
                    ),
                )
        )
    }

    private fun getWinterSection(
        periods: List<ReservationPeriod>,
        year: String
    ): HomeViewSection {
        val preConfiguredSeasonTime = "15.9.$year–10.6.${(year.toInt() + 1)}"
        val renewPeriodForEspooCitizens =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Winter &&
                    it.isEspooCitizen &&
                    it.operation == ReservationOperation.Renew
            }
        val reservationPeriodForEspooCitizens =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Winter &&
                    it.isEspooCitizen &&
                    it.operation == ReservationOperation.New
            }

        return HomeViewSection(
            title = t("citizenFrontpage.periods.winter.title", listOf(year)),
            season = t("citizenFrontpage.periods.winter.season", listOf(preConfiguredSeasonTime)),
            periods =
                listOf(
                    t(
                        "citizenFrontpage.periods.winter.renewPeriodForEspooCitizens",
                        listOf(formatPeriodDates(renewPeriodForEspooCitizens, year))
                    ),
                    t(
                        "citizenFrontpage.periods.winter.reservationPeriodForEspooCitizens",
                        listOf(formatPeriodDates(reservationPeriodForEspooCitizens, year))
                    ),
                )
        )
    }

    private fun getStorageSection(
        periods: List<ReservationPeriod>,
        year: String
    ): HomeViewSection {
        val preConfiguredSeasonTime = "15.9.$year–14.9.${(year.toInt() + 1)}"
        val renewPeriod =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Storage &&
                    it.operation == ReservationOperation.Renew
            }
        val reservationPeriod =
            periods.firstOrNull {
                it.boatSpaceType == BoatSpaceType.Storage &&
                    it.operation == ReservationOperation.New
            }

        return HomeViewSection(
            title = t("citizenFrontpage.periods.storage.title", listOf(year)),
            season = t("citizenFrontpage.periods.storage.season", listOf(preConfiguredSeasonTime)),
            periods =
                listOf(
                    t(
                        "citizenFrontpage.periods.storage.renewPeriod",
                        listOf(formatPeriodDates(renewPeriod, year))
                    ),
                    t(
                        "citizenFrontpage.periods.storage.reservationPeriod",
                        listOf(formatPeriodDates(reservationPeriod, year))
                    ),
                )
        )
    }

    private fun formatPeriodDates(
        period: ReservationPeriod?,
        year: String
    ): String {
        if (period == null) {
            return t("citizenFrontpage.periods.notFound") + ","
        }
        var endYear = year
        var startYear = ""
        if (period.startMonth > period.endMonth || (period.startMonth == period.endMonth && period.startDay > period.endDay)) {
            startYear = year
            endYear = (year.toInt() + 1).toString()
        }
        val startMonth = period.startMonth.toString()
        val startDay = period.startDay.toString()
        val endMonth = period.endMonth.toString()
        val endDay = period.endDay.toString()
        return "$startDay.$startMonth.$startYear–$endDay.$endMonth.$endYear"
    }
}
