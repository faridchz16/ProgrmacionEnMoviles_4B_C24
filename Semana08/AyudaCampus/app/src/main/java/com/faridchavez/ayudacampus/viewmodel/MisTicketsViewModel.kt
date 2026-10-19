package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MisTicketsViewModel(
    repo: AyudaRepository,
    usuario: Usuario
) : ViewModel() {

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

    private fun coincide(t: TicketConDetalle, texto: String): Boolean {
        val consulta = texto.trim()
        if (consulta.isEmpty()) return true
        val numero = consulta.removePrefix("#").trimStart('0')
        return t.ticket.titulo.contains(consulta, ignoreCase = true) ||
                (numero.isNotEmpty() && t.ticket.id.toString() == numero)
    }
}