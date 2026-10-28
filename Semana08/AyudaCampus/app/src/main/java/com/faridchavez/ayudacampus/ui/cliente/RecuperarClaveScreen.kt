package com.faridchavez.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.AyudaTextField
import com.faridchavez.ayudacampus.ui.theme.EstadoResuelto
import com.faridchavez.ayudacampus.viewmodel.RecuperarClaveViewModel

@Composable
fun RecuperarClaveScreen(vm: RecuperarClaveViewModel, onVolver: () -> Unit) {
    val f by vm.formulario.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Recuperar contraseña", onAtras = onVolver) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            when {
                f.listo -> ClaveActualizada(onVolver)

                f.nombreCuenta == null -> {
                    Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Escribe el correo de tu cuenta para crear una nueva contraseña.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(20.dp))
                    AyudaTextField(
                        valor = f.correo,
                        onCambio = vm::cambiarCorreo,
                        etiqueta = "Correo",
                        icono = Icons.Outlined.Email,
                        error = f.errorCorreo,
                        tipoTeclado = KeyboardType.Email
                    )
                    Spacer(Modifier.height(16.dp))
                    AyudaButton("Continuar", onClick = vm::buscarCuenta)
                }

                else -> {
                    Text("Hola, ${f.nombreCuenta}", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Crea una nueva contraseña para tu cuenta.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(20.dp))
                    AyudaTextField(
                        valor = f.nueva,
                        onCambio = vm::cambiarNueva,
                        etiqueta = "Nueva contraseña",
                        icono = Icons.Outlined.Lock,
                        error = f.errorNueva,
                        esClave = true
                    )
                    Spacer(Modifier.height(8.dp))
                    AyudaTextField(
                        valor = f.confirmar,
                        onCambio = vm::cambiarConfirmar,
                        etiqueta = "Confirmar contraseña",
                        icono = Icons.Outlined.Lock,
                        error = f.errorConfirmar,
                        esClave = true
                    )
                    Spacer(Modifier.height(16.dp))
                    AyudaButton("Cambiar contraseña", onClick = vm::guardarNuevaClave)
                    TextButton(
                        onClick = vm::usarOtroCorreo,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("Usar otro correo") }
                }
            }

            if (!f.listo) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Modo práctica: en una app real se enviaría un código de verificación " +
                            "a tu correo antes de cambiar la contraseña.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ClaveActualizada(onVolver: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EstadoResuelto.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = EstadoResuelto)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("¡Contraseña actualizada!", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Ya puedes ingresar con tu nueva contraseña.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    AyudaButton("Ir a iniciar sesión", onClick = onVolver)
}