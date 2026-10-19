package com.faridchavez.ayudacampus.model

enum class Prioridad(val etiqueta: String, val horasRespuesta: Int) {
    BAJA("Baja", 72),
    MEDIA("Media", 24),
    ALTA("Alta", 4);

    companion object {
        fun desde(valor: String): Prioridad =
            entries.firstOrNull { it.name == valor } ?: MEDIA
    }
}