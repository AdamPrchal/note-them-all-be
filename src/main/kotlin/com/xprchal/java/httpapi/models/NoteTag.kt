package com.xprchal.java.httpapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class NoteTag(val id: Int = 0, val noteId: Int, val tagId: Int)

object NoteTags: Table() {
    val noteId = reference("note_id", Notes)
    val tagId = reference("tag_id", Tags)

    override val primaryKey = PrimaryKey(arrayOf(noteId, tagId))
}
