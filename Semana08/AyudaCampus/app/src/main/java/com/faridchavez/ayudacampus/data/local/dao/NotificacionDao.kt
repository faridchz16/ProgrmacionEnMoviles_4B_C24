package com.faridchavez.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.faridchavez.ayudacampus.data.local.entity.Notificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert
    suspend fun insertar(n: Notificacion)

    @Query("SELECT * FROM notificaciones WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarDeUsuario(usuarioId: Int): Flow<List<Notificacion>>

    @Query("SELECT * FROM notificaciones WHERE paraRol = 'ADMIN' ORDER BY fecha DESC")
    fun observarDeSoporte(): Flow<List<Notificacion>>

    @Query("SELECT * FROM notificaciones ORDER BY id DESC LIMIT 1")
    fun observarUltima(): Flow<Notificacion?>

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: Int)

    @Query("UPDATE notificaciones SET leida = 1 WHERE usuarioId = :usuarioId")
    suspend fun marcarTodasDeUsuario(usuarioId: Int)

    @Query("UPDATE notificaciones SET leida = 1 WHERE paraRol = 'ADMIN'")
    suspend fun marcarTodasDeSoporte()
}