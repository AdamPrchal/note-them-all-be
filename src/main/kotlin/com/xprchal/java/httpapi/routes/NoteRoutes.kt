package com.xprchal.java.httpapi.routes

import com.xprchal.java.httpapi.models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.noteRouting() {
    route("/note") {
        get {
            var notes: List<Note> = listOf()

            transaction{
                 notes = NoteT.all().toList().map {
                    it.getSerializable()
                }
                notes.forEach{ note ->
                    val links = NoteLinkT.find { NoteLinks.fromNote eq note.id}
                    note.links = links.map{ link ->
                        link.toNote.value
                    }
                }
            }

            call.respond(notes)

        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val note: Note? = transaction {
                NoteT.findById(id.toInt())?.getSerializable()
            }

            transaction {
                if(note != null){
                    val links = NoteLinkT.find { NoteLinks.fromNote eq note.id}
                    note.links = links.map{ link ->
                        link.toNote.value
                    }
                }
            }

            if (note != null){
                call.respond(note!!)
            }
            call.respondText(
                "No note with id $id",
                status = HttpStatusCode.NotFound
            )

        }
        post {
            val note = call.receive<Note>()
            var responseNote:Note = Note(0,"","")
            transaction {
                val tags = TagT.find { Tags.id inList note.tags}
                val links = NoteT.find {Notes.id inList note.links}

                val newNote = NoteT.new {
                    topic = note.topic
                    content = note.content
                }

                transaction{
                    newNote.tags = tags
                    links.map{
                         NoteLinkT.new {
                            fromNote = newNote.id
                            toNote = it.id
                        }
                    }

                }

                responseNote = newNote.getSerializable()

            }
            call.respond(responseNote)
        }

        patch("{id}") {
            val id = call.parameters["id"]
            val patchNote = call.receive<Note>()

            if(id == null){
                call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )
                return@patch
            }

            val note: NoteT? = transaction {
               NoteT.findById(id.toInt())
            }

            if (note != null){
                transaction {
                    val tags = TagT.find { Tags.id inList patchNote.tags}

                    val links = NoteT.find {Notes.id inList patchNote.links}
                    val linksIds = links.map {link->
                        link.id.value
                    }

                    val existingLinks = NoteLinkT.find{NoteLinks.fromNote eq note.id}
                    val existingLinksIds = existingLinks.map {link->
                        link.toNote.value
                    }

                    val newLinks = links.filter{ link ->
                        link.id.value !in existingLinksIds
                    }

                    val removedLinks = existingLinks.filter{ existingLink ->
                        existingLink.toNote.value !in linksIds
                    }


                    newLinks.forEach{ link ->
                        NoteLinkT.new {
                            fromNote = note.id
                            toNote = link.id
                        }
                    }

                    removedLinks.forEach{link ->
                        link.delete()
                    }


                    note.topic = patchNote.topic
                    note.content = patchNote.content
                    note.tags = tags

                }
                call.respondText("Note updated correctly", status = HttpStatusCode.NoContent)

            }
            call.respondText("Not Found", status = HttpStatusCode.NotFound)

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

            var note: NoteT? = null
            transaction {
                note = NoteT.findById(id.toInt())
            }

            if (note != null){
                transaction {
                    note!!.delete()
                }
                call.respondText("Note removed correctly", status = HttpStatusCode.Accepted)

            }
            call.respondText("Not Found", status = HttpStatusCode.NotFound)

        }
    }
}

fun Application.registerNoteRoutes() {
    routing {
        noteRouting()
    }
}
