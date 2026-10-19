package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.prefs.PreferenciasStore
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Rol
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ResumenTickets(
    val abiertos: Int = 0,
    val resueltos: Int = 0,
    val total: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // ---------- Preferencias (DataStore) ----------

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val notificaciones: StateFlow<Boolean> = prefs.notificaciones
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val recibirTickets: StateFlow<Boolean> = prefs.recibirTickets
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val resumen: StateFlow<ResumenTickets> = _usuarioActual
        .flatMapLatest { u ->
            if (u == null || u.rol != Rol.CLIENTE.name) {
                flowOf(ResumenTickets())
            } else {
                repo.observarTicketsCliente(u.id).map { lista ->
                    ResumenTickets(
                        abiertos = lista.count {
                            it.estado == EstadoTicket.ABIERTO || it.estado == EstadoTicket.EN_PROCESO
                        },
                        resueltos = lista.count {
                            it.estado == EstadoTicket.RESUELTO || it.estado == EstadoTicket.CERRADO
                        },
                        total = lista.size
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenTickets())

    init {
        // Splash: si ya hay un perfil guardado, se recupera el usuario y se salta el login
        viewModelScope.launch {
            val rol = Rol.desde(prefs.perfilActivo.first())
            val id = prefs.usuarioId.first()
            if (rol != null && id > 0) {
                val usuario = repo.buscarUsuario(id)
                _usuarioActual.value = usuario?.takeIf { it.rol == rol.name }
            }
            _cargando.value = false
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        val correoLimpio = correo.trim()
        if (correoLimpio.isEmpty() || clave.isEmpty()) {
            _error.value = "Ingresa tu correo y tu contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correoLimpio, clave)
            when {
                usuario == null ->
                    _error.value = "Correo o contraseña incorrectos"

                usuario.rol != rolSeleccionado.name ->
                    _error.value = "Esta cuenta no es de ${rolSeleccionado.etiqueta}. " +
                            "Elige la otra pestaña."

                else -> {
                    prefs.guardarSesion(rolSeleccionado, usuario.id)
                    _error.value = null
                    _usuarioActual.value = usuario // NavAyuda navega al grafo del rol
                }
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun cambiarPerfil() {
        val actual = Rol.desde(_usuarioActual.value?.rol) ?: return
        viewModelScope.launch {
            val otroRol = actual.otro()
            val otro = repo.primerUsuarioConRol(otroRol) ?: return@launch
            prefs.guardarSesion(otroRol, otro.id)
            _usuarioActual.value = otro
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
        }
    }

    fun cambiarModoOscuro(valor: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(valor) }
    }

    fun cambiarNotificaciones(valor: Boolean) {
        viewModelScope.launch { prefs.guardarNotificaciones(valor) }
    }

    fun cambiarRecibirTickets(valor: Boolean) {
        viewModelScope.launch { prefs.guardarRecibirTickets(valor) }
    }
}