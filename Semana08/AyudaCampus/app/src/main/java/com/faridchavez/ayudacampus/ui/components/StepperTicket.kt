package com.faridchavez.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.ui.theme.EstadoResuelto

@Composable
fun StepperTicket(estado: EstadoTicket, modifier: Modifier = Modifier) {
    val pasos = EstadoTicket.entries
    val actual = estado.ordinal
    val primario = MaterialTheme.colorScheme.primary
    val apagado = MaterialTheme.colorScheme.outline

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            pasos.forEachIndexed { i, _ ->
                when {
                    i < actual || (i == actual && estado == EstadoTicket.CERRADO) -> Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(24.dp).background(EstadoResuelto, CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Check, contentDescription = null,
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    i == actual -> Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(3.dp, primario.copy(alpha = 0.3f), CircleShape)
                            .padding(4.dp)
                            .background(primario, CircleShape)
                    )
                    else -> Box(
                        Modifier
                            .size(22.dp)
                            .border(2.dp, apagado, CircleShape)
                    )
                }
                if (i < pasos.lastIndex) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(if (i < actual) primario else apagado.copy(alpha = 0.5f))
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            pasos.forEachIndexed { i, paso ->
                Text(
                    paso.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (i == actual) FontWeight.Bold else FontWeight.Normal,
                    color = if (i <= actual) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = when (i) {
                        0 -> TextAlign.Start
                        pasos.lastIndex -> TextAlign.End
                        else -> TextAlign.Center
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}