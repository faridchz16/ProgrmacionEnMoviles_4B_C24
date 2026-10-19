package com.faridchavez.ayudacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class EstiloBoton { RELLENO, CONTORNO, PELIGRO }

@Composable
fun AyudaButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    estilo: EstiloBoton = EstiloBoton.RELLENO,
    habilitado: Boolean = true
) {
    val forma = RoundedCornerShape(14.dp)
    val mod = modifier.fillMaxWidth().height(52.dp)
    when (estilo) {
        EstiloBoton.RELLENO -> Button(
            onClick = onClick,
            enabled = habilitado,
            shape = forma,
            modifier = mod
        ) { Text(texto, style = MaterialTheme.typography.labelLarge) }

        EstiloBoton.CONTORNO -> OutlinedButton(
            onClick = onClick,
            enabled = habilitado,
            shape = forma,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            modifier = mod
        ) { Text(texto, style = MaterialTheme.typography.labelLarge) }

        EstiloBoton.PELIGRO -> OutlinedButton(
            onClick = onClick,
            enabled = habilitado,
            shape = forma,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            modifier = mod
        ) { Text(texto, style = MaterialTheme.typography.labelLarge) }
    }
}