package com.faridchavez.ayudacampus.model

enum class Rol(val etiqueta: String) {
    CLIENTE("Estudiante"),
    ADMIN("Soporte TI");

    fun otro(): Rol = if (this == CLIENTE) ADMIN else CLIENTE

    companion object {
        fun desde(valor: String?): Rol? = entries.firstOrNull { it.name == valor }
    }
}