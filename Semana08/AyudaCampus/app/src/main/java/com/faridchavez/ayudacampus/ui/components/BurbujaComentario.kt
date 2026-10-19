package com.faridchavez.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.faridchavez.ayudacampus.model.Rol
import com.faridchavez.ayudacampus.ui.theme.Cian
import com.faridchavez.ayudacampus.ui.theme.Violeta

@Composable
fun BurbujaComentario(
    autor: String,
    fecha: Long,
    texto: String,
    autorRol: Rol,
    propio: Boolean,
    modifier: Modifier = Modifier
) {
    val forma = if (propio)
        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
    else
        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)

    val fondo = when {
        propio -> MaterialTheme.colorScheme.primary
        autorRol == Rol.ADMIN -> Violeta.copy(alpha = 0.14f)
        else -> Cian.copy(alpha = 0.12f)
    }
    val colorTexto = if (propio) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (propio) Arrangement.End else Arrangement.Start
    ) {
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .clip(forma)
                .background(fondo)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "${if (propio) "Tú" else autor} · ${formatoHora(fecha)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorTexto.copy(alpha = 0.75f)
                )
                Text(texto, style = MaterialTheme.typography.bodyMedium, color = colorTexto)
            }
        }
    }
}