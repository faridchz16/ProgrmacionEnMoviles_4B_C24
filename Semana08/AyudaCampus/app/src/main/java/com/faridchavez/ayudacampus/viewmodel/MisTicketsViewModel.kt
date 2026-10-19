package com.faridchavez.ayudacampus.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SlaInfo(
    val texto: String,
    val color: Color
)

class MisTicketsViewModel(
    repo: AyudaRepository,
    usuario: Usuario
) : ViewModel() {

    val ahora: StateFlow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(60_000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())

    private val _filtro = MutableStateFlow<EstadoTicket?>(null)
    val filtro: StateFlow<EstadoTicket?> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val misTickets: Flow<List<TicketConDetalle>> = repo.observarTicketsCliente(usuario.id)

    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(misTickets, _filtro, _busqueda) { lista, filtro, texto ->
            lista
                .filter { filtro == null || it.estado == filtro }
                .filter { coincide(it, texto) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val abiertos: StateFlow<Int> = misTickets
        .map { lista ->
            lista.count { it.estado == EstadoTicket.ABIERTO || it.estado == EstadoTicket.EN_PROCESO }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val nombre: String = usuario.nombre.substringBefore(" ")

    fun seleccionarFiltro(estado: EstadoTicket?) {
        _filtro.value = estado
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun calcularSla(
        fecha: Long,
        prioridad: Prioridad,
        estado: EstadoTicket,
        ahoraMs: Long
    ): SlaInfo? = calcularSlaHelper(fecha, prioridad, estado, ahoraMs)

    private fun coincide(t: TicketConDetalle, texto: String): Boolean {
        val consulta = texto.trim()
        if (consulta.isEmpty()) return true
        val numero = consulta.removePrefix("#").trimStart('0')
        return t.ticket.titulo.contains(consulta, ignoreCase = true) ||
                (numero.isNotEmpty() && t.ticket.id.toString() == numero)
    }
}

fun calcularSlaHelper(
    fecha: Long,
    prioridad: Prioridad,
    estado: EstadoTicket,
    ahoraMs: Long
): SlaInfo? {
    if (estado != EstadoTicket.ABIERTO && estado != EstadoTicket.EN_PROCESO) {
        return null
    }

    val duracionSlaMs = when (prioridad) {
        Prioridad.ALTA -> 4 * 60 * 60 * 1000L
        Prioridad.MEDIA -> 24 * 60 * 60 * 1000L
        Prioridad.BAJA -> 72 * 60 * 60 * 1000L
    }

    val transcurridoMs = ahoraMs - fecha
    val restanteMs = duracionSlaMs - transcurridoMs
    val porcentajeRestante = restanteMs.toDouble() / duracionSlaMs.toDouble()

    return if (restanteMs <= 0) {
        SlaInfo(
            texto = "SLA: Vencido",
            color = Color(0xFFDC2626) // Rojo
        )
    } else if (porcentajeRestante < 0.25) {
        val minutosTotales = (restanteMs / 60_000L).coerceAtLeast(1)
        val horas = minutosTotales / 60
        val mins = minutosTotales % 60
        val textoTiempo = if (horas > 0) "${horas}h ${mins}m" else "$minutosTotales min"
        SlaInfo(
            texto = "SLA: $textoTiempo",
            color = Color(0xFFD97706) // Ámbar
        )
    } else {
        val minutosTotales = (restanteMs / 60_000L).coerceAtLeast(1)
        val horas = minutosTotales / 60
        val mins = minutosTotales % 60
        val textoTiempo = if (horas > 0) "${horas}h ${mins}m" else "$minutosTotales min"
        SlaInfo(
            texto = "SLA: $textoTiempo",
            color = Color(0xFF16A34A) // Verde
        )
    }
}
