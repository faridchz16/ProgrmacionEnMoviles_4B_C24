package com.faridchavez.ayudacampus.ui.navigation

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
import com.faridchavez.ayudacampus.ui.cliente.DetalleTicketScreen
import com.faridchavez.ayudacampus.ui.cliente.LoginScreen
import com.faridchavez.ayudacampus.ui.cliente.MisTicketsScreen
import com.faridchavez.ayudacampus.ui.cliente.NuevoTicketScreen
import com.faridchavez.ayudacampus.ui.cliente.PerfilScreen
import com.faridchavez.ayudacampus.ui.cliente.RecuperarClaveScreen
import com.faridchavez.ayudacampus.ui.cliente.RegistroScreen
import com.faridchavez.ayudacampus.ui.cliente.SplashScreen
import com.faridchavez.ayudacampus.ui.comun.NotificacionesScreen
import com.faridchavez.ayudacampus.ui.soporte.AjustesScreen
import com.faridchavez.ayudacampus.ui.soporte.AtenderTicketScreen
import com.faridchavez.ayudacampus.ui.soporte.BandejaScreen
import com.faridchavez.ayudacampus.ui.soporte.CategoriasScreen
import com.faridchavez.ayudacampus.ui.soporte.FormularioCategoriaScreen
import com.faridchavez.ayudacampus.ui.soporte.PanelScreen
import com.faridchavez.ayudacampus.ui.theme.AyudaTheme
import com.faridchavez.ayudacampus.viewmodel.AtenderViewModel
import com.faridchavez.ayudacampus.viewmodel.BandejaViewModel
import com.faridchavez.ayudacampus.viewmodel.CategoriasViewModel
import com.faridchavez.ayudacampus.viewmodel.MisTicketsViewModel
import com.faridchavez.ayudacampus.viewmodel.NotificacionesViewModel
import com.faridchavez.ayudacampus.viewmodel.NuevoTicketViewModel
import com.faridchavez.ayudacampus.viewmodel.PanelViewModel
import com.faridchavez.ayudacampus.viewmodel.RecuperarClaveViewModel
import com.faridchavez.ayudacampus.viewmodel.RegistroViewModel
import com.faridchavez.ayudacampus.viewmodel.SesionViewModel
import com.faridchavez.ayudacampus.viewmodel.TicketDetalleViewModel
import com.faridchavez.ayudacampus.viewmodel.ayudaViewModel

@Composable
fun NavAyuda() {
    val nav = rememberNavController()

    // Una sola sesión para toda la Activity
    val sesion = ayudaViewModel { app -> SesionViewModel(app.repository, app.preferencias) }
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val cargando by sesion.cargando.collectAsStateWithLifecycle()
    val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()

    // Avisos del usuario activo: contador de la campanita y lista de notificaciones
    val notifVm = usuario?.let { u ->
        ayudaViewModel(clave = "notif-${u.id}") { app -> NotificacionesViewModel(app.repository, u) }
    }
    val noLeidas = notifVm?.noLeidas?.collectAsStateWithLifecycle()?.value ?: 0

    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val enCliente = ruta?.startsWith(Rutas.GRAFO_CLIENTE) == true
    val enSoporte = ruta?.startsWith(Rutas.GRAFO_SOPORTE) == true

    // Íconos de la barra de estado claros u oscuros según el modo oscuro de la app
    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            WindowCompat.getInsetsController(ventana, vista).apply {
                isAppearanceLightStatusBars = !oscuro
                isAppearanceLightNavigationBars = !oscuro
            }
        }
    }

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
                modifier = Modifier.padding(padding),
                enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { 300 } },
                exitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -300 } },
                popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -300 } },
                popExitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { 300 } }
            ) {
                composable(Rutas.SPLASH) {
                    SplashScreen(onComenzar = { nav.navigate(Rutas.LOGIN) })
                }

                composable(Rutas.LOGIN) {
                    LoginScreen(
                        vm = sesion,
                        oscuro = oscuro,
                        onOlvidaste = { nav.navigate(Rutas.RECUPERAR) },
                        onRegistrarse = { nav.navigate(Rutas.REGISTRO) }
                    )
                }

                composable(Rutas.REGISTRO) {
                    val vm = ayudaViewModel { app -> RegistroViewModel(app.repository) }
                    RegistroScreen(
                        vm = vm,
                        onAtras = { nav.popBackStack() },
                        onRegistrado = { usuario -> sesion.iniciarSesion(usuario) }
                    )
                }

                composable(Rutas.RECUPERAR) {
                    val vm = ayudaViewModel { app -> RecuperarClaveViewModel(app.repository) }
                    RecuperarClaveScreen(vm = vm, onVolver = { nav.popBackStack() })
                }

                // ---------------- Estudiante ----------------
                navigation(startDestination = Rutas.MIS_TICKETS, route = Rutas.GRAFO_CLIENTE) {
                    composable(Rutas.MIS_TICKETS) {
                        val u = usuario ?: return@composable
                        val vm = ayudaViewModel(clave = "mis-${u.id}") { app ->
                            MisTicketsViewModel(app.repository, u)
                        }
                        MisTicketsScreen(
                            vm = vm,
                            onAbrir = { id -> nav.navigate(Rutas.detalleTicket(id)) },
                            onNuevo = { nav.irA(Rutas.NUEVO_TICKET, Rutas.MIS_TICKETS) },
                            noLeidas = noLeidas,
                            onNotificaciones = { nav.navigate(Rutas.NOTIF_CLIENTE) }
                        )
                    }
                    composable(Rutas.NUEVO_TICKET) {
                        val u = usuario ?: return@composable
                        val vm = ayudaViewModel(clave = "nuevo-${u.id}") { app ->
                            NuevoTicketViewModel(app.repository, u, app.preferencias.recibirTickets)
                        }
                        NuevoTicketScreen(
                            vm = vm,
                            onAtras = { nav.popBackStack() },
                            onEnviado = { nav.irA(Rutas.MIS_TICKETS, Rutas.MIS_TICKETS) }
                        )
                    }
                    composable(
                        Rutas.DETALLE_TICKET,
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { entrada ->
                        val u = usuario ?: return@composable
                        val id = entrada.arguments?.getInt("id") ?: return@composable
                        val vm = ayudaViewModel(clave = "detalle-$id-${u.id}") { app ->
                            TicketDetalleViewModel(app.repository, id, u)
                        }
                        DetalleTicketScreen(vm = vm, onAtras = { nav.popBackStack() })
                    }
                    composable(Rutas.PERFIL) { PerfilScreen(vm = sesion) }
                    composable(Rutas.NOTIF_CLIENTE) {
                        val vm = notifVm ?: return@composable
                        NotificacionesScreen(
                            vm = vm,
                            onAtras = { nav.popBackStack() },
                            onAbrirTicket = { id -> nav.navigate(Rutas.detalleTicket(id)) }
                        )
                    }
                }

                // ---------------- Soporte TI ----------------
                navigation(startDestination = Rutas.PANEL, route = Rutas.GRAFO_SOPORTE) {
                    composable(Rutas.PANEL) {
                        val vm = ayudaViewModel { app -> PanelViewModel(app.repository) }
                        PanelScreen(
                            vm = vm,
                            onVerAlerta = { nav.irA(Rutas.BANDEJA, Rutas.PANEL) },
                            noLeidas = noLeidas,
                            onNotificaciones = { nav.navigate(Rutas.NOTIF_SOPORTE) }
                        )
                    }
                    composable(Rutas.BANDEJA) {
                        val vm = ayudaViewModel { app -> BandejaViewModel(app.repository) }
                        BandejaScreen(
                            vm = vm,
                            onAtender = { id -> nav.navigate(Rutas.atenderTicket(id)) }
                        )
                    }
                    composable(
                        Rutas.ATENDER_TICKET,
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { entrada ->
                        val u = usuario ?: return@composable
                        val id = entrada.arguments?.getInt("id") ?: return@composable
                        val vm = ayudaViewModel(clave = "atender-$id-${u.id}") { app ->
                            AtenderViewModel(app.repository, id, u)
                        }
                        AtenderTicketScreen(vm = vm, onAtras = { nav.popBackStack() })
                    }
                    composable(Rutas.CATEGORIAS) {
                        val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
                        CategoriasScreen(
                            vm = vm,
                            onEditar = { id -> nav.navigate(Rutas.formCategoria(id)) },
                            onNueva = { nav.navigate(Rutas.formCategoria()) }
                        )
                    }
                    composable(
                        Rutas.FORM_CATEGORIA,
                        arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = -1
                        })
                    ) { entrada ->
                        val id = entrada.arguments?.getInt("id")?.takeIf { it > 0 }
                        val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
                        FormularioCategoriaScreen(
                            vm = vm,
                            id = id,
                            onVolver = { nav.popBackStack() }
                        )
                    }
                    composable(Rutas.AJUSTES) { AjustesScreen(vm = sesion) }
                    composable(Rutas.NOTIF_SOPORTE) {
                        val vm = notifVm ?: return@composable
                        NotificacionesScreen(
                            vm = vm,
                            onAtras = { nav.popBackStack() },
                            onAbrirTicket = { id -> nav.navigate(Rutas.atenderTicket(id)) }
                        )
                    }
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
