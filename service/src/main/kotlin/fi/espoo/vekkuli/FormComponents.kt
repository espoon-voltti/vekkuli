package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.MessageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.util.HtmlUtils
import java.math.BigDecimal

data class RadioOption(
    val value: String,
    val label: String,
    val subLabel: String? = null
)

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
        serverValidate: Pair<String, String>? = null,
        type: String = "text",
        name: String = id
    ): String {
        val errorContainer = renderErrorContainer(id, pattern, serverValidate)

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id" $labelAttributes>${t(labelKey)}</label>
                    <input
                        class="input ${if (compact) "compact" else ""}"
                        ${if (required == true) "data-required" else ""}
                        ${if (pattern != null) "data-pattern=\"${pattern.first}\"" else ""}
                        ${if (serverValidate != null) "data-validate-url=\"${serverValidate.first}\"" else ""}
                        type="$type"
                        id="$id"
                        name="$name"
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
        compact: Boolean = false,
        attributes: String = "",
    ): String =
        textInput(
            labelKey = labelKey,
            id = id,
            value = value?.toString(),
            required = required,
            compact = compact,
            type = "number",
            attributes =
                """
                step="1"
                min="1"
                max="9999999"
                @change="${"$"}el.value !== '' && (
                    parseFloat(${"$"}el.value) < parseFloat(${'$'}el.min) ? ${"$"}el.value = ${"$"}el.min : 
                    parseFloat(${"$"}el.value) > parseFloat(${'$'}el.max) ? ${"$"}el.value = ${"$"}el.max : 
                    ${'$'}el.value = Math.round(parseFloat(${'$'}el.value))
                )"
                $attributes
                """.trimIndent()
        )

    fun decimalInput(
        labelKey: String,
        id: String,
        value: BigDecimal?,
        required: Boolean? = false,
        attributes: String = "",
        step: Double? = 0.01,
        compact: Boolean = false
    ): String =
        textInput(
            labelKey = labelKey,
            id = id,
            value = value?.toString(),
            required = required,
            compact = compact,
            type = "number",
            attributes =
                """
                step="${step?.toString() ?: "0.01"}"
                min="0"
                max="9999999"
                @change="${"$"}el.value !== '' && (
                    parseFloat(${"$"}el.value) < parseFloat(${'$'}el.min) ? ${"$"}el.value = ${"$"}el.min : 
                    parseFloat(${"$"}el.value) > parseFloat(${'$'}el.max) ? ${"$"}el.value = ${"$"}el.max : 
                    ${'$'}el.value
                )"
                $attributes
                """.trimIndent()
        )

    fun select(
        labelKey: String,
        id: String,
        selectedValue: String?,
        options: List<Pair<String, String>>,
        required: Boolean? = false,
        attributes: String = "",
        isFullWidth: Boolean = false,
        placeholder: String? = ""
    ): String {
        //language=HTML
        var opts =
            options.joinToString("\n") { (value, text) ->
                """<option value="$value" ${if (value == selectedValue) "selected" else ""}>$text</option>"""
            }
        val errorContainer = renderValidationErrorContainer(id)

        if (selectedValue == null && placeholder != null) {
            opts = "<option disabled selected>$placeholder</option>$opts"
        }

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id">${t(labelKey)}</label>
                    <div class="select${if (isFullWidth == true) " is-fullwidth" else ""}">
                        <select id="$id" name="$id" $attributes >
                            $opts
                        </select>
                    </div>
                    $errorContainer
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
            <div class='field' >
                <label class="label">${t(labelKey)}</label>
                 <p id="$id">${if (value.isNullOrEmpty()) '-' else value}</p>
             </div>
            """.trimIndent()
    }

    fun radioButtons(
        labelKey: String,
        id: String,
        defaultValue: String?,
        options: List<RadioOption>,
        staticAttributesForOptions: Map<String, String> = emptyMap(),
        isColumnLayout: Boolean = false
    ): String {
        //language=HTML
        val opts =
            options.joinToString("\n") { opt ->
                """ <label class="radio ${if (isColumnLayout) "column is-narrow" else "has-text-top-aligned" } for="${opt.value}" xmlns="http://www.w3.org/1999/html">
                     <input type="radio" id="$id-${opt.value}" name="$id" value="${opt.value}" ${if (opt.value == defaultValue) "checked" else ""} ${
                    staticAttributesForOptions.map {
                        "${it.key}=${HtmlUtils.htmlEscape(it.value, "UTF-8")}"
                    }.joinToString(
                        " "
                    )
                }>
                <div class='label-text' >
                    <p class="body">${opt.label}</p>
                    ${if (opt.subLabel != null) """<p class="mt-s information-text">${opt.subLabel}</p>""" else ""}
                </div>
                </label>
                
                """
            }

        //language=HTML
        return """
            <div class="field">
               <label class="label" for="$id">${t(labelKey)}</label>
                <div class="control ${if (isColumnLayout) "columns" else ""}">
                  $opts
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

    fun dateInput(options: DateInputOptions): String {
        val (id, labelKey, value, required, pattern, attributes, labelAttributes, compact, serverValidate, autoWidth) = options
        val errorContainer = renderErrorContainer(id, pattern, serverValidate)

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label class="label ${if (required == true) "required" else ""}" for="$id" $labelAttributes >${t(labelKey)}</label>
                    <input
                    lang="fi"
                        class="input${if (compact) " compact" else ""}${if (autoWidth) " auto-width" else ""}"
                        ${if (required == true) "data-required" else ""}
                        ${if (pattern != null) "data-pattern=\"${pattern.first}\"" else ""}
                        ${if (serverValidate != null) "data-validate-url=\"${serverValidate.first}\"" else ""}
                        type="date"
                        id="$id"
                        name="$id"
                        ${if (value != null) "value=\"$value\"" else ""}
                        $attributes />
                   $errorContainer
                </div>
            </div>
            """.trimIndent()
    }

    fun textArea(options: TextAreaOptions): String {
        //language=HTML
        val errorContainer = renderErrorContainer(options.id, null, options.serverValidate)
        val classes = mutableListOf<String>()
        if (options.compact) {
            classes.add("compact")
        }
        if (options.resizable) {
            classes.add("resizable")
        }

        //language=HTML
        return """
            <div class="field">
                <div class="control">
                    <label 
                        class="label ${if (options.required == true) "required" else ""}"
                        for="${options.id}"
                        ${options.labelAttributes}
                    >
                        ${t(options.labelKey)}
                    </label>
                    <textarea
                        class="textarea${classes.joinToString { " " + it }}"
                        ${if (options.required == true) "data-required" else ""}
                        ${if (options.serverValidate != null) "data-validate-url=\"${options.serverValidate.first}\"" else ""}
                        ${if (options.rows != null) "rows=\"${options.rows}\"" else ""}
                        id="${options.id}"
                        name="${options.name}"
                        ${options.attributes}>${options.value}</textarea>
                   $errorContainer
                </div>
            </div>
            """.trimIndent()
    }

    private fun renderErrorContainer(
        id: String,
        pattern: Pair<String, String>? = null,
        serverValidate: Pair<String, String>? = null
    ): String =
        """
        ${renderValidationErrorContainer(id)}
        ${renderPatternErrorContainer(id, pattern)}
        ${renderServerErrorContainer(id, serverValidate)}
        """.trimIndent()

    private fun renderValidationErrorContainer(id: String): String {
        //language=HTML
        return """
            <div id="$id-error-container">
                <span id="$id-error" class="help is-danger"
                style="display: none">
                ${t("validation.required")}
                </span>
            </div>
            """.trimIndent()
    }

    private fun renderPatternErrorContainer(
        id: String,
        pattern: Pair<String, String>? = null
    ): String {
        if (pattern == null) {
            return ""
        }
        //language=HTML
        return """
            <div id="$id-error-container">
                <span id="$id-pattern-error" class="help is-danger"
                style="display: none">
                ${t(pattern.second)}
                </span>
            </div>
            """.trimIndent()
    }

    private fun renderServerErrorContainer(
        id: String,
        serverValidate: Pair<String, String>? = null
    ): String {
        if (serverValidate == null) {
            return ""
        }
        //language=HTML
        return """
            <div id="$id-server-error-container">
                <span id="$id-server-error" class="help is-danger" 
                    style="display: none">
                    ${t(serverValidate.second)} 
                </span>
            </div>
            """.trimIndent()
    }
}

data class DateInputOptions(
    val id: String,
    val labelKey: String,
    val value: String?,
    val required: Boolean? = false,
    val pattern: Pair<String, String>? = null,
    val attributes: String = "",
    val labelAttributes: String = "",
    val compact: Boolean = false,
    val serverValidate: Pair<String, String>? = null,
    val autoWidth: Boolean = false
)

data class TextAreaOptions(
    val labelKey: String,
    val id: String,
    val name: String = id,
    val value: String? = "",
    val required: Boolean? = false,
    val attributes: String = "",
    val labelAttributes: String = "",
    val rows: Int? = null,
    val compact: Boolean = false,
    val serverValidate: Pair<String, String>? = null,
    val resizable: Boolean = false
)
