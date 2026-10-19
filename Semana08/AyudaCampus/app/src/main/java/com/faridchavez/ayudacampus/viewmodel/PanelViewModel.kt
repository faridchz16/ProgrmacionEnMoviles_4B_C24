package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.ui.components.graficos.DatoGrafico
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class PanelViewModel(private val repo: AyudaRepository) : ViewModel() {

    private fun contar(estado: EstadoTicket): Flow<Int> = repo.observarCantidadPorEstado(estado)

    private fun <T> Flow<T>.estado(inicial: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), inicial)

    val abiertos: StateFlow<Int> = contar(EstadoTicket.ABIERTO).estado(0)
    val enProceso: StateFlow<Int> = contar(EstadoTicket.EN_PROCESO).estado(0)
    val resueltos: StateFlow<Int> = contar(EstadoTicket.RESUELTO).estado(0)

    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        contar(EstadoTicket.ABIERTO),
        contar(EstadoTicket.EN_PROCESO),
        contar(EstadoTicket.RESUELTO),
        contar(EstadoTicket.CERRADO)
    ) { a, p, r, c ->
        listOf(
            DatoGrafico(EstadoTicket.ABIERTO.etiqueta, a),
            DatoGrafico(EstadoTicket.EN_PROCESO.etiqueta, p),
            DatoGrafico(EstadoTicket.RESUELTO.etiqueta, r),
            DatoGrafico(EstadoTicket.CERRADO.etiqueta, c)
        )
    }.estado(emptyList())

    val porCategoria: StateFlow<List<DatoGrafico>> = repo.observarCantidadPorCategoria()
        .map { lista -> lista.map { DatoGrafico(it.nombre, it.cantidad) } }
        .estado(emptyList())

    private val limite: Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis() - UNA_HORA)
            delay(60_000)
        }
    }

    val altasSinAtender: StateFlow<Int> = limite
        .flatMapLatest { repo.observarAltasSinAtender(it) }
        .estado(0)

    val tiempoPromedioResolucion: StateFlow<String> = repo.observarTiempoPromedioResolucion()
        .map { ms -> formatearTiempoPromedio(ms) }
        .estado("Sin datos")

    private fun formatearTiempoPromedio(ms: Double?): String {
        if (ms == null || ms <= 0) return "Sin datos"
        val totalMinutos = (ms / 60_000).toLong()
        val horas = totalMinutos / 60
        val minutos = totalMinutos % 60
        val dias = horas / 24
        val horasRestantes = horas % 24

        return when {
            dias > 0 -> "${dias}d ${horasRestantes}h"
            horas > 0 -> "${horas}h ${minutos}m"
            totalMinutos > 0 -> "${minutos} min"
            else -> "< 1 min"
        }
    }

    private companion object {
        const val UNA_HORA = 60 * 60 * 1000L
    }
}