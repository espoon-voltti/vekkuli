package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FormComponents {
    @Autowired
    lateinit var messageUtil: MessageUtil

    fun t(key: String): String = messageUtil.getMessage(key)

    fun textInput(
        labelKey: String,
        id: String,
        value: String?,
        required: Boolean? = false,
        pattern: Pair<String, String>? = null,
        attributes: String = ""
    ): String {
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <input
                        class="input"
                        ${if (required == true) "data-required" else ""}
                        ${if (pattern != null) "data-pattern=\"${pattern.first}\"" else ""}
                        type="text"
                        id="$id"
                        name="$id"
                        value="${value ?: ""}"
                        $attributes />
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: hidden">
                            ${t("validation.required")} 
                        </span>
                    </div> 
                    <div id="$id-error-container">
                        <span id="$id-pattern-error" class="help is-danger" 
                            style="visibility: hidden">
                            ${if (pattern != null) t(pattern.second) else ""} 
                        </span>
                    </div> 
                </div>
            </div>
            """.trimIndent()
    }

    fun numberInput(
        labelKey: String,
        id: String,
        value: Int?,
        required: Boolean? = false,
    ): String {
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <input
                        class="input"
                        ${if (required == true) "data-required" else ""}
                        type="number"
                        id="$id"
                        name="$id"
                        value="${value ?: ""}"/>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: hidden">
                            ${t("validation.required")} 
                        </span>
                    </div> 
                </div>
            </div>
            """.trimIndent()
    }

    fun decimalInput(
        labelKey: String,
        id: String,
        value: Double?,
        required: Boolean? = false,
        attributes: String = "",
        step: Double? = 0.01
    ): String {
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <input
                        class="input"
                        ${if (required == true) "data-required" else ""}
                        type="number"
                        step="$step"
                        id="$id"
                        name="$id"
                        value="${value ?: ""}"
                        $attributes
                        />
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: hidden">
                            ${t("validation.required")} 
                        </span>
                    </div> 
                </div>
            </div>
            """.trimIndent()
    }

    fun select(
        labelKey: String,
        id: String,
        selectedValue: String?,
        options: List<Pair<String, String>>,
        required: Boolean? = false,
    ): String {
        //language=HTML
        val opts =
            options.joinToString("\n") { (value, text) ->
                """<option value="$value" ${if (value == selectedValue) "selected" else ""}>$text</option>"""
            }

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <div class="select">
                        <select id="$id" name="$id">
                            $opts
                        </select>
                    </div>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: hidden">
                            ${t("validation.required")} 
                        </span>
                    </div> 
                </div>
            </div>
            """.trimIndent()
    }

    fun radioButtons(
        labelKey: String,
        id: String,
        value: String?,
        options: List<Pair<String, String>>,
        required: Boolean? = false,
    ): String {
        //language=HTML
        val opts =
            options.joinToString("\n") { (key, value) ->
                """<input type="radio" id="$id" name="$id" value="$key" ${if (key == value) "checked" else ""}>
                <label for="$key">$value</label>"""
            }

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <div class="select">
                        $opts
                    </div>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: hidden">
                            ${t("validation.required")} 
                        </span>
                    </div> 
                </div>
            </div>
            """.trimIndent()
    }
}
