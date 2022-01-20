package com.xprchal.java.httpapi.routes

import com.xprchal.java.httpapi.models.Note
import com.xprchal.java.httpapi.models.NoteT
import com.xprchal.java.httpapi.models.Tag
import com.xprchal.java.httpapi.models.TagT
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.tagRouting() {
    route("/tag") {
        get {
            var tags: List<Tag> = listOf()

            transaction{
                tags = TagT.all().toList().map {
                    it.getSerializable()
                }
            }

            call.respond(tags)

        }
        get("{id}") {


        }
        post {
            val tag = call.receive<Tag>()
            transaction {
                val newTag = TagT.new {
                    name = tag.name
                }
            }
            call.respondText("Tag stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id}") {
            val id = call.parameters["id"]
            if(id == null){
                call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )
                return@delete
            }

            var tag: TagT? = null
            transaction {
                tag = TagT.findById(id.toInt())
            }

            if (tag != null){
                transaction {
                    tag!!.delete()
                }
                call.respondText("Tag removed correctly", status = HttpStatusCode.Accepted)

            }
            call.respondText("Not Found", status = HttpStatusCode.NotFound)

        }
    }
}

fun Application.registerTagRoutes() {
    routing {
        tagRouting()
    }
}
