package com.faridchavez.ayudacampus.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.faridchavez.ayudacampus.data.local.entity.Categoria
import com.faridchavez.ayudacampus.data.local.entity.Comentario
import com.faridchavez.ayudacampus.data.local.entity.Ticket
import com.faridchavez.ayudacampus.data.local.entity.Usuario
import com.faridchavez.ayudacampus.model.EstadoTicket
import com.faridchavez.ayudacampus.model.Prioridad

data class TicketConDetalle(
    @Embedded val ticket: Ticket,

    @Relation(parentColumn = "id", entityColumn = "ticketId")
    val comentarios: List<Comentario>,

    @Relation(parentColumn = "categoriaId", entityColumn = "id")
    val categoria: Categoria?,

    @Relation(parentColumn = "clienteId", entityColumn = "id")
    val cliente: Usuario?
)

val TicketConDetalle.estado: EstadoTicket
    get() = EstadoTicket.desde(ticket.estado)

val TicketConDetalle.prioridad: Prioridad
    get() = Prioridad.desde(ticket.prioridad)

val TicketConDetalle.conversacion: List<Comentario>
    get() = comentarios.sortedBy { it.fecha }