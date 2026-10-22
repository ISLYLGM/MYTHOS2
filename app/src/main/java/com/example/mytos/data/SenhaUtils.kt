package com.example.mytos.data

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object SenhaUtils {

    private const val ITERACOES = 120_000
    private const val TAMANHO_SALT = 16
    private const val TAMANHO_HASH = 256

    fun gerarHash(senha: String): String {
        val salt = ByteArray(TAMANHO_SALT)
        SecureRandom().nextBytes(salt)

        val spec = javax.crypto.spec.PBEKeySpec(
            senha.toCharArray(),
            salt,
            ITERACOES,
            TAMANHO_HASH
        )

        val hash = javax.crypto.SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded

        spec.clearPassword()

        return Base64.encodeToString(salt, Base64.NO_WRAP) +
                ":" +
                Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verificarSenha(
        senhaDigitada: String,
        hashArmazenado: String
    ): Boolean {
        return try {
            val partes = hashArmazenado.split(":")

            if (partes.size != 2) return false

            val salt = Base64.decode(partes[0], Base64.NO_WRAP)
            val hashOriginal = Base64.decode(partes[1], Base64.NO_WRAP)

            val spec = javax.crypto.spec.PBEKeySpec(
                senhaDigitada.toCharArray(),
                salt,
                ITERACOES,
                hashOriginal.size * 8
            )

            val hashDigitado = javax.crypto.SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded

            spec.clearPassword()

            MessageDigest.isEqual(hashOriginal, hashDigitado)
        } catch (_: Exception) {
            false
        }
    }
}

