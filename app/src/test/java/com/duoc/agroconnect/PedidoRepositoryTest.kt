package com.duoc.agroconnect

import com.duoc.agroconnect.model.EstadoPedido
import com.duoc.agroconnect.repository.PedidoRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class PedidoRepositoryTest {

    @Test
    fun agregarPedido_incrementaListaYAsignaEstadoPendiente() {
        val repo = PedidoRepository()
        val cantidadInicial = repo.pedidos.value.size

        repo.agregar(
            producto = "Boquilla pulverizadora",
            cantidad = 6,
            notas = "Urgente para lote Norte"
        )

        val listaActualizada = repo.pedidos.value
        val pedidoCreado = listaActualizada.first()

        assertEquals(cantidadInicial + 1, listaActualizada.size)
        assertEquals("Boquilla pulverizadora", pedidoCreado.producto)
        assertEquals(6, pedidoCreado.cantidad)
        assertEquals(EstadoPedido.PENDIENTE, pedidoCreado.estado)
    }
}