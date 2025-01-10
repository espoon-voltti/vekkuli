package fi.espoo.vekkuli.repository.filter.boatspacereservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.InExpr
import fi.espoo.vekkuli.utils.OrExpr

private class SlipAmenityExpr(
    amenities: List<BoatSpaceAmenity>
) : InExpr<BoatSpaceAmenity>("bs.amenity", amenities.filter { slipAmenities.contains(it) })

private class StorageTypeAmenityExpr(
    amenities: List<BoatSpaceAmenity>
) : InExpr<StorageType>(
        "bsr.storage_type",
        amenities
            .filter { storageTypeAmenities.contains(it) }
            .flatMap {
                when (it) {
                    BoatSpaceAmenity.Trailer -> listOf(StorageType.Trailer)
                    BoatSpaceAmenity.Buck -> listOf(StorageType.Buck, StorageType.BuckWithTent)
                    else -> listOf(StorageType.None)
                }
            }.filter { it.name != "None" }
    )

class AmenityExpr(
    amenities: List<BoatSpaceAmenity>
) : OrExpr(
        listOf(
            SlipAmenityExpr(amenities),
            StorageTypeAmenityExpr(amenities)
        )
    )
