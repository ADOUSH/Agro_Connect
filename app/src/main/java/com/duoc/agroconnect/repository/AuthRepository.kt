package com.duoc.agroconnect.repository

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.duoc.agroconnect.model.Usuario
import com.duoc.agroconnect.network.RegistroRequest
import com.duoc.agroconnect.network.RetrofitClient

class AuthRepository(context: Context) {

    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    var usuarioActual: Usuario? = cargarSesion()
        private set

    suspend fun iniciarSesionApi(email: String, clave: String): Boolean {
        val emailLimpio = email.trim().lowercase()
        return try {
            val usuarios = RetrofitClient.api.obtenerUsuarios()
            val encontrado = usuarios.find { it.email.trim().lowercase() == emailLimpio }
            if (encontrado != null) {
                guardarSesion(encontrado.toDomain())
                true
            } else if (emailLimpio == "agricultor@tecmundo.cl" && clave == "agro1234") {
                guardarSesion(Usuario(emailLimpio, "Agricultor"))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("XANO_AUTH", "Error login: ${e.message}", e)
            if (emailLimpio == "agricultor@tecmundo.cl" && clave == "agro1234") {
                guardarSesion(Usuario(emailLimpio, "Agricultor"))
                true
            } else {
                false
            }
        }
    }

    suspend fun registrarUsuarioApi(nombre: String, email: String, clave: String): Boolean {
        val emailLimpio = email.trim().lowercase()
        val nombreLimpio = nombre.trim()
        return try {
            val existentes = RetrofitClient.api.obtenerUsuarios()
            if (existentes.any { it.email.trim().lowercase() == emailLimpio }) {
                return false
            }
            val creado = RetrofitClient.api.registrar(
                RegistroRequest(email = emailLimpio, nombre = nombreLimpio)
            )
            guardarSesion(creado.toDomain())
            true
        } catch (e: Exception) {
            Log.e("XANO_AUTH", "Error registro: ${e.message}", e)
            false
        }
    }

    private fun guardarSesion(usuario: Usuario) {
        usuarioActual = usuario
        prefs.edit {
            putString("email", usuario.email)
            putString("nombre", usuario.nombre)
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        prefs.edit {
            remove("email")
            remove("nombre")
        }
    }

    private fun cargarSesion(): Usuario? {
        val email = prefs.getString("email", null) ?: return null
        val nombre = prefs.getString("nombre", "Agricultor") ?: "Agricultor"
        return Usuario(email, nombre)
    }
}
