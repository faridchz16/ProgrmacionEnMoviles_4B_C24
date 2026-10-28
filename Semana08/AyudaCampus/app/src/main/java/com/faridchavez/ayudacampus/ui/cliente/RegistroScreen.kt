package com.faridchavez.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.ui.components.AppTopBar
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.AyudaTextField
import com.faridchavez.ayudacampus.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    vm: RegistroViewModel,
    onAtras: () -> Unit,
    onRegistrado: (Usuario) -> Unit
) {
    val f by vm.formulario.collectAsStateWithLifecycle()
    val registrado by vm.registrado.collectAsStateWithLifecycle()

    LaunchedEffect(registrado) {
        registrado?.let {
            vm.confirmarRegistro()
            onRegistrado(it)
        }
    }

    Scaffold(
        topBar = { AppTopBar("Crear cuenta", onAtras = onAtras) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Regístrate como estudiante", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Podrás reportar incidencias y seguir tus tickets.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            AyudaTextField(
                valor = f.nombre,
                onCambio = vm::cambiarNombre,
                etiqueta = "Nombre completo",
                icono = Icons.Outlined.Person,
                error = f.errorNombre
            )
            Spacer(Modifier.height(8.dp))
            AyudaTextField(
                valor = f.correo,
                onCambio = vm::cambiarCorreo,
                etiqueta = "Correo",
                icono = Icons.Outlined.Email,
                error = f.errorCorreo,
                tipoTeclado = KeyboardType.Email
            )
            Spacer(Modifier.height(8.dp))
            AyudaTextField(
                valor = f.clave,
                onCambio = vm::cambiarClave,
                etiqueta = "Contraseña",
                icono = Icons.Outlined.Lock,
                error = f.errorClave,
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
            Spacer(Modifier.height(20.dp))

            AyudaButton("Crear cuenta", onClick = vm::registrar, habilitado = !f.enviando)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onAtras, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}