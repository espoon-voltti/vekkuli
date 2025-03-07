package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.PriceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PriceService(
    private val priceRepository: PriceRepository
) {
    fun getPriceClasses(): List<Price> = priceRepository.getPriceClasses()
}
