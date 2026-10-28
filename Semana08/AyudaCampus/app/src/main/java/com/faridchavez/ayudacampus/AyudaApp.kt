package com.faridchavez.ayudacampus

import android.app.Application
import com.faridchavez.ayudacampus.data.local.AyudaDatabase
import com.faridchavez.ayudacampus.data.local.DatosIniciales
import com.faridchavez.ayudacampus.data.prefs.PreferenciasStore
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.data.repository.AyudaRepositoryRoom
import com.faridchavez.ayudacampus.notificaciones.Notificador
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AyudaApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AyudaDatabase by lazy { AyudaDatabase.obtener(this) }

    val repository: AyudaRepository by lazy { AyudaRepositoryRoom(database) }

    val preferencias: PreferenciasStore by lazy { PreferenciasStore(this) }

    private val notificador: Notificador by lazy { Notificador(this) }

    override fun onCreate() {
        super.onCreate()
        // Siembra los datos de prueba la primera vez, fuera del hilo principal
        appScope.launch { DatosIniciales.sembrar(database) }
        escucharNotificaciones()
    }

    private fun escucharNotificaciones() {
        appScope.launch {
            var ultimoId = repository.observarUltimaNotificacion().first()?.id ?: 0
            repository.observarUltimaNotificacion().collect { n ->
                if (n != null && n.id > ultimoId) {
                    ultimoId = n.id
                    if (preferencias.notificaciones.first()) notificador.mostrar(n)
                }
            }
        }
    }
}