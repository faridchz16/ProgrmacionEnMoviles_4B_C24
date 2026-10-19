package com.faridchavez.ayudacampus

import android.app.Application
import com.faridchavez.ayudacampus.data.local.AyudaDatabase
import com.faridchavez.ayudacampus.data.local.DatosIniciales
import com.faridchavez.ayudacampus.data.repository.AyudaRepository
import com.faridchavez.ayudacampus.data.repository.AyudaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AyudaApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AyudaDatabase by lazy { AyudaDatabase.obtener(this) }

    val repository: AyudaRepository by lazy { AyudaRepositoryRoom(database) }

    override fun onCreate() {
        super.onCreate()
        // Siembra los datos de prueba la primera vez, fuera del hilo principal
        appScope.launch { DatosIniciales.sembrar(database) }
    }
}