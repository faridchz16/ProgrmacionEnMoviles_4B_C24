package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.prefs.PreferenciasStore
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

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
}