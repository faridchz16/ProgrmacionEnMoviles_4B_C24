package com.faridchavez.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.faridchavez.ayudacampus.model.Rol
import com.faridchavez.ayudacampus.ui.cliente.LoginScreen
import com.faridchavez.ayudacampus.ui.cliente.SplashScreen
import com.faridchavez.ayudacampus.ui.soporte.CategoriasScreen
import com.faridchavez.ayudacampus.ui.theme.AyudaTheme
import com.faridchavez.ayudacampus.viewmodel.CategoriasViewModel
import com.faridchavez.ayudacampus.viewmodel.SesionViewModel
import com.faridchavez.ayudacampus.viewmodel.ayudaViewModel

@Composable
fun NavAyuda() {
    val nav = rememberNavController()

    // Una sola sesión para toda la Activity
    val sesion = ayudaViewModel { app -> SesionViewModel(app.repository, app.preferencias) }
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val cargando by sesion.cargando.collectAsStateWithLifecycle()
    val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()

    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val enCliente = ruta?.startsWith(Rutas.GRAFO_CLIENTE) == true
    val enSoporte = ruta?.startsWith(Rutas.GRAFO_SOPORTE) == true

    // Navega según la sesión: entrar (o saltar el login), cambiar de perfil o cerrar sesión
    LaunchedEffect(usuario, cargando, ruta) {
        if (cargando || ruta == null) return@LaunchedEffect
        val destino = when (Rol.desde(usuario?.rol)) {
            Rol.CLIENTE -> Rutas.GRAFO_CLIENTE
            Rol.ADMIN -> Rutas.GRAFO_SOPORTE
            null -> null
        }
        if (destino != null && !ruta.startsWith(destino)) {
            nav.navigate(destino) { popUpTo(nav.graph.id) { inclusive = true } }
        } else if (destino == null && (enCliente || enSoporte)) {
            nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
        }
    }

    AyudaTheme(rol = if (enSoporte) Rol.ADMIN else Rol.CLIENTE, oscuro = oscuro) {
        Scaffold(
            bottomBar = {
                when {
                    enCliente -> BarraCliente(ruta, nav)
                    enSoporte -> BarraSoporte(ruta, nav)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = Rutas.SPLASH,
                modifier = Modifier.padding(padding)
            ) {
                composable(Rutas.SPLASH) {
                    SplashScreen(onComenzar = { nav.navigate(Rutas.LOGIN) })
                }

                composable(Rutas.LOGIN) {
                    LoginScreen(vm = sesion, oscuro = oscuro)
                }

                // ---------------- Estudiante ----------------
                navigation(startDestination = Rutas.MIS_TICKETS, route = Rutas.GRAFO_CLIENTE) {
                    composable(Rutas.MIS_TICKETS) { EnConstruccion("Mis tickets", "Paso 7") }
                    composable(Rutas.NUEVO_TICKET) { EnConstruccion("Nuevo ticket", "Paso 8") }
                    composable(
                        Rutas.DETALLE_TICKET,
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { EnConstruccion("Detalle del ticket", "Paso 9") }
                    composable(Rutas.PERFIL) { EnConstruccion("Perfil", "Paso 12") }
                }

                // ---------------- Soporte TI ----------------
                navigation(startDestination = Rutas.PANEL, route = Rutas.GRAFO_SOPORTE) {
                    composable(Rutas.PANEL) { EnConstruccion("Panel de soporte", "Paso 10") }
                    composable(Rutas.BANDEJA) { EnConstruccion("Bandeja de tickets", "Paso 10") }
                    composable(
                        Rutas.ATENDER_TICKET,
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { EnConstruccion("Atender ticket", "Paso 11") }
                    composable(Rutas.CATEGORIAS) {
                        val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
                        CategoriasScreen(vm = vm, onEditar = { })
                    }
                    composable(
                        Rutas.FORM_CATEGORIA,
                        arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = -1
                        })
                    ) { EnConstruccion("Formulario de categoría", "Paso 11") }
                    composable(Rutas.AJUSTES) { EnConstruccion("Ajustes", "Paso 12") }
                }
            }
        }
    }
}

// ---------------- NavigationBar ----------------

private fun NavHostController.irA(ruta: String, inicio: String) {
    navigate(ruta) {
        popUpTo(inicio)
        launchSingleTop = true
    }
}

@Composable
private fun BarraCliente(ruta: String?, nav: NavHostController) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        ItemBarra(
            "Tickets", Icons.Filled.Menu,
            seleccionado = ruta == Rutas.MIS_TICKETS || ruta == Rutas.DETALLE_TICKET
        ) { nav.irA(Rutas.MIS_TICKETS, Rutas.MIS_TICKETS) }
        ItemBarra("Nuevo", Icons.Filled.Add, seleccionado = ruta == Rutas.NUEVO_TICKET) {
            nav.irA(Rutas.NUEVO_TICKET, Rutas.MIS_TICKETS)
        }
        ItemBarra("Perfil", Icons.Outlined.Person, seleccionado = ruta == Rutas.PERFIL) {
            nav.irA(Rutas.PERFIL, Rutas.MIS_TICKETS)
        }
    }
}

@Composable
private fun BarraSoporte(ruta: String?, nav: NavHostController) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        ItemBarra("Panel", Icons.Filled.BarChart, seleccionado = ruta == Rutas.PANEL) {
            nav.irA(Rutas.PANEL, Rutas.PANEL)
        }
        ItemBarra(
            "Tickets", Icons.Filled.Menu,
            seleccionado = ruta == Rutas.BANDEJA || ruta == Rutas.ATENDER_TICKET
        ) { nav.irA(Rutas.BANDEJA, Rutas.PANEL) }
        ItemBarra(
            "Categorías", Icons.Outlined.LocalOffer,
            seleccionado = ruta == Rutas.CATEGORIAS || ruta == Rutas.FORM_CATEGORIA
        ) { nav.irA(Rutas.CATEGORIAS, Rutas.PANEL) }
        ItemBarra("Ajustes", Icons.Outlined.Settings, seleccionado = ruta == Rutas.AJUSTES) {
            nav.irA(Rutas.AJUSTES, Rutas.PANEL)
        }
    }
}

@Composable
private fun RowScope.ItemBarra(
    texto: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = seleccionado,
        onClick = onClick,
        icon = { Icon(icono, contentDescription = texto) },
        label = { Text(texto) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}


@Composable
private fun EnConstruccion(titulo: String, paso: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            "$titulo\n(se construye en el $paso)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}