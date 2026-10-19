package com.faridchavez.ayudacampus.ui.soporte

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.local.relation.prioridad
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.EstadoVacio
import com.faridchavez.ayudacampus.ui.components.TicketCard
import com.faridchavez.ayudacampus.ui.components.numeroTicket
import com.faridchavez.ayudacampus.viewmodel.BandejaViewModel

private val FILTROS_BANDEJA = listOf(
    null to "Todos",
    EstadoTicket.ABIERTO to "Abiertos",
    EstadoTicket.EN_PROCESO to "En proceso",
    EstadoTicket.RESUELTO to "Resueltos"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(vm: BandejaViewModel, onAtender: (Int) -> Unit) {
    val tickets by vm.tickets.collectAsStateWithLifecycle()
    val filtro by vm.filtro.collectAsStateWithLifecycle()
    val ahora by vm.ahora.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Tickets") },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FILTROS_BANDEJA.forEach { (estado, texto) ->
                    FilterChip(
                        selected = filtro == estado,
                        onClick = { vm.seleccionarFiltro(estado) },
                        label = { Text(texto) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            if (tickets.isEmpty()) {
                EstadoVacio("No hay tickets con este filtro")
            } else {
                // El orden (ALTA → MEDIA → BAJA, luego más reciente) viene del DAO
                LazyColumn(
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tickets, key = { it.ticket.id }) { t ->
                        TicketCard(
                            titulo = t.ticket.titulo,
                            detalle = "${numeroTicket(t.ticket.id)} · ${t.cliente?.nombre ?: "Sin cliente"}",
                            icono = t.categoria?.icono,
                            estado = t.estado,
                            prioridad = t.prioridad,
                            fecha = t.ticket.fecha,
                            slaInfo = vm.calcularSla(t.ticket.fecha, t.prioridad, t.estado, ahora),
                            onClick = { onAtender(t.ticket.id) }
                        )
                    }
                }
            }
        }
    }
}
