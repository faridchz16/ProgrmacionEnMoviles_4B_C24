package com.faridchavez.ayudacampus.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.faridchavez.ayudacampus.model.Rol

private fun esquemaClaro(primario: Color, contenedor: Color, fondo: Color): ColorScheme =
    lightColorScheme(
        primary = primario,
        onPrimary = Color.White,
        primaryContainer = contenedor,
        onPrimaryContainer = primario,
        secondary = primario,
        onSecondary = Color.White,
        secondaryContainer = contenedor,
        onSecondaryContainer = Tinta,
        background = fondo,
        onBackground = Tinta,
        surface = Color.White,
        onSurface = Tinta,
        surfaceVariant = Color(0xFFF1F5F9),
        onSurfaceVariant = TintaSuave,
        surfaceContainer = Color.White,
        surfaceContainerLow = Color.White,
        surfaceContainerHigh = Color(0xFFF1F5F9),
        outline = Color(0xFFCBD5E1),
        outlineVariant = Borde,
        error = Error,
        onError = Color.White
    )

private fun esquemaOscuro(primario: Color, contenedor: Color): ColorScheme =
    darkColorScheme(
        primary = primario,
        onPrimary = Color(0xFF0B1220),
        primaryContainer = contenedor,
        onPrimaryContainer = Color.White,
        secondary = primario,
        secondaryContainer = contenedor,
        onSecondaryContainer = Color.White,
        background = FondoOscuro,
        onBackground = Color(0xFFE2E8F0),
        surface = SuperficieOscura,
        onSurface = Color(0xFFE2E8F0),
        surfaceVariant = Color(0xFF1E293B),
        onSurfaceVariant = Color(0xFF94A3B8),
        surfaceContainer = SuperficieOscura,
        surfaceContainerLow = SuperficieOscura,
        surfaceContainerHigh = Color(0xFF1E293B),
        outline = Color(0xFF475569),
        outlineVariant = Color(0xFF334155),
        error = Color(0xFFF87171),
        onError = Color(0xFF0B1220)
    )

@Composable
fun AyudaTheme(
    rol: Rol = Rol.CLIENTE,
    oscuro: Boolean = false,
    content: @Composable () -> Unit
) {
    val esquema = when (rol) {
        Rol.CLIENTE ->
            if (oscuro) esquemaOscuro(CianOscuro, CianContenedorOscuro)
            else esquemaClaro(Cian, CianContenedor, CianFondo)
        Rol.ADMIN ->
            if (oscuro) esquemaOscuro(VioletaOscuro, VioletaContenedorOscuro)
            else esquemaClaro(Violeta, VioletaContenedor, VioletaFondo)
    }
    MaterialTheme(
        colorScheme = esquema,
        typography = AyudaTypography,
        content = content
    )
}