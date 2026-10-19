package com.faridchavez.ayudacampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.faridchavez.ayudacampus.ui.theme.EstadoAbierto
import com.faridchavez.ayudacampus.ui.theme.EstadoResuelto
import com.faridchavez.ayudacampus.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(vm: SesionViewModel) {
    val usuario by vm.usuarioActual.collectAsStateWithLifecycle()
    val resumen by vm.resumen.collectAsStateWithLifecycle()
    val oscuro by vm.modoOscuro.collectAsStateWithLifecycle()
    val notificaciones by vm.notificaciones.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Perfil") },
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
            usuario?.let { TarjetaUsuario(it.nombre, it.correo, Rol.CLIENTE.etiqueta) }

            // Contadores: abiertos, resueltos y totales
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ContadorPerfil(resumen.abiertos, "Abiertos", EstadoAbierto, Modifier.weight(1f))
                ContadorPerfil(resumen.resueltos, "Resueltos", EstadoResuelto, Modifier.weight(1f))
                ContadorPerfil(
                    resumen.total, "Total", MaterialTheme.colorScheme.primary, Modifier.weight(1f)
                )
            }

            TarjetaCambiarPerfil(destino = Rol.ADMIN.etiqueta, onClick = vm::cambiarPerfil)

            Text("Preferencias", style = MaterialTheme.typography.titleMedium)
            TarjetaPreferencias {
                FilaSwitch("Modo oscuro", "Cambia el tema de la app", oscuro, vm::cambiarModoOscuro)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilaSwitch(
                    "Notificaciones", "Avisar cambios de estado",
                    notificaciones, vm::cambiarNotificaciones
                )
            }
            NotaDataStore()

            AyudaButton("Cerrar sesión", onClick = vm::cerrarSesion, estilo = EstiloBoton.PELIGRO)
        }
    }
}

@Composable
private fun ContadorPerfil(valor: Int, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
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