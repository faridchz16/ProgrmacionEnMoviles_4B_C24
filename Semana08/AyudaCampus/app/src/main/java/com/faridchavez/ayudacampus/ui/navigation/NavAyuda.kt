package com.faridchavez.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faridchavez.ayudacampus.model.Rol
import com.faridchavez.ayudacampus.ui.cliente.SplashScreen
import com.faridchavez.ayudacampus.ui.soporte.CategoriasScreen
import com.faridchavez.ayudacampus.ui.theme.AyudaTheme
import com.faridchavez.ayudacampus.viewmodel.CategoriasViewModel
import com.faridchavez.ayudacampus.viewmodel.ayudaViewModel

@Composable
fun NavAyuda() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            AyudaTheme(rol = Rol.CLIENTE) {
                SplashScreen(onComenzar = { nav.navigate(Rutas.CATEGORIAS) })
            }
        }

        composable(Rutas.CATEGORIAS) {
            AyudaTheme(rol = Rol.ADMIN) {
                val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
                Box(Modifier.navigationBarsPadding()) {
                    CategoriasScreen(
                        vm = vm,
                        onEditar = { /* Paso 11 */ },
                        onAtras = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}