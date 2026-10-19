package com.faridchavez.ayudacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.faridchavez.ayudacampus.data.local.dao.CategoriaDao
import com.faridchavez.ayudacampus.data.local.dao.TicketDao
import com.faridchavez.ayudacampus.data.local.dao.UsuarioDao
import com.faridchavez.ayudacampus.data.local.entity.Categoria
import com.faridchavez.ayudacampus.data.local.entity.Comentario
import com.faridchavez.ayudacampus.data.local.entity.Ticket
import com.faridchavez.ayudacampus.data.local.entity.Usuario

@Database(
    entities = [Usuario::class, Categoria::class, Ticket::class, Comentario::class],
    version = 2,
    exportSchema = false
)
abstract class AyudaDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCIA: AyudaDatabase? = null

        fun obtener(context: Context): AyudaDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AyudaDatabase::class.java,
                    "ayuda_campus.db"
                )
                    // En el laboratorio no migramos: si cambia la versión, se recrea la base
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCIA = it }
            }
    }
}