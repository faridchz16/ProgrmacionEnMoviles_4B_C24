package com.faridchavez.ayudacampus.ui.cliente

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.data.local.relation.TicketConDetalle
import com.faridchavez.ayudacampus.data.local.relation.conversacion
import com.faridchavez.ayudacampus.data.local.relation.estado
import com.faridchavez.ayudacampus.data.local.relation.prioridad
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Rol
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.AyudaTextField
import com.faridchavez.ayudacampus.ui.components.BurbujaComentario
import com.faridchavez.ayudacampus.ui.components.CampoComentario
import com.faridchavez.ayudacampus.ui.components.EstadoChip
import com.faridchavez.ayudacampus.ui.components.EstiloBoton
import com.faridchavez.ayudacampus.ui.components.Estrellas
import com.faridchavez.ayudacampus.ui.components.PrioridadChip
import com.faridchavez.ayudacampus.ui.components.StepperTicket
import com.faridchavez.ayudacampus.ui.components.formatoFecha
import com.faridchavez.ayudacampus.ui.components.iconoCategoria
import com.faridchavez.ayudacampus.ui.components.numeroTicket
import com.faridchavez.ayudacampus.ui.components.tiempoRelativo
import com.faridchavez.ayudacampus.ui.theme.EstadoResuelto
import com.faridchavez.ayudacampus.viewmodel.TicketDetalleViewModel

@Composable
fun DetalleTicketScreen(vm: TicketDetalleViewModel, onAtras: () -> Unit) {
    val ticket by vm.ticket.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()

    var texto by rememberSaveable { mutableStateOf("") }
    var calificacion by rememberSaveable { mutableIntStateOf(0) }
    var comentarioFinal by rememberSaveable { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    val t = ticket
    val estado = t?.estado

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = if (t != null) "Ticket ${numeroTicket(t.ticket.id)}" else "Ticket",
                onAtras = onAtras
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            // Se puede comentar mientras el ticket no esté CERRADO
            // (si está RESUELTO, se usa el "comentario final" al calificar)
            if (estado == EstadoTicket.ABIERTO || estado == EstadoTicket.EN_PROCESO) {
                CampoComentario(
                    texto = texto,
                    onCambio = { texto = it },
                    onEnviar = {
                        vm.comentar(texto)
                        texto = ""
                    },
                    placeholder = "Escribe un comentario..."
                )
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
            item { CabeceraTicket(t) }
            item { StepperTicket(t.estado, Modifier.padding(vertical = 4.dp)) }

            if (t.estado == EstadoTicket.RESUELTO) {
                item { AvisoResuelto() }
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
                    propio = c.autorId == vm.usuarioId
                )
            }

            when (t.estado) {
                EstadoTicket.RESUELTO -> {
                    item {
                        TarjetaCalificacion(calificacion, onCambio = { calificacion = it })
                    }
                    item {
                        AyudaTextField(
                            valor = comentarioFinal,
                            onCambio = { comentarioFinal = it },
                            etiqueta = "Comentario final (opcional)"
                        )
                    }
                    item {
                        AyudaButton(
                            "Confirmar y cerrar",
                            onClick = { vm.cerrar(calificacion, comentarioFinal) }
                        )
                    }
                    item {
                        AyudaButton(
                            "Reabrir ticket",
                            onClick = {
                                vm.reabrir()
                                calificacion = 0
                            },
                            estilo = EstiloBoton.PELIGRO
                        )
                    }
                }

                EstadoTicket.CERRADO -> item {
                    CalificacionRecibida(
                        valor = t.ticket.calificacion ?: 0,
                        fechaCierre = t.ticket.fechaCierre
                    )
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun CabeceraTicket(t: TicketConDetalle) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(t.ticket.titulo, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EstadoChip(t.estado)
                PrioridadChip(t.prioridad)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    iconoCategoria(t.categoria?.icono), null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    listOfNotNull(
                        t.categoria?.nombre,
                        t.ticket.ubicacion.ifBlank { null },
                        tiempoRelativo(t.ticket.fecha)
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(t.ticket.descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AvisoResuelto() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EstadoResuelto.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Outlined.CheckCircle, null, tint = EstadoResuelto)
            Spacer(Modifier.width(12.dp))
            Text(
                "Soporte marcó este ticket como resuelto. ¿El problema se solucionó?",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TarjetaCalificacion(valor: Int, onCambio: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Califica la atención", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Estrellas(valor = valor, onCambio = onCambio)
            Spacer(Modifier.height(6.dp))
            Text(
                if (valor == 0) "Toca una estrella" else "$valor de 5 estrellas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalificacionRecibida(valor: Int, fechaCierre: Long?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Calificación recibida", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Estrellas(valor = valor, onCambio = null, tamano = 30.dp)
            if (fechaCierre != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Ticket cerrado el ${formatoFecha(fechaCierre)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}