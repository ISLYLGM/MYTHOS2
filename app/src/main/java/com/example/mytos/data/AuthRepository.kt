package com.example.mytos.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val sessaoManager: SessaoManager
) {
    suspend fun cadastrar(nome: String, email: String, senha: String): Long =
        withContext(Dispatchers.IO) {
            val nomeLimpo = nome.trim()
            val emailNormalizado = email.trim().lowercase()

            require(nomeLimpo.length >= 3) {
                "Digite um nome com pelo menos 3 caracteres."
            }

            require(
                emailNormalizado.matches(
                    Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                )
            ) {
                "Digite um e-mail válido."
            }

            require(
                senha.length >= 6 &&
                        senha.any { it.isLetter() } &&
                        senha.any { it.isDigit() }
            ) {
                "A senha precisa ter pelo menos 6 caracteres, uma letra e um número."
            }

            if (usuarioDao.buscarPorEmail(emailNormalizado) != null) {
                throw IllegalArgumentException("Este e-mail já está cadastrado.")
            }

            try {
                usuarioDao.inserir(
                    UsuarioEntity(
                        nome = nomeLimpo,
                        email = emailNormalizado,
                        senhaHash = SenhaUtils.gerarHash(senha)
                    )
                )
            } catch (e: Exception) {
                if (usuarioDao.buscarPorEmail(emailNormalizado) != null) {
                    throw IllegalArgumentException("Este e-mail já está cadastrado.")
                }
                throw e
            }
        }

    suspend fun entrar(email: String, senha: String): UsuarioEntity =
        withContext(Dispatchers.IO) {
            val emailNormalizado = email.trim().lowercase()

            val usuario = usuarioDao.buscarPorEmail(emailNormalizado)
                ?: throw IllegalArgumentException("E-mail ou senha incorretos.")

            if (!SenhaUtils.verificarSenha(senha, usuario.senhaHash)) {
                throw IllegalArgumentException("E-mail ou senha incorretos.")
            }

            sessaoManager.salvarUsuarioLogado(usuario.id)
            usuario
        }

    suspend fun obterUsuarioLogado(): UsuarioEntity? =
        withContext(Dispatchers.IO) {
            val id = sessaoManager.obterUsuarioLogado()
                ?: return@withContext null

            usuarioDao.buscarPorId(id)
        }

    suspend fun atualizarPerfil(usuario: UsuarioEntity) =
        withContext(Dispatchers.IO) {
            val emailNormalizado = usuario.email.trim().lowercase()
            val emailExistente = usuarioDao.buscarPorEmail(emailNormalizado)

            if (emailExistente != null && emailExistente.id != usuario.id) {
                throw IllegalArgumentException(
                    "Este e-mail já está sendo usado por outra conta."
                )
            }

            usuarioDao.atualizar(
                usuario.copy(
                    nome = usuario.nome.trim(),
                    email = emailNormalizado
                )
            )
        }

    fun sair() {
        sessaoManager.encerrarSessao()
    }
}