// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.oppivelvollisuus

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import testUser

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
abstract class FullApplicationTest {
    @Autowired
    protected lateinit var jdbi: Jdbi

    @BeforeAll
    fun beforeAll() {
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
        }
    }

    @BeforeEach
    fun beforeEach() {
        jdbi.withHandleUnchecked { tx ->
            tx.execute("SELECT reset_database()")
            tx.createUpdate(
                """
                INSERT INTO users (id, updated, external_id, first_names, last_name, email) 
                VALUES (:id, now(), 'test', 'Teija', 'Testaaja', NULL)
            """
            ).bind("id", testUser.id).execute()
        }
    }
}
