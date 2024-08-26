package fi.espoo.vekkuli.views.citizen

class Layout {
    fun generateLayout(
        isAuthenticated: Boolean,
        userName: String?,
        bodyContent: String
    ): String {
        // language=HTML
        val menu =
            if (!isAuthenticated) {
                """
                <a id="loginButton"
                   class="link"
                   href="/auth/saml-suomifi/login">Login</a>
                """.trimIndent()
            } else {
                """
                <div class="container" x-data="{ open: false }">
                    <div class="dropdown" :class="{ 'is-active': open }">
                        <div class="dropdown-trigger">
                            <a aria-haspopup="true" aria-controls="dropdown-menu" @click="open = !open">
                                <span>${userName ?: "Dropdown"}</span>
                                <span class="icon is-small">
                                    <i class="fas fa-angle-down" aria-hidden="true"></i>
                                </span>
                            </a>
                        </div>
                        <div class="dropdown-menu" id="dropdown-menu" role="menu">
                            <div class="dropdown-content">
                                <a href="/auth/saml-suomifi/logout" class="dropdown-item">
                                    Logout
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                """.trimIndent()
            }

        // language=HTML
        return """
            <!DOCTYPE html>
            <html class="theme-light">
            <head>
                <title>Vekkuli</title>
                ${head()} <!-- Function to generate head resources -->
            </head>
            <body>

            <nav class="navbar mb-s" role="navigation" aria-label="main navigation">
                <div class="navbar-brand">
                    <a class="navbar-item" href="/">
                        <img src="/static/images/espoo_logo.png" alt="Espoo logo" />
                    </a>
                </div>
                <div class="navbar-end" style="margin-right: 132px">
                    <div class="navbar-item">
                        <div class="buttons">
                            $menu
                        </div>
                    </div>
                </div>
            </nav>
            
            <div>
                $bodyContent <!-- Insert the page-specific content here -->
            </div>

            </body>
            </html>
            """.trimIndent()
    }

    fun head(): String {
        // language=HTML
        return """
            <script src="https://unpkg.com/htmx.org@2.0.1"
                    integrity="sha384-QWGpdj554B4ETpJJC9z+ZHJcA/i59TyjxEPXiiUgN2WmTyV5OEZWCD6gQhgkdpB/"
                    crossorigin="anonymous"></script>

            <meta name="htmx-config" content='{"allowNestedOobSwaps":false}'>
            <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
            <script src="/static/validation.js"></script>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <link rel="stylesheet" type="text/css" href="/static/css/main.css">
            """.trimIndent()
    }
}
