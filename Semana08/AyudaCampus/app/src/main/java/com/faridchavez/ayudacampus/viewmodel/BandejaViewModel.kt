package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Categoria
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.local.relation.prioridad
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

data class FiltrosBandeja(
    val estado: EstadoTicket? = null,
    val prioridad: Prioridad? = null,
    val categoriaId: Int? = null,
    val texto: String = ""
)

class BandejaViewModel(repo: AyudaRepository) : ViewModel() {

    val ahora: StateFlow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(60_000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())

    private val _filtros = MutableStateFlow(FiltrosBandeja())
    val filtros: StateFlow<FiltrosBandeja> = _filtros.asStateFlow()

    val categorias: StateFlow<List<Categoria>> = repo.observarCategoriasActivas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tickets: StateFlow<List<TicketConDetalle>> = combine(
        repo.observarTodosLosTickets(),
        _filtros
    ) { lista, f ->
        lista.filter { t ->
            (f.estado == null || t.estado == f.estado) &&
                    (f.prioridad == null || t.prioridad == f.prioridad) &&
                    (f.categoriaId == null || t.ticket.categoriaId == f.categoriaId) &&
                    coincideTexto(t, f.texto)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun seleccionarEstado(estado: EstadoTicket?) {
        _filtros.value = _filtros.value.copy(estado = estado)
    }

    fun seleccionarPrioridad(prioridad: Prioridad?) {
        _filtros.value = _filtros.value.copy(prioridad = prioridad)
    }

    fun seleccionarCategoria(categoriaId: Int?) {
        _filtros.value = _filtros.value.copy(categoriaId = categoriaId)
    }

    fun buscarTexto(texto: String) {
        _filtros.value = _filtros.value.copy(texto = texto)
    }

    fun limpiarFiltros() {
        _filtros.value = FiltrosBandeja()
    }

    fun calcularSla(
        fecha: Long,
        prioridad: Prioridad,
        estado: EstadoTicket,
        ahoraMs: Long
    ): SlaInfo? = calcularSlaHelper(fecha, prioridad, estado, ahoraMs)

    private fun coincideTexto(t: TicketConDetalle, texto: String): Boolean {
        val consulta = texto.trim()
        if (consulta.isEmpty()) return true
        val numero = consulta.removePrefix("#").trimStart('0')
        val coincideTitulo = t.ticket.titulo.contains(consulta, ignoreCase = true)
        val coincideCliente = t.cliente?.nombre?.contains(consulta, ignoreCase = true) == true
        val coincideNumero = numero.isNotEmpty() && t.ticket.id.toString() == numero
        return coincideTitulo || coincideCliente || coincideNumero
    }
}
