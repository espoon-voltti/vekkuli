package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.views.components.modal.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class ModalBuilderTest {
    @Test
    fun `test building a simple modal`() {
        val modalBuilder = ModalBuilder()
            .setTitle("Test Modal")
            .setContent("<p>This is a test modal.</p>")
            .addButton {
                setText("Close")
                setType(ModalButtonType.Cancel)
                setStyle(ModalButtonStyle.Default)
                addAttribute("x-on:click", "isOpen = false")
            }

        val modalHtml = modalBuilder.build()

        assertNotNull(modalHtml)
        assertTrue(modalHtml.contains("<h3>Test Modal</h3>"))
        assertTrue(modalHtml.contains("<p>This is a test modal.</p>"))
        assertTrue(modalHtml.contains("Close"))
        assertTrue(modalHtml.contains("type=\"button\""))
        assertTrue(modalHtml.contains("x-on:click=\"isOpen = false\""))
    }

    @Test
    fun `test modal without title`() {
        val modalBuilder = ModalBuilder()
            .setContent("<p>No title modal.</p>")
            .addButton {
                setText("OK")
                setType(ModalButtonType.Button)
                setStyle(ModalButtonStyle.Primary)
            }
        val modalHtml = modalBuilder.build()

        assertNotNull(modalHtml)
        assertFalse(modalHtml.contains("<h3>"))
        assertTrue(modalHtml.contains("<p>No title modal.</p>"))
        assertTrue(modalHtml.contains("OK"))
        assertTrue(modalHtml.contains("button is-primary"))
    }

    @Test
    fun `test adding multiple buttons`() {

        val modalBuilder = ModalBuilder()
            .setTitle("Multiple Buttons Modal")
            .setContent("<p>Testing multiple buttons.</p>")
            .addButton {
                setText("Cancel")
                setType(ModalButtonType.Cancel)
                setStyle(ModalButtonStyle.Default)
            }
            .addButton {
                setText("Accept")
                setType(ModalButtonType.Submit)
                setStyle(ModalButtonStyle.Primary)
                addAttribute("form", "acceptForm")
            }

        val modalHtml = modalBuilder.build()

        assertNotNull(modalHtml)
        assertTrue(modalHtml.contains("Multiple Buttons Modal"))
        assertTrue(modalHtml.contains("Cancel"))
        assertTrue(modalHtml.contains("Accept"))
        assertTrue(modalHtml.contains("type=\"submit\""))
        assertTrue(modalHtml.contains("form=\"acceptForm\""))
    }

    @Test
    fun `test modal with danger button`() {
        val modalBuilder = ModalBuilder()
            .setTitle("Warning")
            .setContent("<p>This action is irreversible.</p>")
            .addButton {
                setText("Delete")
                setType(ModalButtonType.Button)
                setStyle(ModalButtonStyle.Danger)
                addAttribute("hx-delete", "/delete/item")
                addAttribute("hx-target", "#item-list")
            }

        val modalHtml = modalBuilder.build()

        assertNotNull(modalHtml)
        assertTrue(modalHtml.contains("Warning"))
        assertTrue(modalHtml.contains("This action is irreversible."))
        assertTrue(modalHtml.contains("Delete"))
        assertTrue(modalHtml.contains("button is-danger"))
        assertTrue(modalHtml.contains("hx-delete=\"/delete/item\""))
        assertTrue(modalHtml.contains("hx-target=\"#item-list\""))
    }
}