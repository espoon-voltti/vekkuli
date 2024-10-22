// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.utils

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.mockito.Mockito
import java.io.File
import java.time.LocalDateTime

fun dataQa(s: String) = "[data-qa='$s']"

fun createAndSeedDatabase(jdbi: Jdbi) {
    jdbi.withHandleUnchecked { tx ->
        tx.execute(
            """
            CREATE OR REPLACE FUNCTION reset_database() RETURNS void AS ${'$'}${'$'}
            BEGIN
              EXECUTE (
                SELECT 'TRUNCATE TABLE ' || string_agg(quote_ident(table_name), ', ') || ' CASCADE'
                FROM information_schema.tables
                WHERE table_schema = 'public'
                AND table_type = 'BASE TABLE'
                AND table_name <> 'flyway_schema_history'
              );
              EXECUTE (
                SELECT 'SELECT ' || coalesce(string_agg(format('setval(%L, %L, false)', sequence_name, start_value), ', '), '')
                FROM information_schema.sequences
                WHERE sequence_schema = 'public'
              );
            END ${'$'}${'$'} LANGUAGE plpgsql;
            """.trimIndent()
        )

        jdbi.withHandleUnchecked { tx ->
            tx.execute(
                """
                SELECT reset_database();
                """.trimIndent()
            )
        }
        // execute seed.sql
        val file = File("src/e2eTest/resources/seed.sql").readText()
        jdbi.withHandleUnchecked { h ->
            h.createScript(file).execute()
        }
    }
}

fun mockTimeProvider(
    timeProvider: TimeProvider,
    date: LocalDateTime = LocalDateTime.of(2024, 4, 1, 0, 0, 0)
) {
    // Mock the methods
    Mockito.`when`(timeProvider.getCurrentDateTime()).thenReturn(date)
    Mockito.`when`(timeProvider.getCurrentDate()).thenReturn(date.toLocalDate())
}
