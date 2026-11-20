package com.duoc.agroconnect.repository

import android.content.Context
import androidx.core.content.edit
import com.duoc.agroconnect.model.Usuario

class AuthRepository(context: Context) {

    private val prefs = context.getSharedPreferences(ARCHIVO_SESION, Context.MODE_PRIVATE)

    private val emailDemo = "agricultor@tecmundo.cl"
    private val claveDemo = "agro1234"

    var usuarioActual: Usuario? = cargarSesion()
        private set

    fun iniciarSesion(email: String, clave: String): Boolean {
        val emailLimpio = email.trim().lowercase()

        // 1. Validar usuario de prueba
        if (emailLimpio == emailDemo && clave == claveDemo) {
            usuarioActual = Usuario(emailDemo, NOMBRE_DEMO)
            prefs.edit {
                putString(CLAVE_EMAIL_SESION, emailDemo)
                putString(CLAVE_NOMBRE_SESION, NOMBRE_DEMO)
            }
            return true
        }

        // 2. Validar usuario registrado en SharedPreferences
        val emailRegistrado = prefs.getString(CLAVE_REG_EMAIL, null)
        val claveRegistrada = prefs.getString(CLAVE_REG_PASS, null)
        val nombreRegistrado = prefs.getString(CLAVE_REG_NOMBRE, "Agricultor") ?: "Agricultor"

        return if (emailLimpio == emailRegistrado && clave == claveRegistrada) {
            usuarioActual = Usuario(emailLimpio, nombreRegistrado)
            prefs.edit {
                putString(CLAVE_EMAIL_SESION, emailLimpio)
                putString(CLAVE_NOMBRE_SESION, nombreRegistrado)
            }
            true
        } else {
            false
        }
    }

    fun registrarUsuario(nombre: String, email: String, clave: String): Boolean {
        val emailLimpio = email.trim().lowercase()
        val nombreLimpio = nombre.trim()

        if (emailLimpio == emailDemo) return false

        prefs.edit {
            putString(CLAVE_REG_NOMBRE, nombreLimpio)
            putString(CLAVE_REG_EMAIL, emailLimpio)
            putString(CLAVE_REG_PASS, clave)
            putString(CLAVE_EMAIL_SESION, emailLimpio)
            putString(CLAVE_NOMBRE_SESION, nombreLimpio)
        }
        usuarioActual = Usuario(emailLimpio, nombreLimpio)
        return true
    }

    fun cerrarSesion() {
        usuarioActual = null
        prefs.edit {
            remove(CLAVE_EMAIL_SESION)
            remove(CLAVE_NOMBRE_SESION)
        }
    }

    private fun cargarSesion(): Usuario? {
        val email = prefs.getString(CLAVE_EMAIL_SESION, null) ?: return null
        val nombre = prefs.getString(CLAVE_NOMBRE_SESION, NOMBRE_DEMO) ?: NOMBRE_DEMO
        return Usuario(email, nombre)
    }

    private companion object {
        const val ARCHIVO_SESION = "sesion"
        const val CLAVE_EMAIL_SESION = "email"
        const val CLAVE_NOMBRE_SESION = "nombre"
        const val CLAVE_REG_NOMBRE = "reg_nombre"
        const val CLAVE_REG_EMAIL = "reg_email"
        const val CLAVE_REG_PASS = "reg_pass"
        const val NOMBRE_DEMO = "Agricultor"
    }
}
