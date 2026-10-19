package com.faridchavez.ayudacampus.ui.theme

import androidx.compose.ui.graphics.Color
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad

// ---------- Estudiante (cian) ----------
val Cian = Color(0xFF0891B2)
val CianContenedor = Color(0xFFCFFAFE)
val CianFondo = Color(0xFFF7FDFE)
val CianOscuro = Color(0xFF67E8F9)
val CianContenedorOscuro = Color(0xFF155E75)

// ---------- Soporte TI (violeta) ----------
val Violeta = Color(0xFF7C3AED)
val VioletaContenedor = Color(0xFFEDE4FF)
val VioletaFondo = Color(0xFFFAF8FF)
val VioletaOscuro = Color(0xFFC4B5FD)
val VioletaContenedorOscuro = Color(0xFF4C1D95)

// ---------- Neutros ----------
val Tinta = Color(0xFF0F172A)
val TintaSuave = Color(0xFF64748B)
val Borde = Color(0xFFE2E8F0)
val FondoOscuro = Color(0xFF0B1220)
val SuperficieOscura = Color(0xFF162032)
val Error = Color(0xFFDC2626)

// ---------- Estado del ticket ----------
val EstadoAbierto = Color(0xFFD97706)   // ámbar
val EstadoEnProceso = Color(0xFF2563EB) // azul
val EstadoResuelto = Color(0xFF16A34A)  // verde
val EstadoCerrado = Color(0xFF6B7280)   // gris

// ---------- Prioridad ----------
val PrioridadAlta = Color(0xFFDC2626)   // rojo
val PrioridadMedia = Color(0xFFB45309)  // café
val PrioridadBaja = Color(0xFF6B7280)   // gris

fun colorEstado(estado: EstadoTicket): Color = when (estado) {
    EstadoTicket.ABIERTO -> EstadoAbierto
    EstadoTicket.EN_PROCESO -> EstadoEnProceso
    EstadoTicket.RESUELTO -> EstadoResuelto
    EstadoTicket.CERRADO -> EstadoCerrado
}

fun colorPrioridad(prioridad: Prioridad): Color = when (prioridad) {
    Prioridad.ALTA -> PrioridadAlta
    Prioridad.MEDIA -> PrioridadMedia
    Prioridad.BAJA -> PrioridadBaja
}