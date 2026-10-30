package com.duoc.agroconnect

import android.content.Context
import com.duoc.agroconnect.repository.AuthRepository
import com.duoc.agroconnect.repository.DronRepository
import com.duoc.agroconnect.repository.PedidoRepository

/** Aquí viven los repositorios para que todas las pantallas compartan los mismos datos. */
object AppContainer {
    lateinit var authRepository: AuthRepository
        private set

    val dronRepository = DronRepository()
    val pedidoRepository = PedidoRepository()

    /** Se llama una sola vez desde AgroApplication, porque AuthRepository necesita el Context. */
    fun iniciar(context: Context) {
        authRepository = AuthRepository(context.applicationContext)
    }
}
