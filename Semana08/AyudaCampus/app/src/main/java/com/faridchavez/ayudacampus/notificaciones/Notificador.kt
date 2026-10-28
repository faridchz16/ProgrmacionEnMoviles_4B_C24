package com.faridchavez.ayudacampus.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.faridchavez.ayudacampus.MainActivity
import com.faridchavez.ayudacampus.data.local.entity.Notificacion
import com.faridchavez.ayudacampus.model.Rol

class Notificador(private val context: Context) {

    init {
        // minSdk 26: los canales de notificación siempre existen
        val canal = NotificationChannel(
            CANAL, "Avisos de tickets", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Nuevos tickets, respuestas y cambios de estado" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    private fun tienePermiso(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission") // el permiso se verifica en tienePermiso()
    fun mostrar(n: Notificacion) {
        if (!tienePermiso()) return
        val abrirApp = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val paraQuien = if (n.paraRol == Rol.ADMIN.name) "Soporte TI" else "Estudiante"
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(n.titulo)
            .setContentText(n.mensaje)
            .setSubText(paraQuien)
            .setStyle(NotificationCompat.BigTextStyle().bigText(n.mensaje))
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(n.id, notificacion)
    }

    private companion object {
        const val CANAL = "avisos_tickets"
    }
}