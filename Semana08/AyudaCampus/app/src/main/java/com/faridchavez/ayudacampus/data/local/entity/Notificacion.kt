package com.faridchavez.ayudacampus.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notificaciones",
    indices = [Index("usuarioId"), Index("paraRol")]
)
data class Notificacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int? = null,
    val paraRol: String,
    val ticketId: Int?,
    val titulo: String,
    val mensaje: String,
    val leida: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)