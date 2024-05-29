package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle

data class AddCitizen(
    val name: String,
    val email: String,
    val phone: String,
)

data class Citizen(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
)

fun Handle.getCitizen(id: Int): Citizen? {
    return createQuery("SELECT * FROM citizen WHERE id = :id")
        .bind("id", id)
        .mapTo(Citizen::class.java).toList().first()
}

fun Handle.insertCitizen(citizen: AddCitizen): Citizen {
    return createQuery("""
        INSERT INTO citizen (name, email, phone) 
        VALUES (:name, :email, :phone)
        RETURNING *
    """.trimIndent())
        .bind("name", citizen.name)
        .bind("email", citizen.email)
        .bind("phone", citizen.phone)
        .mapTo(Citizen::class.java)
        .toList()
        .first()
}