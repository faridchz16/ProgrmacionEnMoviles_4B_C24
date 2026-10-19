package com.faridchavez.ayudacampus.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.faridchavez.ayudacampus.model.Prioridad
import com.faridchavez.ayudacampus.ui.theme.colorPrioridad

@Composable
fun PrioridadChip(prioridad: Prioridad, modifier: Modifier = Modifier) =
    EtiquetaColor(prioridad.etiqueta, colorPrioridad(prioridad), modifier)