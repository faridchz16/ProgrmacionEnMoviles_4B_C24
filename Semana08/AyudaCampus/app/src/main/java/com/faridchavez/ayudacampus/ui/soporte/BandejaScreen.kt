package com.faridchavez.ayudacampus.ui.soporte

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.FilterListOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.local.relation.prioridad
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.EstadoVacio
import com.faridchavez.ayudacampus.ui.components.TicketCard
import com.faridchavez.ayudacampus.ui.components.numeroTicket
import com.faridchavez.ayudacampus.viewmodel.BandejaViewModel
import com.faridchavez.ayudacampus.viewmodel.FiltrosBandeja

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(vm: BandejaViewModel, onAtender: (Int) -> Unit) {
    val tickets by vm.tickets.collectAsStateWithLifecycle()
    val filtros by vm.filtros.collectAsStateWithLifecycle()
    val categorias by vm.categorias.collectAsStateWithLifecycle()
    val ahora by vm.ahora.collectAsStateWithLifecycle()

    val hayFiltros = filtros != FiltrosBandeja()

    Scaffold(
        topBar = { AppTopBar("Tickets de soporte") },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Buscador por título, cliente o #ticket
            OutlinedTextField(
                value = filtros.texto,
                onValueChange = vm::buscarTexto,
                placeholder = { Text("Buscar título, cliente o #ticket...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (filtros.texto.isNotEmpty()) {
                        IconButton(onClick = { vm.buscarTexto("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar texto")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Encabezado de Filtros y Botón Limpiar filtros
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Filtros",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (hayFiltros) {
                    TextButton(onClick = vm::limpiarFiltros) {
                        Icon(
                            Icons.Outlined.FilterListOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.padding(horizontal = 2.dp))
                        Text("Limpiar filtros")
                    }
                }
            }

            // Chips para Estado
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = filtros.estado == null,
                    onClick = { vm.seleccionarEstado(null) },
                    label = { Text("Todos los estados") },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                EstadoTicket.entries.forEach { estado ->
                    FilterChip(
                        selected = filtros.estado == estado,
                        onClick = {
                            vm.seleccionarEstado(if (filtros.estado == estado) null else estado)
                        },
                        label = { Text(estado.etiqueta) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Chips para Prioridad y Categoría
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                Prioridad.entries.forEach { prio ->
                    FilterChip(
                        selected = filtros.prioridad == prio,
                        onClick = {
                            vm.seleccionarPrioridad(if (filtros.prioridad == prio) null else prio)
                        },
                        label = { Text("P: ${prio.etiqueta}") },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                categorias.forEach { cat ->
                    FilterChip(
                        selected = filtros.categoriaId == cat.id,
                        onClick = {
                            vm.seleccionarCategoria(if (filtros.categoriaId == cat.id) null else cat.id)
                        },
                        label = { Text(cat.nombre) },
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
                EstadoVacio("No hay tickets con los filtros seleccionados")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
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
