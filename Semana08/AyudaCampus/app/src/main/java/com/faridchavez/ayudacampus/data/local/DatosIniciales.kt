package com.faridchavez.ayudacampus.data.local

import com.faridchavez.ayudacampus.data.local.entity.Categoria

object DatosIniciales {

    suspend fun sembrar(db: AyudaDatabase) {
        if (db.categoriaDao().contar() > 0) return
        db.categoriaDao().insertarTodas(categorias())
    }

    fun categorias() = listOf(
        Categoria(nombre = "Hardware", icono = "pc"),
        Categoria(nombre = "Software", icono = "code"),
        Categoria(nombre = "Red / Wi-Fi", icono = "wifi"),
        Categoria(nombre = "Cuenta", icono = "key"),
        Categoria(nombre = "Impresión", icono = "printer")
    )
}