package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioRecuperar(
    val correo: String = "",
    val nueva: String = "",
    val confirmar: String = "",

    val nombreCuenta: String? = null,
    val errorCorreo: String? = null,
    val errorNueva: String? = null,
    val errorConfirmar: String? = null,

    val listo: Boolean = false
)

class RecuperarClaveViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val _formulario = MutableStateFlow(FormularioRecuperar())
    val formulario: StateFlow<FormularioRecuperar> = _formulario.asStateFlow()

    private var cuenta: Usuario? = null

    fun cambiarCorreo(t: String) = _formulario.update { it.copy(correo = t, errorCorreo = null) }
    fun cambiarNueva(t: String) = _formulario.update { it.copy(nueva = t, errorNueva = null) }
    fun cambiarConfirmar(t: String) = _formulario.update { it.copy(confirmar = t, errorConfirmar = null) }

    fun buscarCuenta() {
        val correo = _formulario.value.correo.trim().lowercase()
        if (correo.isEmpty()) {
            _formulario.update { it.copy(errorCorreo = "Ingresa tu correo") }
            return
        }
        viewModelScope.launch {
            val usuario = repo.buscarUsuarioPorCorreo(correo)
            if (usuario == null) {
                _formulario.update { it.copy(errorCorreo = "No existe una cuenta con este correo") }
            } else {
                cuenta = usuario
                _formulario.update { it.copy(nombreCuenta = usuario.nombre) }
            }
        }
    }

    fun guardarNuevaClave() {
        val usuario = cuenta ?: return
        val f = _formulario.value
        val errorNueva = when {
            f.nueva.length < 4 -> "La contraseña debe tener al menos 4 caracteres"
            f.nueva == usuario.clave -> "La nueva contraseña debe ser distinta a la anterior"
            else -> null
        }
        val errorConfirmar = if (f.confirmar != f.nueva) "Las contraseñas no coinciden" else null
        if (errorNueva != null || errorConfirmar != null) {
            _formulario.value = f.copy(errorNueva = errorNueva, errorConfirmar = errorConfirmar)
            return
        }
        viewModelScope.launch {
            repo.cambiarClave(usuario.id, f.nueva)
            _formulario.update { it.copy(listo = true) }
        }
    }

    fun usarOtroCorreo() {
        cuenta = null
        _formulario.value = FormularioRecuperar(correo = _formulario.value.correo)
    }
}