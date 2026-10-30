package com.duoc.agroconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.agroconnect.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val repo = AppContainer.authRepository

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    /** Devuelve true si el login fue correcto. */
    fun iniciarSesion(email: String, clave: String): Boolean {
        if (email.isBlank() || clave.isBlank()) {
            _error.value = "Completa correo y contraseña"
            return false
        }
        val ok = repo.iniciarSesion(email, clave)
        _error.value = if (ok) "" else "Correo o contraseña incorrectos"
        return ok
    }

    fun cerrarSesion() = repo.cerrarSesion()
}
