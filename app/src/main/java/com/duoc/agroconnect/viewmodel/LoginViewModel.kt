package com.duoc.agroconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.agroconnect.AppContainer
import com.duoc.agroconnect.repository.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val repo = AppContainer.authRepository

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    fun iniciarSesion(email: String, clave: String): Boolean {
        if (email.isBlank() || clave.isBlank()) {
            _error.value = "Completa correo y contraseña"
            return false
        }
        val ok = repo.iniciarSesion(email, clave)
        _error.value = if (ok) "" else "Correo o contraseña incorrectos"
        return ok
    }

    fun registrar(nombre: String, email: String, clave: String, confirmarClave: String): Boolean {
        val validacion = AuthValidator.validarRegistro(nombre, email, clave, confirmarClave)
        if (!validacion.esValido) {
            _error.value = validacion.mensajeError
            return false
        }
        val creado = repo.registrarUsuario(nombre, email, clave)
        _error.value = if (creado) "" else "Ese correo ya está en uso"
        return creado
    }

    fun limpiarError() {
        _error.value = ""
    }

    fun cerrarSesion() = repo.cerrarSesion()
}
