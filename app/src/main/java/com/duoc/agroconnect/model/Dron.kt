package com.duoc.agroconnect.model

enum class EstadoDron(val texto: String) {
    EN_VUELO("En vuelo"),
    CARGANDO("Cargando")
}

data class Dron(
    val id: String,
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val bateria: Int,
    val estado: EstadoDron
)
