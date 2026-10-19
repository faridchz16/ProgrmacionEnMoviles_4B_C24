package com.faridchavez.ayudacampus.ui.components.graficos

import androidx.compose.ui.graphics.Color

data class DatoGrafico(
    val etiqueta: String,
    val valor: Int,
    val color: Color = Color.Unspecified
)