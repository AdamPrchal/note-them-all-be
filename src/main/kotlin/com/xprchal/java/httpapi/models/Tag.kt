package com.xprchal.java.httpapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Tag(val id: Int = 0, val name: String)

object Tags: IntIdTable() {
    val name = varchar("name", 255)
}

class TagT(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TagT>(Tags)
    var name by Tags.name

    fun getSerializable(): Tag {
        return Tag(id.value, name)
    }
}