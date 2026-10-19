package com.faridchavez.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.model.Rol
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.EstiloBoton
import com.faridchavez.ayudacampus.ui.components.FilaSwitch
import com.faridchavez.ayudacampus.ui.components.NotaDataStore
import com.faridchavez.ayudacampus.ui.components.TarjetaCambiarPerfil
import com.faridchavez.ayudacampus.ui.components.TarjetaPreferencias
import com.faridchavez.ayudacampus.ui.components.TarjetaUsuario
import com.faridchavez.ayudacampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(vm: SesionViewModel) {
    val usuario by vm.usuarioActual.collectAsStateWithLifecycle()
    val oscuro by vm.modoOscuro.collectAsStateWithLifecycle()
    val notificaciones by vm.notificaciones.collectAsStateWithLifecycle()
    val recibirTickets by vm.recibirTickets.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Ajustes") },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            usuario?.let { TarjetaUsuario(it.nombre, it.correo, "Perfil activo") }

            TarjetaCambiarPerfil(destino = Rol.CLIENTE.etiqueta, onClick = vm::cambiarPerfil)

            Text("Preferencias", style = MaterialTheme.typography.titleMedium)
            TarjetaPreferencias {
                FilaSwitch("Modo oscuro", "Cambia el tema de la app", oscuro, vm::cambiarModoOscuro)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilaSwitch(
                    "Notificaciones", "Avisar tickets nuevos",
                    notificaciones, vm::cambiarNotificaciones
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilaSwitch(
                    "Recibir tickets", "Abrir o cerrar la mesa de ayuda",
                    recibirTickets, vm::cambiarRecibirTickets
                )
            }
            NotaDataStore()

            AyudaButton("Cerrar sesión", onClick = vm::cerrarSesion, estilo = EstiloBoton.PELIGRO)
        }
    }
}