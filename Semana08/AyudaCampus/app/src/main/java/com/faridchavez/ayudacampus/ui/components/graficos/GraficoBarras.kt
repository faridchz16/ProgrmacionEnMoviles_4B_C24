package com.faridchavez.ayudacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GraficoBarras(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    alto: Dp = 140.dp
) {
    val primario = MaterialTheme.colorScheme.primary
    val colorTexto = MaterialTheme.colorScheme.onSurface
    val maximo = (datos.maxOfOrNull { it.valor } ?: 0).coerceAtLeast(1)

    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(alto)
        ) {
            if (datos.isEmpty()) return@Canvas
            val ancho = size.width / datos.size
            val barra = ancho * 0.6f
            val espacioTexto = 18.dp.toPx()
            val altoUtil = size.height - espacioTexto
            val pintura = android.graphics.Paint().apply {
                color = colorTexto.toArgb()
                textSize = 11.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            datos.forEachIndexed { i, d ->
                val base = if (d.color == Color.Unspecified) primario else d.color
                val color = if (d.valor == maximo) base else base.copy(alpha = 0.35f)
                val h = altoUtil * d.valor / maximo
                val x = i * ancho + (ancho - barra) / 2f
                val y = size.height - h
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barra, h),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawIntoCanvas {
                    it.nativeCanvas.drawText("${d.valor}", x + barra / 2f, y - 6.dp.toPx(), pintura)
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            datos.forEach { d ->
                Text(
                    d.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}