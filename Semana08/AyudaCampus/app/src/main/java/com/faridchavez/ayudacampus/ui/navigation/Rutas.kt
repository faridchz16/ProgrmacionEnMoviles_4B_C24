package com.faridchavez.ayudacampus.ui.navigation

object Rutas {
    // Comunes
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val RECUPERAR = "recuperar"

    // Grafo del estudiante (CLIENTE)
    const val GRAFO_CLIENTE = "cliente"
    const val MIS_TICKETS = "cliente/tickets"
    const val NUEVO_TICKET = "cliente/nuevo"
    const val DETALLE_TICKET = "cliente/ticket/{id}"
    const val PERFIL = "cliente/perfil"
    fun detalleTicket(id: Int) = "cliente/ticket/$id"
    const val NOTIF_CLIENTE = "cliente/notificaciones"

    // Grafo de Soporte TI (ADMIN)
    const val GRAFO_SOPORTE = "soporte"
    const val PANEL = "soporte/panel"
    const val BANDEJA = "soporte/bandeja"
    const val ATENDER_TICKET = "soporte/atender/{id}"
    const val CATEGORIAS = "soporte/categorias"
    const val FORM_CATEGORIA = "soporte/categoria?id={id}"
    const val AJUSTES = "soporte/ajustes"
    const val NOTIF_SOPORTE = "soporte/notificaciones"
    fun atenderTicket(id: Int) = "soporte/atender/$id"
    fun formCategoria(id: Int? = null) =
        if (id == null) "soporte/categoria" else "soporte/categoria?id=$id"
}