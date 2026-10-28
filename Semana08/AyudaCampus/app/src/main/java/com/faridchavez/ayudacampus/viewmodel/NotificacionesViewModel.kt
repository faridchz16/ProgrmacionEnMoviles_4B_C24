package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Notificacion
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificacionesViewModel(
    private val repo: AyudaRepository,
    private val usuario: Usuario
) : ViewModel() {

    val lista: StateFlow<List<Notificacion>> = repo.observarNotificaciones(usuario)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val noLeidas: StateFlow<Int> = repo.observarNotificaciones(usuario)
        .map { lista -> lista.count { !it.leida } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun abrir(n: Notificacion) {
        if (!n.leida) viewModelScope.launch { repo.marcarNotificacionLeida(n.id) }
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch { repo.marcarTodasLeidas(usuario) }
    }
}