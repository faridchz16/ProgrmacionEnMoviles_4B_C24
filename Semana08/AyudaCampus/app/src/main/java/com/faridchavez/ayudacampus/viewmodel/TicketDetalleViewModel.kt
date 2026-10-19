package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TicketDetalleViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val usuario: Usuario
) : ViewModel() {

    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .map { t -> t?.takeIf { it.ticket.clienteId == usuario.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    val usuarioId: Int = usuario.id

    fun comentar(texto: String) {
        val t = ticket.value ?: return
        if (texto.isBlank() || t.estado == EstadoTicket.CERRADO) return
        viewModelScope.launch { repo.agregarComentario(ticketId, usuario, texto.trim()) }
    }

    fun cerrar(calificacion: Int, comentarioFinal: String = "") {
        val t = ticket.value ?: return
        if (t.estado != EstadoTicket.RESUELTO) {
            _mensaje.value = "Solo puedes cerrar un ticket resuelto"
            return
        }
        if (calificacion !in 1..5) {
            _mensaje.value = "Califica la atención de 1 a 5 estrellas para cerrar"
            return
        }
        viewModelScope.launch {
            if (comentarioFinal.isNotBlank()) {
                repo.agregarComentario(ticketId, usuario, comentarioFinal.trim())
            }
            repo.cerrarConCalificacion(ticketId, calificacion)
        }
    }

    fun reabrir() {
        val t = ticket.value ?: return
        if (t.estado != EstadoTicket.RESUELTO) {
            _mensaje.value = "Solo puedes reabrir un ticket resuelto"
            return
        }
        viewModelScope.launch { repo.reabrir(ticketId) }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}