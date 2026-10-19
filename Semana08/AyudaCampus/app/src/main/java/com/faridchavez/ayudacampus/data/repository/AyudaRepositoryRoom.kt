package com.faridchavez.ayudacampus.data.repository

import com.faridchavez.ayudacampus.data.local.AyudaDatabase
import com.faridchavez.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

class AyudaRepositoryRoom(private val db: AyudaDatabase) : AyudaRepository {

    private val categoriaDao = db.categoriaDao()

    // ---------- Categorías ----------
    override fun observarCategorias(): Flow<List<Categoria>> = categoriaDao.observarTodas()

    override fun observarCategoriasActivas(): Flow<List<Categoria>> = categoriaDao.observarActivas()

    override suspend fun buscarCategoria(id: Int): Categoria? = categoriaDao.buscar(id)

    override suspend fun guardarCategoria(c: Categoria) {
        // id = 0 → nueva; id > 0 → edición
        if (c.id == 0) categoriaDao.insertar(c) else categoriaDao.actualizar(c)
    }

    override suspend fun eliminarCategoria(c: Categoria) = categoriaDao.eliminar(c)

    override suspend fun cambiarActiva(id: Int, activa: Boolean) =
        categoriaDao.cambiarActiva(id, activa)
}