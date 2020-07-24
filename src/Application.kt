package jp.kaleidot725.sample

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    Database.connect("jdbc:postgresql://localhost:15432/postgres", "org.postgresql.Driver", "postgres", "hello")
    transaction {
        // Create cities table
        SchemaUtils.create(Cities)

        // Create new city item
        City.new { name = "St. Petersburg" }

        // Get all city item
        val cities = City.all()
        cities.forEach { println("${it.name}") }
    }
}

object Cities: IntIdTable() {
    val name = varchar("name", 50)
}

class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)
    var name by Cities.name
}