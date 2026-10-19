package com.faridchavez.ayudacampus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Dorado = Color(0xFFF59E0B)

@Composable
fun Estrellas(
    valor: Int,
    onCambio: ((Int) -> Unit)?,
    modifier: Modifier = Modifier,
    tamano: Dp = 40.dp
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 1..5) {
            val mod = Modifier.size(tamano)
            Icon(
                Icons.Filled.Star,
                contentDescription = "$i estrellas",
                tint = if (i <= valor) Dorado else MaterialTheme.colorScheme.outlineVariant,
                modifier = if (onCambio != null) mod.clickable { onCambio(i) } else mod
            )
        }
    }
}