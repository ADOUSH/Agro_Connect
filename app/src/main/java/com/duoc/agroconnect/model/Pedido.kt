package com.duoc.agroconnect.model

enum class EstadoPedido(val texto: String) {
    PENDIENTE("Pendiente"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado")
}

/** Las fechas van en milisegundos; fechaEntrega es null mientras el pedido no haya llegado. */
data class Pedido(
    val id: Int,
    val producto: String,
    val cantidad: Int,
    val notas: String,
    val estado: EstadoPedido = EstadoPedido.PENDIENTE,
    val fechaPedido: Long = System.currentTimeMillis(),
    val fechaEntrega: Long? = null
)
