package com.faridchavez.ayudacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    etiquetaCentro: String = "tickets",
    tamano: Dp = 120.dp,
    grosor: Dp = 18.dp
) {
    val total = datos.sumOf { it.valor }
    val primario = MaterialTheme.colorScheme.primary
    val pista = MaterialTheme.colorScheme.surfaceVariant

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(tamano), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val trazo = grosor.toPx()
                val diametro = size.minDimension - trazo
                val esquina = Offset((size.width - diametro) / 2f, (size.height - diametro) / 2f)
                val area = Size(diametro, diametro)
                // pista de fondo
                drawArc(pista, 0f, 360f, false, esquina, area, style = Stroke(trazo))
                if (total > 0) {
                    var inicio = -90f
                    datos.forEach { d ->
                        val barrido = 360f * d.valor / total
                        val color = if (d.color == Color.Unspecified) primario else d.color
                        drawArc(color, inicio, barrido, false, esquina, area, style = Stroke(trazo))
                        inicio += barrido
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$total", style = MaterialTheme.typography.headlineSmall)
                Text(
                    etiquetaCentro,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.width(20.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            datos.forEach { d ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(12.dp)
                            .background(
                                if (d.color == Color.Unspecified) primario else d.color,
                                RoundedCornerShape(3.dp)
                            )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(d.etiqueta, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text("${d.valor}", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}