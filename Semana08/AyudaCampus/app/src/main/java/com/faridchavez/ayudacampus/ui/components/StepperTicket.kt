package com.faridchavez.ayudacampus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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

    val transition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            pasos.forEachIndexed { i, _ ->
                val completado = i < actual || (i == actual && estado == EstadoTicket.CERRADO)
                val esActual = i == actual && estado != EstadoTicket.CERRADO

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(28.dp)) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = completado,
                        enter = scaleIn(spring(stiffness = Spring.StiffnessLow)) + fadeIn()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .background(EstadoResuelto, CircleShape)
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (!completado && esActual) {
                        // Halo con animación de pulso
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .scale(pulseScale)
                                .background(primario.copy(alpha = 0.25f), CircleShape)
                        )
                        // Círculo interno
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(2.dp, primario, CircleShape)
                                .padding(4.dp)
                                .background(primario, CircleShape)
                        )
                    } else if (!completado) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .border(2.dp, apagado, CircleShape)
                        )
                    }
                }

                if (i < pasos.lastIndex) {
                    val lineProgress by animateFloatAsState(
                        targetValue = if (i < actual) 1f else 0f,
                        animationSpec = tween(500, delayMillis = i * 100),
                        label = "lineProgress"
                    )

                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(apagado.copy(alpha = 0.3f))
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(lineProgress)
                                .background(if (i < actual) EstadoResuelto else primario)
                        )
                    }
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
