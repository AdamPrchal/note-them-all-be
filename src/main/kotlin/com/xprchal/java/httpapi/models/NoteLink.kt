
import com.xprchal.java.httpapi.models.Notes
import com.xprchal.java.httpapi.models.Tag
import com.xprchal.java.httpapi.models.Tags
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

@Serializable
data class NoteLink(val fromNote: Int, val toNote: Int)

object NoteLinks: IntIdTable() {
    val fromNote = reference("from_note", Notes)
    val toNote = reference("to_note", Notes)

//    override val primaryKey = PrimaryKey(arrayOf(fromNote, toNote))
}


class NoteLinkT(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<NoteLinkT>(NoteLinks)
    var fromNote by NoteLinks.fromNote
    var toNote by NoteLinks.toNote

    fun getSerializable(): NoteLink {
        return NoteLink(fromNote.value, toNote.value)
    }
}