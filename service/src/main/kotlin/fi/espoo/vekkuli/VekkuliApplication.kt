// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli

import AppEnv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties(AppEnv::class)
@EnableScheduling
class VekkuliApplication

fun main(args: Array<String>) {
    runApplication<VekkuliApplication>(*args)
}
