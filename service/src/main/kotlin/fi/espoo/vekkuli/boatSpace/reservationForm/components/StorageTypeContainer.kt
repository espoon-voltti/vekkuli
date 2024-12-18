package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.domain.StorageType
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.math.BigDecimal

// language=HTML
@Component
class StorageTypeContainer(
    private val formComponents: FormComponents,
) : BaseView() {
    fun render(
        trailerRegistrationNumber: String?,
        trailerWidth: BigDecimal?,
        trailerLength: BigDecimal?,
        storageType: StorageType? = StorageType.Trailer
    ): String {
        val trailerRegistrationNumberInput =
            formComponents.textInput(
                "boatApplication.title.trailerRegistrationNumber",
                "trailerRegistrationNumber",
                trailerRegistrationNumber ?: "",
                true
            )
        val trailerWidthInput =
            formComponents.decimalInput(
                labelKey = "boatApplication.title.trailerWidth",
                "trailerWidth",
                trailerWidth,
                true
            )
        val trailerLengthInput =
            formComponents.decimalInput(
                "boatApplication.title.trailerLength",
                "trailerLength",
                trailerLength,
                true
            )

        val radioButtons =
            formComponents.radioButtons(
                "boatApplication.title.storageType",
                "storageType",
                storageType?.name,
                listOf(
                    RadioOption(StorageType.Trailer.name, t("boatApplication.option.trailer")),
                    RadioOption(StorageType.Buck.name, t("boatApplication.option.buck")),
                    RadioOption(StorageType.BuckWithTent.name, t("boatApplication.option.buckTent"))
                ),
                mapOf("x-model" to "storageType"),
                isColumnLayout = true
            )

        return """<div data-testid="storage-type-selector" >
            $radioButtons
            <template x-if="storageType === '${StorageType.Trailer.name}'">
                <div data-testid="trailer-information-inputs" class='columns'>
                    <div class='column is-one-quarter'>
                        $trailerRegistrationNumberInput
                    </div>
                     <div class='column is-one-quarter'>
                        $trailerWidthInput
                     </div>
                     <div class='column is-one-quarter'>
                        $trailerLengthInput
                    </div>
                </div>
            </template>
            </div>
            """
    }
}
