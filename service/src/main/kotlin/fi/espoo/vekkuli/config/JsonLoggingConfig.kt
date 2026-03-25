// SPDX-FileCopyrightText: 2023-2026 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import net.logstash.logback.decorate.MapperBuilderDecorator
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.cfg.MapperBuilder

/**
 * Decorator applied by logback to customize the Jackson MapperBuilder used by the
 * logstash-logback-encoder. It disables WRITE_DATES_AS_TIMESTAMPS in a programmatic
 * way to avoid XML enum parsing issues with the built-in SerializationFeatureDecorator.
 */
@Suppress("unused")
class JsonLoggingConfig : net.logstash.logback.decorate.Decorator<Any> {
    override fun decorate(obj: Any): Any {
        try {
            val clazz = obj::class.java
            // If we're handed a MapperBuilder, try to invoke configure(SerializationFeature, boolean)
            try {
                val featureClass = Class.forName("tools.jackson.databind.SerializationFeature")
                val boolType = java.lang.Boolean.TYPE
                val configure = clazz.methods.firstOrNull { m ->
                    val params = m.parameterTypes
                    params.size == 2 && params[0].name == featureClass.name && params[1] == boolType
                }
                if (configure != null) {
                    val enumConst = java.lang.Enum.valueOf(featureClass as Class<out Enum<*>>, "WRITE_DATES_AS_TIMESTAMPS")
                    configure.invoke(obj, enumConst, java.lang.Boolean.FALSE)
                    return obj
                }
            } catch (_: ClassNotFoundException) {
                // tools.jackson shaded classes not present, ignore
            }
            // If we're handed an ObjectMapper, try to set the feature directly
            if (clazz.name == "tools.jackson.databind.ObjectMapper" || clazz.name == "com.fasterxml.jackson.databind.ObjectMapper") {
                try {
                    val featureClass = Class.forName("tools.jackson.databind.SerializationFeature")
                    val setConfig = clazz.methods.firstOrNull { m -> m.name == "setConfig" }
                    // fallback: call configure if present
                    val configure = clazz.methods.firstOrNull { m -> m.name == "configure" && m.parameterTypes.size == 2 }
                    if (configure != null) {
                        val enumConst = java.lang.Enum.valueOf(featureClass as Class<out Enum<*>>, "WRITE_DATES_AS_TIMESTAMPS")
                        configure.invoke(obj, enumConst, java.lang.Boolean.FALSE)
                    }
                } catch (_: Throwable) {
                    // ignore
                }
            }
        } catch (_: Throwable) {
            // ignore any reflection problems
        }
        return obj
    }
}

