package com.faridchavez.ayudacampus.data.repository

import com.faridchavez.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

interface AyudaRepository {

    // Categorías (ejemplo resuelto)
    fun observarCategorias(): Flow<List<Categoria>>
    fun observarCategoriasActivas(): Flow<List<Categoria>>
    suspend fun buscarCategoria(id: Int): Categoria?
    suspend fun guardarCategoria(c: Categoria)
    suspend fun eliminarCategoria(c: Categoria)
    suspend fun cambiarActiva(id: Int, activa: Boolean)
}