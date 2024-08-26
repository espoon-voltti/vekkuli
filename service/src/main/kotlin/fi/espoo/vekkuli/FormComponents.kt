package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FormComponents {
    @Autowired
    lateinit var messageUtil: MessageUtil

    fun t(key: String): String {
        return messageUtil.getMessage(key)
    }

    fun textInput(
        labelKey: String,
        id: String,
        value: String?,
        errors: Map<String, String> = emptyMap(),
        required: Boolean? = false,
        pattern: Pair<String, String>?,
    ): String {
        val display = if (required == true && errors[id] != null) "visible" else "hidden"
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label" for="$id">${t(labelKey)}</label>
                    <input
                        class="input"
                        ${if (required == true) "data-required" else ""}
                        ${if (pattern != null) "data-pattern=\"${pattern.first}\"" else ""}
                        type="text"
                        id="$id"
                        name="$id"
                        value="${value ?: ""}"/>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: $display">
                            ${t("validation.required")} 
                        </span>
                    </div> 
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: $display">
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
        errors: Map<String, String>
    ): String {
        val display = if (required == true && errors[id] != null) "visible" else "hidden"
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label" for="$id">${t(labelKey)}</label>
                    <input
                        class="input"
                        ${if (required == true) "data-required" else ""}
                        type="number"
                        id="$id"
                        name="$id"
                        value="${value ?: ""}"/>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: $display">
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
        errors: Map<String, String>
    ): String {
        val display = if (required == true && errors[id] != null) "visible" else "hidden"
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label" for="$id">${t(labelKey)}</label>
                    <input
                        class="input"
                        ${if (required == true) "data-required" else ""}
                        type="number"
                        step="0.01"
                        id="$id"
                        name="$id"
                        value="${value ?: ""}"/>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: $display">
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
        value: String?,
        options: List<Pair<String, String>>,
        required: Boolean? = false,
        errors: Map<String, String>
    ): String {
        val display = if (required == true && errors[id] != null) "visible" else "hidden"

        //language=HTML
        val opts =
            options.joinToString("\n") { (key, value) ->
                """<option value="$key" ${if (key == value) "selected" else ""}>$value</option>"""
            }

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label" for="$id">${t(labelKey)}</label>
                    <div class="select">
                        <select id="$id" name="$id">
                            $opts
                        </select>
                    </div>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: $display">
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
        errors: Map<String, String>
    ): String {
        val display = if (required == true && errors[id] != null) "visible" else "hidden"

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
                    <label class="label" for="$id">${t(labelKey)}</label>
                    <div class="select">
                        $opts
                    </div>
                    <div id="$id-error-container">
                        <span id="$id-error" class="help is-danger" 
                            style="visibility: $display">
                            ${t("validation.required")} 
                        </span>
                    </div> 
                </div>
            </div>
            """.trimIndent()
    }
}
