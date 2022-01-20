package com.xprchal.java.httpapi

import NoteLinks
import com.xprchal.java.httpapi.models.NoteTags
import com.xprchal.java.httpapi.models.Notes
import com.xprchal.java.httpapi.models.Tags
import com.xprchal.java.httpapi.routes.registerNoteRoutes
import com.xprchal.java.httpapi.routes.registerTagRoutes
import io.ktor.application.Application
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(CORS){
        host("localhost:3000")
        header(HttpHeaders.ContentType)
    }

    install(ContentNegotiation) {
        json()
    }

    registerNoteRoutes()
    registerTagRoutes()

    Database.connect("jdbc:sqlite:database/data.db", "org.sqlite.JDBC")

    transaction {

        SchemaUtils.create (Notes)
        SchemaUtils.create (Tags)
        SchemaUtils.create (NoteTags)
        SchemaUtils.create (NoteLinks)

    }
}

