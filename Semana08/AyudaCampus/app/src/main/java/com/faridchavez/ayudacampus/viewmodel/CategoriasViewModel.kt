package com.faridchavez.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faridchavez.ayudacampus.data.local.entity.Categoria
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriasViewModel(private val repo: AyudaRepository) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun alternar(c: Categoria) {
        viewModelScope.launch { repo.cambiarActiva(c.id, !c.activa) }
    }

    // ---------- Formulario (crear / editar) ----------

    private val _editando = MutableStateFlow<Categoria?>(null)
    val editando: StateFlow<Categoria?> = _editando.asStateFlow()

    private val _ticketsDeCategoria = MutableStateFlow(0)
    val ticketsDeCategoria: StateFlow<Int> = _ticketsDeCategoria.asStateFlow()

    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _listo = MutableStateFlow(false)
    val listo: StateFlow<Boolean> = _listo.asStateFlow()

    fun cargar(id: Int) {
        if (id <= 0) return
        viewModelScope.launch {
            _editando.value = repo.buscarCategoria(id)
            _ticketsDeCategoria.value = repo.contarTicketsDeCategoria(id)
        }
    }

    fun guardar(categoria: Categoria) {
        val nombre = categoria.nombre.trim()
        if (nombre.isEmpty()) {
            _errorNombre.value = "El nombre es obligatorio"
            return
        }
        viewModelScope.launch {
            repo.guardarCategoria(categoria.copy(nombre = nombre))
            _listo.value = true
        }
    }

    fun eliminar(categoria: Categoria) {
        viewModelScope.launch {
            if (repo.contarTicketsDeCategoria(categoria.id) > 0) {
                _mensaje.value = "No se puede eliminar \"${categoria.nombre}\" porque ya tiene " +
                        "tickets. Puedes desactivarla."
                return@launch
            }
            repo.eliminarCategoria(categoria)
            _listo.value = true
        }
    }

    fun limpiarErrorNombre() {
        _errorNombre.value = null
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun confirmarListo() {
        _listo.value = false
    }
}