package com.faridchavez.ayudacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampanaNotificaciones(noLeidas: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (noLeidas > 0) {
                    Badge { Text(if (noLeidas > 9) "9+" else "$noLeidas") }
                }
            }
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones")
        }
    }
}