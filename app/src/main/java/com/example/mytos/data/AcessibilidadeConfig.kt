package com.example.mytos.data

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Preferência "Leitura de tela" DENTRO do MYTOS.
 *
 * O Android não permite que um app ligue ou desligue o TalkBack do sistema
 * (isso só é feito pelo usuário nas configurações do aparelho). Por isso o
 * app guarda a própria preferência: quando ativa, aumenta os textos e lê
 * em voz alta o nome de cada tela.
 */
object AcessibilidadeConfig {

    private const val PREFS = "mytos_acessibilidade"
    private const val CHAVE_LEITOR = "leitor_ativo"

    var leitorAtivo by mutableStateOf(false)
        private set

    fun carregar(context: Context) {
        leitorAtivo = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(CHAVE_LEITOR, false)
    }

    fun definir(context: Context, ativo: Boolean) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(CHAVE_LEITOR, ativo)
            .apply()

        leitorAtivo = ativo
    }

    /** true se o TalkBack (ou outro leitor de toque) do Android está ligado. */
    fun talkBackDoSistemaAtivo(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
                as? AccessibilityManager
        return manager?.isTouchExplorationEnabled == true
    }
}