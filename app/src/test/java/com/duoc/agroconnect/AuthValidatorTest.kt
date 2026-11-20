package com.duoc.agroconnect

import com.duoc.agroconnect.repository.AuthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun validarRegistro_datosValidos_retornaTrue() {
        val resultado = AuthValidator.validarRegistro(
            nombre = "Camila Soto",
            email = "camila@agroconnect.cl",
            clave = "password123",
            confirmarClave = "password123"
        )

        assertTrue(resultado.esValido)
        assertEquals("", resultado.mensajeError)
    }

    @Test
    fun validarRegistro_emailInvalidoOClavesDistintas_retornaFalse() {
        val emailInvalido = AuthValidator.validarRegistro(
            nombre = "Camila Soto",
            email = "camila-sin-arroba.cl",
            clave = "password123",
            confirmarClave = "password123"
        )
        val clavesDistintas = AuthValidator.validarRegistro(
            nombre = "Camila Soto",
            email = "camila@agroconnect.cl",
            clave = "password123",
            confirmarClave = "otraClave456"
        )

        assertFalse(emailInvalido.esValido)
        assertFalse(clavesDistintas.esValido)
        assertEquals("Las contraseñas no coinciden", clavesDistintas.mensajeError)
    }
}