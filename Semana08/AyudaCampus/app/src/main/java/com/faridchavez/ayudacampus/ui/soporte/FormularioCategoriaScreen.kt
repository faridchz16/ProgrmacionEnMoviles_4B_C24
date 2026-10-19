package com.faridchavez.ayudacampus.ui.soporte

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.data.local.entity.Categoria
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.AyudaTextField
import com.faridchavez.ayudacampus.ui.components.EstiloBoton
import com.faridchavez.ayudacampus.ui.components.ICONOS_CATEGORIA
import com.faridchavez.ayudacampus.ui.components.iconoCategoria
import com.faridchavez.ayudacampus.viewmodel.CategoriasViewModel

@Composable
fun FormularioCategoriaScreen(
    vm: CategoriasViewModel,
    id: Int?,
    onVolver: () -> Unit
) {
    val editando by vm.editando.collectAsStateWithLifecycle()
    val cantidadTickets by vm.ticketsDeCategoria.collectAsStateWithLifecycle()
    val errorNombre by vm.errorNombre.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val listo by vm.listo.collectAsStateWithLifecycle()

    var nombre by rememberSaveable { mutableStateOf("") }
    var icono by rememberSaveable { mutableStateOf("tag") }
    var activa by rememberSaveable { mutableStateOf(true) }
    var cargado by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(id) { if (id != null) vm.cargar(id) }

    // Al editar, se copian los datos de la categoría a los campos (una sola vez)
    LaunchedEffect(editando) {
        val c = editando
        if (c != null && !cargado) {
            nombre = c.nombre
            icono = c.icono
            activa = c.activa
            cargado = true
        }
    }

    LaunchedEffect(listo) {
        if (listo) {
            vm.confirmarListo()
            onVolver()
        }
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(if (id == null) "Nueva categoría" else "Editar categoría", onAtras = onVolver)
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AyudaTextField(
                valor = nombre,
                onCambio = {
                    nombre = it
                    vm.limpiarErrorNombre()
                },
                etiqueta = "Nombre de la categoría",
                error = errorNombre
            )

            Text("Icono", style = MaterialTheme.typography.titleMedium)
            ICONOS_CATEGORIA.chunked(3).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    fila.forEach { opcion ->
                        val seleccionado = opcion == icono
                        val forma = RoundedCornerShape(14.dp)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(forma)
                                .background(
                                    if (seleccionado) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = if (seleccionado) 2.dp else 1.dp,
                                    color = if (seleccionado) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    shape = forma
                                )
                                .clickable { icono = opcion }
                        ) {
                            Icon(
                                iconoCategoria(opcion), contentDescription = opcion,
                                tint = if (seleccionado) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Switch "Activa"
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Categoría activa", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Los estudiantes podrán elegirla",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = activa, onCheckedChange = { activa = it })
                }
            }

            // Vista previa
            Text("Vista previa", style = MaterialTheme.typography.titleMedium)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(iconoCategoria(icono), null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            nombre.ifBlank { "Nombre de la categoría" },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "$cantidadTickets tickets · ${if (activa) "Activa" else "Inactiva"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            AyudaButton(
                "Guardar categoría",
                onClick = {
                    vm.guardar(
                        Categoria(id = id ?: 0, nombre = nombre, icono = icono, activa = activa)
                    )
                }
            )
            AyudaButton("Cancelar", onClick = onVolver, estilo = EstiloBoton.CONTORNO)

            val actual = editando
            if (id != null && actual != null) {
                AyudaButton(
                    "Eliminar categoría",
                    onClick = { vm.eliminar(actual) },
                    estilo = EstiloBoton.PELIGRO
                )
            }
        }
    }
}