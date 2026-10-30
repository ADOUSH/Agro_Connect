package com.duoc.agroconnect.repository

import com.duoc.agroconnect.model.Dron
import com.duoc.agroconnect.model.EstadoDron
import com.duoc.agroconnect.model.Mision
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class DronRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _dron = MutableStateFlow(
        Dron(
            id = "D1",
            nombre = "AgroDron T-100",
            latitud = -33.4500,
            longitud = -70.6600,
            bateria = 100,
            estado = EstadoDron.EN_VUELO
        )
    )
    val dron: StateFlow<Dron> = _dron.asStateFlow()

    init {
        scope.launch {
            while (isActive) {
                delay(2000)
                _dron.update { d ->
                    when (d.estado) {
                        EstadoDron.EN_VUELO ->
                            if (d.bateria <= 15) {
                                d.copy(estado = EstadoDron.CARGANDO)
                            } else {
                                d.copy(
                                    latitud = d.latitud + 0.0002,
                                    longitud = d.longitud + 0.0001,
                                    bateria = d.bateria - 1
                                )
                            }
                        EstadoDron.CARGANDO ->
                            if (d.bateria >= 100) {
                                d.copy(estado = EstadoDron.EN_VUELO)
                            } else {
                                d.copy(bateria = (d.bateria + 5).coerceAtMost(100))
                            }
                    }
                }
            }
        }
    }

    fun obtenerMisiones(): List<Mision> = listOf(
        Mision(1, "Pulverización", "Aplicación de fungicida en lote Norte", "22-10-2026", 12.5),
        Mision(2, "Monitoreo", "Mapeo de cultivo de maíz con cámara", "21-10-2026", 20.0),
        Mision(3, "Siembra", "Siembra de pradera en lote Sur", "19-10-2026", 8.0),
        Mision(4, "Fertilización", "Fertilizante en lote Este", "17-10-2026", 15.2)
    )
}
