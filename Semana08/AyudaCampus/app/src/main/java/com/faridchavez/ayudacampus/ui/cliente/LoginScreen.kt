package com.faridchavez.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faridchavez.ayudacampus.model.Rol
import com.faridchavez.ayudacampus.ui.components.AyudaButton
import com.faridchavez.ayudacampus.ui.components.AyudaTextField
import com.faridchavez.ayudacampus.ui.components.SelectorOpciones
import com.faridchavez.ayudacampus.ui.theme.AyudaTheme
import com.faridchavez.ayudacampus.viewmodel.SesionViewModel

@Composable
fun LoginScreen(vm: SesionViewModel, oscuro: Boolean) {
    var rol by rememberSaveable { mutableStateOf(Rol.CLIENTE) }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    val mensajeError by vm.error.collectAsStateWithLifecycle()

    // El tema cambia con la pestaña elegida: cian (Estudiante) o violeta (Soporte TI)
    AyudaTheme(rol = rol, oscuro = oscuro) {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(Modifier.height(32.dp))
                Text("Bienvenido", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Ingresa para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))

                SelectorOpciones(
                    opciones = Rol.entries,
                    seleccionada = rol,
                    etiqueta = { it.etiqueta },
                    onSeleccion = {
                        rol = it
                        vm.limpiarError()
                    }
                )
                Spacer(Modifier.height(20.dp))

                AyudaTextField(
                    valor = correo,
                    onCambio = {
                        correo = it
                        vm.limpiarError()
                    },
                    etiqueta = "Correo",
                    icono = Icons.Outlined.Email,
                    tipoTeclado = KeyboardType.Email
                )
                Spacer(Modifier.height(8.dp))
                AyudaTextField(
                    valor = clave,
                    onCambio = {
                        clave = it
                        vm.limpiarError()
                    },
                    etiqueta = "Contraseña",
                    icono = Icons.Outlined.Lock,
                    esClave = true
                )
                TextButton(onClick = { }, modifier = Modifier.align(Alignment.End)) {
                    Text("¿Olvidaste tu contraseña?")
                }

                mensajeError?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                AyudaButton("Ingresar", onClick = { vm.login(correo, clave, rol) })
                Spacer(Modifier.height(16.dp))

                if (rol == Rol.CLIENTE) {
                    Text(
                        "¿No tienes cuenta?  Regístrate",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Modo práctica",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    "Cuentas de prueba: juan@campus.test / 1234 · soporte@campus.test / admin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}