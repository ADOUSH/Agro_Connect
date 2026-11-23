package com.duoc.agroconnect.repository

import com.duoc.agroconnect.model.EstadoPedido
import com.duoc.agroconnect.model.Pedido
import com.duoc.agroconnect.network.PedidoDto
import com.duoc.agroconnect.network.PedidoEstadoRequest
import com.duoc.agroconnect.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PedidoRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    init {
        sincronizarDesdeApi()
    }

    fun sincronizarDesdeApi() {
        scope.launch {
            try {
                val remotos = RetrofitClient.api.obtenerPedidos()
                _pedidos.value = remotos.map { it.toDomain() }.reversed()
            } catch (_: Exception) {
            }
        }
    }

    fun agregar(producto: String, cantidad: Int, notas: String) {
        val ahora = System.currentTimeMillis()
        _pedidos.update { lista ->
            val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
            listOf(
                Pedido(
                    id = nuevoId,
                    producto = producto,
                    cantidad = cantidad,
                    notas = notas,
                    estado = EstadoPedido.PENDIENTE,
                    fechaPedido = ahora,
                    fechaEntrega = null
                )
            ) + lista
        }
        scope.launch {
            try {
                RetrofitClient.api.crearPedido(
                    PedidoDto(
                        producto = producto,
                        cantidad = cantidad,
                        notas = notas,
                        estado = EstadoPedido.PENDIENTE.name,
                        fechaPedido = ahora,
                        fechaEntrega = null
                    )
                )
                sincronizarDesdeApi()
            } catch (_: Exception) {
            }
        }
    }

    fun marcarEntregado(pedidoId: Int) {
        val entrega = System.currentTimeMillis()
        scope.launch {
            try {
                RetrofitClient.api.actualizarEstadoPedido(
                    id = pedidoId,
                    request = PedidoEstadoRequest(
                        estado = EstadoPedido.ENTREGADO.name,
                        fechaEntrega = entrega
                    )
                )
                sincronizarDesdeApi()
            } catch (_: Exception) {
            }
        }
    }
}