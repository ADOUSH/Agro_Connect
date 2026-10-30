package com.duoc.agroconnect.repository

import com.duoc.agroconnect.model.EstadoPedido
import com.duoc.agroconnect.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class PedidoRepository {

    private val _pedidos = MutableStateFlow(
        listOf(
            Pedido(
                id = 1,
                producto = "Repuesto de hélice",
                cantidad = 4,
                notas = "Para el T-100",
                estado = EstadoPedido.EN_CAMINO,
                fechaPedido = fecha(29, 10, 2026, 10, 30)
            ),
            Pedido(
                id = 2,
                producto = "Batería de repuesto",
                cantidad = 2,
                notas = "",
                estado = EstadoPedido.ENTREGADO,
                fechaPedido = fecha(27, 10, 2026, 9, 15),
                fechaEntrega = fecha(28, 10, 2026, 16, 40)
            )
        )
    )
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    fun agregar(producto: String, cantidad: Int, notas: String) {
        _pedidos.update { lista ->
            val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
            listOf(Pedido(nuevoId, producto, cantidad, notas)) + lista
        }
    }

    private fun fecha(dia: Int, mes: Int, anio: Int, hora: Int, minuto: Int): Long =
        Calendar.getInstance().apply {
            clear()
            set(anio, mes - 1, dia, hora, minuto)
        }.timeInMillis
}
