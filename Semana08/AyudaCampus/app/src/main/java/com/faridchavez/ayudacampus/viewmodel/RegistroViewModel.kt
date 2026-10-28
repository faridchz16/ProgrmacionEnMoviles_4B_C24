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

data class FormularioRegistro(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmar: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorConfirmar: String? = null,
    val enviando: Boolean = false
)

class RegistroViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val _formulario = MutableStateFlow(FormularioRegistro())
    val formulario: StateFlow<FormularioRegistro> = _formulario.asStateFlow()

    private val _registrado = MutableStateFlow<Usuario?>(null)
    val registrado: StateFlow<Usuario?> = _registrado.asStateFlow()

    fun cambiarNombre(t: String) = _formulario.update { it.copy(nombre = t, errorNombre = null) }
    fun cambiarCorreo(t: String) = _formulario.update { it.copy(correo = t, errorCorreo = null) }
    fun cambiarClave(t: String) = _formulario.update { it.copy(clave = t, errorClave = null) }
    fun cambiarConfirmar(t: String) = _formulario.update { it.copy(confirmar = t, errorConfirmar = null) }

    fun registrar() {
        val f = _formulario.value
        if (f.enviando) return
        val nombre = f.nombre.trim()
        val correo = f.correo.trim().lowercase()

        val errorNombre = if (nombre.length < 3) "Ingresa tu nombre completo" else null
        val errorCorreo = if (!CORREO_VALIDO.matches(correo)) "Ingresa un correo válido" else null
        val errorClave = if (f.clave.length < 4) "La contraseña debe tener al menos 4 caracteres" else null
        val errorConfirmar = if (f.confirmar != f.clave) "Las contraseñas no coinciden" else null

        if (errorNombre != null || errorCorreo != null || errorClave != null || errorConfirmar != null) {
            _formulario.value = f.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorClave = errorClave,
                errorConfirmar = errorConfirmar
            )
            return
        }

        _formulario.value = f.copy(enviando = true)
        viewModelScope.launch {
            // Regla de negocio: el correo debe ser único
            if (repo.buscarUsuarioPorCorreo(correo) != null) {
                _formulario.update {
                    it.copy(enviando = false, errorCorreo = "Ya existe una cuenta con este correo")
                }
                return@launch
            }
            _registrado.value = repo.registrarEstudiante(nombre, correo, f.clave)
        }
    }

    fun confirmarRegistro() {
        _registrado.value = null
    }

    private companion object {
        val CORREO_VALIDO = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")
    }
}