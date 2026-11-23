package com.duoc.agroconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.agroconnect.AppContainer
import com.duoc.agroconnect.model.Alerta
import com.duoc.agroconnect.model.EstadoDron
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class EstadoViewModel : ViewModel() {
    private val repo = AppContainer.dronRepository

    val dron = repo.dron
    val misiones = repo.misionesFlow

    val alertas: StateFlow<List<Alerta>> = combine(repo.dron, repo.alertasRemotas) { d, remotas ->
        buildList {
            if (d.bateria <= 20) add(Alerta("Batería baja", "Queda ${d.bateria}% de batería"))
            if (d.estado == EstadoDron.CARGANDO) add(Alerta("En estación", "El dron está recargando"))
            addAll(remotas)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
