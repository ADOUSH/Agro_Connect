package com.duoc.agroconnect.repository

import android.content.Context
import androidx.core.content.edit
import com.duoc.agroconnect.model.Usuario

/**
 * Login SIMULADO con un usuario de prueba.
 * La sesión se guarda en SharedPreferences, así sigue iniciada al cerrar la app
 * y solo termina cuando el usuario toca "Salir".
 * Más adelante se cambia por Firebase Auth o una API.
 */
class AuthRepository(context: Context) {

    private val prefs = context.getSharedPreferences(ARCHIVO_SESION, Context.MODE_PRIVATE)

    private val emailValido = "agricultor@tecmundo.cl"
    private val claveValida = "agro1234"

    var usuarioActual: Usuario? = cargarSesion()
        private set

    fun iniciarSesion(email: String, clave: String): Boolean {
        return if (email.trim().lowercase() == emailValido && clave == claveValida) {
            usuarioActual = Usuario(emailValido, NOMBRE_USUARIO)
            prefs.edit { putString(CLAVE_EMAIL, emailValido) }
            true
        } else {
            false
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        prefs.edit { remove(CLAVE_EMAIL) }
    }

    private fun cargarSesion(): Usuario? {
        val email = prefs.getString(CLAVE_EMAIL, null) ?: return null
        return Usuario(email, NOMBRE_USUARIO)
    }

    private companion object {
        const val ARCHIVO_SESION = "sesion"
        const val CLAVE_EMAIL = "email"
        const val NOMBRE_USUARIO = "Agricultor"
    }
}
