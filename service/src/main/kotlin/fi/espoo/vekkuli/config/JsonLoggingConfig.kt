// SPDX-FileCopyrightText: 2023-2026 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

/**
 * Placeholder for historical JSON logging customization.
 *
 * logstash-logback-encoder v9 uses a new Decorator API and provides built-in
 * SerializationFeatureDecorator. The previous JsonFactoryDecorator-based
 * customization is replaced by configuring the SerializationFeatureDecorator
 * in logback XML.
 */
@Suppress("unused")
class JsonLoggingConfig {
    // Intentionally left empty to avoid referencing removed APIs from v9.
}
