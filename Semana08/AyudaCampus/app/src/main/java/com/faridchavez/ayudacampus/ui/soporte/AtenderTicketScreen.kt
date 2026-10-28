package com.faridchavez.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.data.local.relation.conversacion
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad
import com.faridchavez.ayudacampus.model.Rol
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.BurbujaComentario
import com.faridchavez.ayudacampus.ui.components.CampoComentario
import com.faridchavez.ayudacampus.ui.components.Estrellas
import com.faridchavez.ayudacampus.ui.components.SelectorOpciones
import com.faridchavez.ayudacampus.ui.components.numeroTicket
import com.faridchavez.ayudacampus.ui.components.tiempoRelativo
import com.faridchavez.ayudacampus.ui.theme.colorEstado
import com.faridchavez.ayudacampus.ui.theme.colorPrioridad
import com.faridchavez.ayudacampus.viewmodel.AtenderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtenderTicketScreen(vm: AtenderViewModel, onAtras: () -> Unit) {
    val ticket by vm.ticket.collectAsStateWithLifecycle()
    val estadoSel by vm.estadoSeleccionado.collectAsStateWithLifecycle()
    val prioridadSel by vm.prioridadSeleccionada.collectAsStateWithLifecycle()
    val esNotaInterna by vm.esNotaInterna.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()

    var respuesta by rememberSaveable { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    val t = ticket
    val cerrado = t?.estado == EstadoTicket.CERRADO

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = if (t != null) "Atender ticket ${numeroTicket(t.ticket.id)}" else "Atender ticket",
                onAtras = onAtras
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (t != null && !cerrado) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = esNotaInterna,
                            onClick = { vm.cambiarEsNotaInterna(!esNotaInterna) },
                            label = { Text("🔒 Nota interna") },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                selectedContainerColor = Color(0xFFFEF3C7),
                                selectedLabelColor = Color(0xFF92400E)
                            )
                        )
                    }
                    CampoComentario(
                        texto = respuesta,
                        onCambio = { respuesta = it },
                        onEnviar = {
                            vm.responder(respuesta)
                            respuesta = ""
                        },
                        placeholder = if (esNotaInterna) "Escribir nota interna (solo visible por Soporte)..." else "Responder al cliente..."
                    )
                    Spacer(Modifier.height(8.dp))
                    AyudaButton(
                        "Guardar cambios",
                        onClick = vm::guardar,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        if (t == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { DatosTicket(t) }

            item {
                Column {
                    Text("Estado", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    // CERRADO aparece deshabilitado: Soporte no puede cerrar
                    SelectorOpciones(
                        opciones = EstadoTicket.entries,
                        seleccionada = estadoSel,
                        etiqueta = { it.etiqueta },
                        onSeleccion = vm::cambiarEstado,
                        color = { colorEstado(it) },
                        habilitada = vm::puedeElegir
                    )
                }
            }

            item {
                Column {
                    Text("Prioridad", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    SelectorOpciones(
                        opciones = Prioridad.entries,
                        seleccionada = prioridadSel,
                        etiqueta = { it.etiqueta },
                        onSeleccion = vm::cambiarPrioridad,
                        color = { colorPrioridad(it) },
                        habilitada = { !cerrado }
                    )
                }
            }

            if (cerrado) {
                item { TicketCerrado(t.ticket.calificacion ?: 0) }
            }

            item { Text("Conversación", style = MaterialTheme.typography.titleMedium) }
            if (t.conversacion.isEmpty()) {
                item {
                    Text(
                        "Aún no hay comentarios.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(t.conversacion, key = { it.id }) { c ->
                BurbujaComentario(
                    autor = c.autorNombre,
                    fecha = c.fecha,
                    texto = c.texto,
                    autorRol = Rol.desde(c.autorRol) ?: Rol.CLIENTE,
                    propio = c.autorId == vm.usuarioId,
                    esNotaInterna = c.interno
                )
            }
        }
    }
}

@Composable
private fun DatosTicket(t: TicketConDetalle) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(t.ticket.titulo, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                listOfNotNull(
                    t.cliente?.nombre,
                    t.ticket.ubicacion.ifBlank { null },
                    t.categoria?.nombre,
                    tiempoRelativo(t.ticket.fecha)
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            t.cliente?.let {
                Text(
                    it.correo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(t.ticket.descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TicketCerrado(calificacion: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text("Ticket cerrado por el estudiante", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Ya no se puede cambiar ni comentar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Estrellas(valor = calificacion, onCambio = null, tamano = 18.dp)
        }
    }
}
