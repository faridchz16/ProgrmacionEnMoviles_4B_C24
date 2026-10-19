package com.faridchavez.ayudacampus.data.local

import com.faridchavez.ayudacampus.data.local.entity.Categoria
import com.faridchavez.ayudacampus.data.local.entity.Comentario
import com.faridchavez.ayudacampus.data.local.entity.Ticket
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad
import com.faridchavez.ayudacampus.model.Rol
import kotlinx.coroutines.flow.first

object DatosIniciales {

    private const val HORA = 3600 * 1000L

    suspend fun sembrar(db: AyudaDatabase) {
        if (db.usuarioDao().contar() > 0) return

        val ahora = System.currentTimeMillis()
        fun hace(horas: Double): Long = ahora - (horas * HORA).toLong()

        // ---------- Categorías ----------
        val catDao = db.categoriaDao()
        if (catDao.contar() == 0) catDao.insertarTodas(categorias())
        val ids = catDao.observarTodas().first().associate { it.nombre to it.id }
        fun cat(nombre: String): Int = ids.getValue(nombre)
        val hardware = cat("Hardware")
        val software = cat("Software")
        val red = cat("Red / Wi-Fi")
        val cuenta = cat("Cuenta")
        val impresion = cat("Impresión")

        // ---------- Usuarios ----------
        val juan = Usuario(
            nombre = "Juan Pérez", correo = "juan@campus.test",
            clave = "1234", rol = Rol.CLIENTE.name
        )
        val soporte = Usuario(
            nombre = "Soporte TI", correo = "soporte@campus.test",
            clave = "admin", rol = Rol.ADMIN.name
        )
        val juanId = db.usuarioDao().insertar(juan).toInt()
        val soporteId = db.usuarioDao().insertar(soporte).toInt()

        // ---------- Tickets ----------
        val dao = db.ticketDao()

        val tImpresion = dao.insertarTicket(
            Ticket(
                clienteId = juanId, categoriaId = impresion,
                titulo = "No puedo imprimir en biblioteca",
                descripcion = "La impresora del segundo piso no recibe mis documentos.",
                ubicacion = "Biblioteca",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.RESUELTO.name,
                fecha = hace(72.0)
            )
        ).toInt()

        dao.insertarTicket(
            Ticket(
                clienteId = juanId, categoriaId = cuenta,
                titulo = "Olvidé mi contraseña del aula virtual",
                descripcion = "No puedo entrar al aula virtual desde ayer en la mañana.",
                ubicacion = "Remoto",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.CERRADO.name,
                fecha = hace(96.0), fechaCierre = hace(80.0), calificacion = 5
            )
        )

        dao.insertarTicket(
            Ticket(
                clienteId = juanId, categoriaId = hardware,
                titulo = "Teclado no responde",
                descripcion = "El teclado de la PC 12 no escribe algunas letras.",
                ubicacion = "Aula 204",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = hace(26.0)
            )
        )

        dao.insertarTicket(
            Ticket(
                clienteId = juanId, categoriaId = software,
                titulo = "Instalar Android Studio",
                descripcion = "Necesitamos Android Studio en las PCs del laboratorio 5.",
                ubicacion = "Lab 5",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.EN_PROCESO.name,
                fecha = hace(5.0)
            )
        )

        val tWifi = dao.insertarTicket(
            Ticket(
                clienteId = juanId, categoriaId = red,
                titulo = "Sin Wi-Fi en el laboratorio 3",
                descripcion = "Desde ayer no hay señal de Wi-Fi en todo el laboratorio. " +
                        "Afecta a 30 alumnos durante la clase.",
                ubicacion = "Lab 3 · Pabellón B",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.EN_PROCESO.name,
                fecha = hace(2.0)
            )
        ).toInt()

        // ALTA + ABIERTO con más de 1 hora → aparece en la alerta del panel
        dao.insertarTicket(
            Ticket(
                clienteId = juanId, categoriaId = hardware,
                titulo = "Proyector del aula 305 no enciende",
                descripcion = "El proyector no enciende y hay exposición en una hora.",
                ubicacion = "Aula 305",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = hace(1.5)
            )
        )

        // ---------- Comentarios ----------
        fun comentario(ticketId: Int, autor: Usuario, autorId: Int, texto: String, fecha: Long) =
            Comentario(
                ticketId = ticketId, autorId = autorId, autorNombre = autor.nombre,
                autorRol = autor.rol, texto = texto, fecha = fecha
            )

        dao.insertarComentario(
            comentario(tWifi, soporte, soporteId, "Revisaremos el access point del pabellón B hoy.", hace(1.8))
        )
        dao.insertarComentario(
            comentario(tWifi, juan, juanId, "Gracias, quedo atento.", hace(1.6))
        )
        dao.insertarComentario(
            comentario(tWifi, soporte, soporteId, "Reiniciamos el equipo. ¿Ya tienes señal?", hace(0.5))
        )
        dao.insertarComentario(
            comentario(
                tImpresion, soporte, soporteId,
                "Reinstalamos el driver de la impresora. Ya debería imprimir.", hace(24.0)
            )
        )
    }

    fun categorias() = listOf(
        Categoria(nombre = "Hardware", icono = "pc"),
        Categoria(nombre = "Software", icono = "code"),
        Categoria(nombre = "Red / Wi-Fi", icono = "wifi"),
        Categoria(nombre = "Cuenta", icono = "key"),
        Categoria(nombre = "Impresión", icono = "printer")
    )
}