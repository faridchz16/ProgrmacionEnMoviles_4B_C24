package com.faridchavez.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.model.Prioridad
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.AyudaTextField
import com.faridchavez.ayudacampus.ui.components.SelectorOpciones
import com.faridchavez.ayudacampus.ui.components.iconoCategoria
import com.faridchavez.ayudacampus.ui.theme.colorPrioridad
import com.faridchavez.ayudacampus.viewmodel.NuevoTicketViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NuevoTicketScreen(
    vm: NuevoTicketViewModel,
    onAtras: () -> Unit,
    onEnviado: () -> Unit
) {
    val categorias by vm.categorias.collectAsStateWithLifecycle()
    val f by vm.formulario.collectAsStateWithLifecycle()
    val enviado by vm.enviado.collectAsStateWithLifecycle()

    LaunchedEffect(enviado) {
        if (enviado) {
            vm.confirmarEnvio()
            onEnviado()
        }
    }

    Scaffold(
        topBar = { AppTopBar("Nuevo ticket", onAtras = onAtras) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Categoría (solo activas)
            Text("Categoría", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.forEach { c ->
                    FilterChip(
                        selected = f.categoriaId == c.id,
                        onClick = { vm.elegirCategoria(c.id) },
                        label = { Text(c.nombre) },
                        leadingIcon = {
                            Icon(iconoCategoria(c.icono), null, Modifier.size(18.dp))
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            f.errorCategoria?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(12.dp))

            AyudaTextField(
                valor = f.titulo,
                onCambio = vm::cambiarTitulo,
                etiqueta = "Título",
                error = f.errorTitulo
            )
            Spacer(Modifier.height(4.dp))
            AyudaTextField(
                valor = f.ubicacion,
                onCambio = vm::cambiarUbicacion,
                etiqueta = "Ubicación",
                icono = Icons.Outlined.Place,
                placeholder = "Ej. Lab 3 · Pabellón B"
            )
            Spacer(Modifier.height(12.dp))

            // Prioridad: Baja, Media (por defecto), Alta
            Text("Prioridad", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            SelectorOpciones(
                opciones = Prioridad.entries,
                seleccionada = f.prioridad,
                etiqueta = { it.etiqueta },
                onSeleccion = vm::cambiarPrioridad,
                color = { colorPrioridad(it) }
            )
            Spacer(Modifier.height(12.dp))

            AyudaTextField(
                valor = f.descripcion,
                onCambio = vm::cambiarDescripcion,
                etiqueta = "Descripción",
                error = f.errorDescripcion,
                lineas = 4
            )
            Spacer(Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(Icons.Outlined.Schedule, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Tiempo de respuesta estimado: ${f.prioridad.horasRespuesta} horas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            AyudaButton("Enviar ticket", onClick = vm::enviar, habilitado = !f.enviando)
            Spacer(Modifier.height(16.dp))
        }
    }
}