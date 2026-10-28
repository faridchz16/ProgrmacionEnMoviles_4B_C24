package com.faridchavez.ayudacampus.data.repository

import com.faridchavez.ayudacampus.data.local.entity.Categoria
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.model.ConteoCategoria
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad
import com.faridchavez.ayudacampus.model.Rol
import kotlinx.coroutines.flow.Flow
import com.faridchavez.ayudacampus.data.local.entity.Notificacion

interface AyudaRepository {

    // Usuarios
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun buscarUsuario(id: Int): Usuario?
    suspend fun primerUsuarioConRol(rol: Rol): Usuario?
    suspend fun buscarUsuarioPorCorreo(correo: String): Usuario?
    suspend fun registrarEstudiante(nombre: String, correo: String, clave: String): Usuario
    suspend fun cambiarClave(usuarioId: Int, nuevaClave: String)

    // Categorías (ejemplo resuelto)
    fun observarCategorias(): Flow<List<Categoria>>
    fun observarCategoriasActivas(): Flow<List<Categoria>>
    suspend fun buscarCategoria(id: Int): Categoria?
    suspend fun guardarCategoria(c: Categoria)
    suspend fun eliminarCategoria(c: Categoria)
    suspend fun cambiarActiva(id: Int, activa: Boolean)
    suspend fun contarTicketsDeCategoria(categoriaId: Int): Int

    // Tickets
    suspend fun crearTicket(
        clienteId: Int,
        categoriaId: Int,
        titulo: String,
        descripcion: String,
        ubicacion: String,
        prioridad: Prioridad
    ): Long

    fun observarTicketsCliente(clienteId: Int): Flow<List<TicketConDetalle>>
    fun observarTodosLosTickets(): Flow<List<TicketConDetalle>>
    fun observarTicket(id: Int): Flow<TicketConDetalle?>

    suspend fun cambiarEstado(ticketId: Int, estado: EstadoTicket)
    suspend fun cambiarPrioridad(ticketId: Int, prioridad: Prioridad)
    suspend fun cerrarConCalificacion(ticketId: Int, calificacion: Int)
    suspend fun reabrir(ticketId: Int)
    suspend fun agregarComentario(ticketId: Int, autor: Usuario, texto: String, interno: Boolean = false)

    // Panel
    fun observarCantidadPorEstado(estado: EstadoTicket): Flow<Int>
    fun observarCantidadPorCategoria(): Flow<List<ConteoCategoria>>
    fun observarAltasSinAtender(limite: Long): Flow<Int>
    fun observarTiempoPromedioResolucion(): Flow<Double?>

    // Notificaciones
    fun observarNotificaciones(usuario: Usuario): Flow<List<Notificacion>>
    fun observarUltimaNotificacion(): Flow<Notificacion?>
    suspend fun marcarNotificacionLeida(id: Int)
    suspend fun marcarTodasLeidas(usuario: Usuario)
}
