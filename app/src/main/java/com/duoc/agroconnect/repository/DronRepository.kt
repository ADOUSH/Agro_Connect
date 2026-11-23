package com.duoc.agroconnect.repository

import com.duoc.agroconnect.model.Alerta
import com.duoc.agroconnect.model.Dron
import com.duoc.agroconnect.model.EstadoDron
import com.duoc.agroconnect.model.Mision
import com.duoc.agroconnect.network.AlertaDto
import com.duoc.agroconnect.network.DronUpdateRequest
import com.duoc.agroconnect.network.MisionDto
import com.duoc.agroconnect.network.RetrofitClient
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

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _dron = MutableStateFlow(
        Dron("1", "AgroDron T-100", -33.4500, -70.6600, 100, EstadoDron.EN_VUELO)
    )
    val dron: StateFlow<Dron> = _dron.asStateFlow()

    private val _misiones = MutableStateFlow<List<Mision>>(emptyList())
    val misionesFlow: StateFlow<List<Mision>> = _misiones.asStateFlow()

    private val _alertasRemotas = MutableStateFlow<List<Alerta>>(emptyList())
    val alertasRemotas: StateFlow<List<Alerta>> = _alertasRemotas.asStateFlow()

    init {
        sincronizarTodoDesdeXano()
        iniciarTelemetria()
    }

    fun sincronizarTodoDesdeXano() {
        scope.launch {
            try {
                RetrofitClient.api.obtenerDrones().firstOrNull()?.let {
                    _dron.value = it.toDomain()
                }
            } catch (_: Exception) { }

            try {
                _misiones.value = RetrofitClient.api.obtenerMisiones().map { it.toDomain() }
            } catch (_: Exception) { }

            try {
                _alertasRemotas.value = RetrofitClient.api.obtenerAlertas().map { it.toDomain() }
            } catch (_: Exception) { }
        }
    }

    private fun iniciarTelemetria() {
        scope.launch {
            var ciclos = 0
            while (isActive) {
                delay(2000)
                _dron.update { d ->
                    when (d.estado) {
                        EstadoDron.EN_VUELO ->
                            if (d.bateria <= 15) d.copy(estado = EstadoDron.CARGANDO)
                            else d.copy(
                                latitud = d.latitud + 0.0002,
                                longitud = d.longitud + 0.0001,
                                bateria = d.bateria - 1
                            )
                        EstadoDron.CARGANDO ->
                            if (d.bateria >= 100) d.copy(estado = EstadoDron.EN_VUELO)
                            else d.copy(bateria = (d.bateria + 5).coerceAtMost(100))
                    }
                }
                ciclos++
                if (ciclos % 5 == 0) {
                    try {
                        val actual = _dron.value
                        RetrofitClient.api.actualizarDron(
                            id = actual.id.toIntOrNull() ?: 1,
                            request = DronUpdateRequest(
                                latitud = actual.latitud,
                                longitud = actual.longitud,
                                bateria = actual.bateria,
                                estado = actual.estado.name
                            )
                        )
                    } catch (_: Exception) { }
                }
            }
        }
    }

    fun registrarMision(tipo: String, descripcion: String, fecha: String, hectareas: Double) {
        scope.launch {
            try {
                RetrofitClient.api.crearMision(
                    MisionDto(tipo = tipo, descripcion = descripcion, fecha = fecha, hectareas = hectareas)
                )
                _misiones.value = RetrofitClient.api.obtenerMisiones().map { it.toDomain() }
            } catch (_: Exception) { }
        }
    }

    fun registrarAlerta(titulo: String, detalle: String) {
        scope.launch {
            try {
                RetrofitClient.api.crearAlerta(AlertaDto(titulo = titulo, detalle = detalle))
                _alertasRemotas.value = RetrofitClient.api.obtenerAlertas().map { it.toDomain() }
            } catch (_: Exception) { }
        }
    }

    fun obtenerMisiones(): List<Mision> = _misiones.value
}
