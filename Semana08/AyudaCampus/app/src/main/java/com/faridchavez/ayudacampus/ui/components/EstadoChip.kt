package com.faridchavez.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.ui.theme.colorEstado

@Composable
fun EtiquetaColor(texto: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun EstadoChip(estado: EstadoTicket, modifier: Modifier = Modifier) =
    EtiquetaColor(estado.etiqueta, colorEstado(estado), modifier)