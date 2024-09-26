package fi.espoo.vekkuli.service

import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.AttributeProvider
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.stereotype.Service

internal class ParagraphAttributeProvider : AttributeProvider {
    override fun setAttributes(
        node: Node?,
        tagName: String?,
        attributes: MutableMap<String?, String?>
    ) {
        if (node is Paragraph) {
            attributes["class"] = "block"
        }
        if (node is Link) {
            attributes["target"] = "_blank"
        }
    }
}

@Service
class MarkDownService {
    private val parser = Parser.builder().build()

    private val renderer =
        HtmlRenderer
            .builder()
            .attributeProviderFactory { ParagraphAttributeProvider() }
            .build()

    fun render(md: String): String = renderer.render(parser.parse(md))
}
