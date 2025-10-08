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
    private val formComponents: FormComponents
) : BaseView() {
    fun trailerContainerWithWarningText(
        trailerRegistrationNumber: String?,
        trailerWidth: BigDecimal?,
        trailerLength: BigDecimal?,
    ): String =
        """ <div class='form-section'>
                <h1 class='label'>${t("boatApplication.title.trailerType")}</h1>
                <input type="hidden" name="storageType" value='Trailer'>
                    ${trailerEditWithWarningText(
            trailerRegistrationNumber,
            trailerWidth,
            trailerLength
        )}"""

    fun trailerEdit(
        trailerRegistrationNumber: String?,
        trailerWidth: BigDecimal?,
        trailerLength: BigDecimal?,
        fullWidth: Boolean = false,
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

        return (
            """ <template x-if="storageType === '${StorageType.Trailer.name}'">
                <div data-testid="trailer-information-inputs" class='columns'>
                    <div class='column ${if (fullWidth) "" else "is-one-quarter"}'>
                        $trailerRegistrationNumberInput
                    </div>
                     <div class='column ${if (fullWidth) "" else "is-one-quarter"}'>
                        $trailerWidthInput
                     </div>
                     <div class='column ${if (fullWidth) "" else "is-one-quarter"}'>
                        $trailerLengthInput
                    </div>
                </div>
            </template>"""
        )
    }

    fun trailerEditWithWarningText(
        trailerRegistrationNumber: String?,
        trailerWidth: BigDecimal?,
        trailerLength: BigDecimal?,
        fullWidth: Boolean = false,
    ): String {
        val trailerRegistrationNumberInput =
            formComponents.textInput(
                "boatApplication.title.trailerRegistrationNumber",
                "trailerRegistrationNumber",
                trailerRegistrationNumber ?: ""
            )
        val trailerWidthInput =
            formComponents.decimalInput(
                labelKey = "boatApplication.title.trailerWidth",
                "trailerWidth",
                trailerWidth
            )
        val trailerLengthInput =
            formComponents.decimalInput(
                "boatApplication.title.trailerLength",
                "trailerLength",
                trailerLength
            )

        return (
            """ <template x-if="storageType === '${StorageType.Trailer.name}'">
                <div data-testid="trailer-information-inputs" x-data="{
                    trailerRegistrationNumber: '${trailerRegistrationNumber ?: ""}',
                    trailerWidth: '${trailerWidth ?: ""}',
                    trailerLength: '${trailerLength ?: ""}',
                    get showWarning() {
                        const values = [
                            this.trailerRegistrationNumber,
                            this.trailerWidth,
                            this.trailerLength
                        ];
                        const filled = values.filter(v => v && v.trim() !== '').length;
                        return filled > 0 && filled < values.length;
                    }
                }">
                    <div class='columns'>                    
                        <div class='column ${if (fullWidth) "" else "is-one-quarter"}' x-model='trailerRegistrationNumber'>
                            $trailerRegistrationNumberInput
                        </div>
                         <div class='column ${if (fullWidth) "" else "is-one-quarter"}' x-model='trailerWidth'>
                            $trailerWidthInput
                         </div>
                         <div class='column ${if (fullWidth) "" else "is-one-quarter"}' x-model='trailerLength'>
                            $trailerLengthInput
                        </div>
                    </div>
                    <div data-testid="trailer-inputs-incomplete-warning" class="warning" x-show='showWarning'>Huom. Trailerin tiedot eivät päivity, jos kaikkia kenttiä ei ole täytetty</div>
                </div>
            </template>"""
        )
    }

    fun buckStorageTypeRadioButtons(storageType: StorageType?): String {
        val radioButtons =
            formComponents.radioButtons(
                "boatApplication.title.storageType",
                "storageType",
                storageType?.name,
                listOf(
                    RadioOption(StorageType.Buck.name, t("boatApplication.option.buck")),
                    RadioOption(StorageType.BuckWithTent.name, t("boatApplication.option.buckTent"))
                ),
                mapOf("x-model" to "storageType"),
                isColumnLayout = true
            )
        return radioButtons
    }

    fun storageTypeRadioButtons(storageType: StorageType?): String {
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
        return radioButtons
    }

    fun render(
        trailerRegistrationNumber: String?,
        trailerWidth: BigDecimal?,
        trailerLength: BigDecimal?,
        storageType: StorageType? = StorageType.Trailer,
        fullWidth: Boolean = false,
    ): String {
        val radioButtons =
            storageTypeRadioButtons(storageType)

        return """<div data-testid="storage-type-selector" >
            $radioButtons
            ${trailerEdit(trailerRegistrationNumber, trailerWidth, trailerLength, fullWidth)}
            </div>
            """
    }

    fun renderWithWarningText(
        trailerRegistrationNumber: String?,
        trailerWidth: BigDecimal?,
        trailerLength: BigDecimal?,
        storageType: StorageType? = StorageType.Trailer,
        fullWidth: Boolean = false,
    ): String {
        val radioButtons =
            storageTypeRadioButtons(storageType)

        return """<div data-testid="storage-type-selector" >
            $radioButtons
            ${trailerEditWithWarningText(trailerRegistrationNumber, trailerWidth, trailerLength, fullWidth)}
            </div>
            """
    }
}
