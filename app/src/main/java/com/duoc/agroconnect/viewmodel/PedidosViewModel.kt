package com.duoc.agroconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.agroconnect.AppContainer
import com.duoc.agroconnect.model.Pedido
import kotlinx.coroutines.flow.StateFlow

class PedidosViewModel : ViewModel() {
    private val repo = AppContainer.pedidoRepository

    val pedidos: StateFlow<List<Pedido>> = repo.pedidos

    fun crearPedido(producto: String, cantidad: Int, notas: String) {
        repo.agregar(producto, cantidad, notas)
    }
}
