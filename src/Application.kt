package jp.kaleidot725.sample

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val db = Database.connect(
            url = "jdbc:postgresql://localhost:15432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "hello"
    )

    transaction(db) {
        // Create cities table
        SchemaUtils.create(Cities)

        // Create new city item
        Cities.insert { it[name] = "St. Petersburg" }

        // Get all city item
        Cities.selectAll().forEach { println("${it[Cities.name]}")}
    }
}

object Cities: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}