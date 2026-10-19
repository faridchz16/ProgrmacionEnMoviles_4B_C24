package com.faridchavez.ayudacampus.ui.soporte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.ui.components.graficos.GraficoBarras
import com.faridchavez.ayudacampus.ui.components.graficos.GraficoDona
import com.faridchavez.ayudacampus.ui.theme.colorEstado
import com.faridchavez.ayudacampus.viewmodel.PanelViewModel

@Composable
fun PanelScreen(vm: PanelViewModel, onVerAlerta: () -> Unit) {
    val abiertos by vm.abiertos.collectAsStateWithLifecycle()
    val enProceso by vm.enProceso.collectAsStateWithLifecycle()
    val resueltos by vm.resueltos.collectAsStateWithLifecycle()
    val porEstado by vm.porEstado.collectAsStateWithLifecycle()
    val porCategoria by vm.porCategoria.collectAsStateWithLifecycle()
    val altas by vm.altasSinAtender.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Panel de soporte", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Mesa de ayuda TI · Hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones")
        }

        // 3 contadores de color
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Contador(abiertos, "Abiertos", colorEstado(EstadoTicket.ABIERTO), Modifier.weight(1f))
            Contador(enProceso, "En proceso", colorEstado(EstadoTicket.EN_PROCESO), Modifier.weight(1f))
            Contador(resueltos, "Resueltos", colorEstado(EstadoTicket.RESUELTO), Modifier.weight(1f))
        }

        // Dona: tickets por estado
        Text("Tickets por estado", style = MaterialTheme.typography.titleMedium)
        Tarjeta {
            GraficoDona(
                datos = porEstado.mapIndexed { i, dato ->
                    val estado = EstadoTicket.entries.getOrNull(i)
                    if (estado != null) dato.copy(color = colorEstado(estado)) else dato
                }
            )
        }

        // Barras: tickets por categoría
        Text("Tickets por categoría", style = MaterialTheme.typography.titleMedium)
        Tarjeta {
            GraficoBarras(
                datos = porCategoria.map { it.copy(etiqueta = it.etiqueta.substringBefore(" /")) }
            )
        }

        if (altas > 0) {
            AlertaAltas(altas, onVerAlerta)
        }
    }
}

@Composable
private fun Contador(valor: Int, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(vertical = 16.dp)
    ) {
        Text("$valor", style = MaterialTheme.typography.headlineMedium, color = color)
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
private fun Tarjeta(contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), content = contenido)
    }
}

@Composable
private fun AlertaAltas(cantidad: Int, onVer: () -> Unit) {
    val rojo = MaterialTheme.colorScheme.error
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = rojo.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = rojo)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (cantidad == 1) "1 ticket de prioridad Alta" else "$cantidad tickets de prioridad Alta",
                    style = MaterialTheme.typography.titleSmall,
                    color = rojo
                )
                Text(
                    "sin atender hace más de 1 hora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onVer,
                colors = ButtonDefaults.buttonColors(
                    containerColor = rojo,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) { Text("Ver") }
        }
    }
    Spacer(Modifier.height(8.dp))
}