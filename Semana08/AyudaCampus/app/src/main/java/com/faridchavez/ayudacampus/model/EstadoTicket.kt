package com.faridchavez.ayudacampus.model

enum class EstadoTicket(val etiqueta: String) {
    ABIERTO("Abierto"),
    EN_PROCESO("En proceso"),
    RESUELTO("Resuelto"),
    CERRADO("Cerrado");

    companion object {
        fun desde(valor: String): EstadoTicket =
            entries.firstOrNull { it.name == valor } ?: ABIERTO
    }
}