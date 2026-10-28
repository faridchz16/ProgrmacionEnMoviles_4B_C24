package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.local.relation.prioridad
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AtenderViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val usuario: Usuario
) : ViewModel() {

    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _estadoElegido = MutableStateFlow<EstadoTicket?>(null)
    private val _prioridadElegida = MutableStateFlow<Prioridad?>(null)
    private val _esNotaInterna = MutableStateFlow(false)

    val estadoSeleccionado: StateFlow<EstadoTicket?> =
        combine(ticket, _estadoElegido) { t, elegido -> elegido ?: t?.estado }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val prioridadSeleccionada: StateFlow<Prioridad?> =
        combine(ticket, _prioridadElegida) { t, elegida -> elegida ?: t?.prioridad }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val esNotaInterna: StateFlow<Boolean> = _esNotaInterna.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    val usuarioId: Int = usuario.id

    private fun estaCerrado(): Boolean = ticket.value?.estado == EstadoTicket.CERRADO

    fun puedeElegir(estado: EstadoTicket): Boolean =
        estado != EstadoTicket.CERRADO && !estaCerrado()

    fun cambiarEstado(estado: EstadoTicket) {
        if (ticket.value == null) return
        when {
            estaCerrado() -> _mensaje.value = "No se puede cambiar el estado de un ticket cerrado"
            estado == EstadoTicket.CERRADO -> _mensaje.value = "Solo el estudiante puede cerrar el ticket"
            else -> _estadoElegido.value = estado
        }
    }

    fun cambiarPrioridad(prioridad: Prioridad) {
        if (ticket.value == null) return
        if (estaCerrado()) {
            _mensaje.value = "No se puede cambiar un ticket cerrado"
            return
        }
        _prioridadElegida.value = prioridad
    }

    fun cambiarEsNotaInterna(esInterno: Boolean) {
        _esNotaInterna.value = esInterno
    }

    fun responder(texto: String) {
        if (ticket.value == null || texto.isBlank() || estaCerrado()) return
        val esInterno = _esNotaInterna.value
        viewModelScope.launch {
            repo.agregarComentario(
                ticketId = ticketId,
                autor = usuario,
                texto = texto.trim(),
                interno = esInterno
            )
            _esNotaInterna.value = false
        }
    }

    fun guardar() {
        val t = ticket.value ?: return
        if (estaCerrado()) {
            _mensaje.value = "No se puede cambiar un ticket cerrado"
            return
        }
        val estado = _estadoElegido.value
        val prioridad = _prioridadElegida.value
        if ((estado == null || estado == t.estado) && (prioridad == null || prioridad == t.prioridad)) {
            _mensaje.value = "No hay cambios por guardar"
            return
        }
        viewModelScope.launch {
            if (estado != null && estado != t.estado) repo.cambiarEstado(ticketId, estado)
            if (prioridad != null && prioridad != t.prioridad) repo.cambiarPrioridad(ticketId, prioridad)
            _estadoElegido.value = null
            _prioridadElegida.value = null
            _mensaje.value = "Cambios guardados"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
