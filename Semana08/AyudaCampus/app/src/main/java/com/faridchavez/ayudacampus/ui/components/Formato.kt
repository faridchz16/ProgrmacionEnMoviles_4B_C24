package com.faridchavez.ayudacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val ICONOS_CATEGORIA = listOf("pc", "code", "wifi", "key", "printer", "tag")

fun iconoCategoria(icono: String?): ImageVector = when (icono) {
    "pc" -> Icons.Outlined.Computer
    "code" -> Icons.Outlined.Code
    "wifi" -> Icons.Outlined.Wifi
    "key" -> Icons.Outlined.VpnKey
    "printer" -> Icons.Outlined.Print
    else -> Icons.Outlined.LocalOffer
}

fun numeroTicket(id: Int): String = "#" + id.toString().padStart(4, '0')

fun tiempoRelativo(fecha: Long, ahora: Long = System.currentTimeMillis()): String {
    val minutos = ((ahora - fecha) / 60_000L).coerceAtLeast(0)
    val horas = minutos / 60
    val dias = horas / 24
    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace $minutos min"
        horas < 24 -> "Hace $horas h"
        dias == 1L -> "Ayer"
        else -> "Hace $dias d"
    }
}

fun formatoHora(fecha: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(fecha))

fun formatoFecha(fecha: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fecha))