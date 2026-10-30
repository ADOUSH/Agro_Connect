package com.duoc.agroconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.agroconnect.AppContainer
import com.duoc.agroconnect.model.Alerta
import com.duoc.agroconnect.model.Dron
import com.duoc.agroconnect.model.EstadoDron
import com.duoc.agroconnect.model.Mision
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EstadoViewModel : ViewModel() {
    private val repo = AppContainer.dronRepository

    val dron: StateFlow<Dron> = repo.dron
    val misiones: List<Mision> = repo.obtenerMisiones()

    val alertas: StateFlow<List<Alerta>> = repo.dron
        .map { d ->
            buildList {
                if (d.bateria <= 20) {
                    add(Alerta("Batería baja", "${d.nombre} tiene ${d.bateria}% de batería"))
                }
                if (d.estado == EstadoDron.CARGANDO) {
                    add(Alerta("Dron cargando", "${d.nombre} volverá al vuelo al llegar a 100%"))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
