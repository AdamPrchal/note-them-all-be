package com.xprchal.java.httpapi.models

import NoteLinks
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Note(val id: Int = 0, val topic: String, val content: String, val tags: List<Int> = listOf(), var links: List<Int> = listOf())

object Notes: IntIdTable() {
    val topic = varchar("topic", 255)
    val content = text("content")
}

class NoteT(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NoteT>(Notes)

    var topic by Notes.topic
    var content by Notes.content

    var tags by TagT via NoteTags

    fun getSerializable(): Note {
        val tagsIds = tags.toList().map{
            it.id.value
        }
//
//        val linksIds = links.toList().map{
//            it.id.value
//        }

        return Note(id.value, topic, content, tagsIds)
    }
}