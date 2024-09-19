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
        attributes: String = "",
        labelAttributes: String = "",
        compact: Boolean = false,
        serverValidate: Pair<String, String>? = null
    ): String {
        //language=HTML
        val errorContainer =
            """
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
            <div id="$id-server-error-container">
                <span id="$id-server-error" class="help is-danger" 
                    style="visibility: hidden">
                    ${if (serverValidate != null) t(serverValidate.second) else ""} 
                </span>
            </div> 
            """

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id" $labelAttributes >${t(labelKey)}</label>
                    <input
                        class="input ${if (compact) "compact" else ""}"
                        ${if (required == true) "data-required" else ""}
                        ${if (pattern != null) "data-pattern=\"${pattern.first}\"" else ""}
                        ${if (serverValidate != null) "data-validate-url=\"${serverValidate.first}\"" else ""}
                        type="text"
                        id="$id"
                        name="$id"
                        ${if (value != null) "value=\"$value\"" else ""}
                        $attributes />
                   $errorContainer
                </div>
            </div>
            """.trimIndent()
    }

    fun numberInput(
        labelKey: String,
        id: String,
        value: Int?,
        required: Boolean? = false,
        compact: Boolean = false
    ): String {
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <input
                        class="input ${if (compact) "compact" else ""}"
                        ${if (required == true) "data-required " else ""}
                        type="number"
                        id="$id"
                        name="$id"
                        ${if (value != null) "value=\"$value\"" else ""}
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

    fun decimalInput(
        labelKey: String,
        id: String,
        value: Double?,
        required: Boolean? = false,
        attributes: String = "",
        step: Double? = 0.01,
        compact: Boolean = false
    ): String {
        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <input
                        class="input ${if (compact) "compact" else ""}"
                        ${if (required == true) "data-required " else ""}
                        type="number"
                        step="$step"
                        id="$id"
                        name="$id"
                        ${if (value != null) "value=\"$value\"" else ""}
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
        attributes: String = ""
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
                        <select id="$id" name="$id" $attributes >
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

    fun field(
        labelKey: String,
        id: String,
        value: String?,
    ): String {
        //language=HTML
        return """
            <label class="label" for="$id">${t(labelKey)}</label>
             <p id="$id">$value</p>
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

    fun formHeader(titleKey: String): String {
        //language=HTML
        return """
            <h3 class="header">${t(titleKey)}</h3>
            """.trimIndent()
    }

    fun buttons(
        goBackUrl: String,
        target: String,
        select: String,
        cancelId: String,
        submitId: String
    ): String {
        //language=HTML
        return """
            <div class="buttons">
                    <button
                            id=$cancelId
                            class="button"
                            type="button"
                            hx-get=$goBackUrl
                            hx-target=$target
                            hx-select=$select
                            hx-swap="outerHTML"
                    >${t("cancel")}</button>
                    <button
                            id=$submitId
                            class="button is-primary"
                            type="submit"
                    >${t("citizenDetails.saveChanges")}</button>
                </div>
            """.trimIndent()
    }
}
