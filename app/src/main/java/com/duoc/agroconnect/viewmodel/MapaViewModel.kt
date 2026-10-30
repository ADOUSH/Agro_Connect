package com.duoc.agroconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.agroconnect.AppContainer
import com.duoc.agroconnect.model.Dron
import kotlinx.coroutines.flow.StateFlow

class MapaViewModel : ViewModel() {
    val dron: StateFlow<Dron> = AppContainer.dronRepository.dron
}
