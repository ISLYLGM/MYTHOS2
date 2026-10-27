package com.example.mytos.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mytos.model.ItemSacola
import com.example.mytos.model.Pedido
import com.example.mytos.model.Produto

/**
 * Guarda apenas a SACOLA (carrinho) em memória e o usuário logado.
 * Os PEDIDOS agora vivem no Room (veja PedidoRepository).
 */
object SacolaData {

    private var usuarioAtualId by mutableStateOf<Long?>(null)

    private val sacolas =
        mutableStateMapOf<Long, SnapshotStateList<ItemSacola>>()

    val itensSacola: SnapshotStateList<ItemSacola>
        get() = obterSacolaAtual()

    fun definirUsuarioAtual(usuarioId: Long?) {
        usuarioAtualId = usuarioId
    }

    fun obterUsuarioAtualId(): Long? = usuarioAtualId

    private fun obterSacolaAtual(): SnapshotStateList<ItemSacola> {
        val id = usuarioAtualId ?: return mutableStateListOf()
        return sacolas.getOrPut(id) { mutableStateListOf() }
    }

    fun adicionarProduto(produto: Produto) {
        if (usuarioAtualId == null) return

        val itens = obterSacolaAtual()
        val index = itens.indexOfFirst { it.produto.id == produto.id }

        if (index != -1) {
            val atual = itens[index]
            itens[index] = atual.copy(quantidade = atual.quantidade + 1)
        } else {
            itens.add(ItemSacola(produto = produto, quantidade = 1))
        }
    }

    fun aumentarQuantidade(produtoId: Int) {
        if (usuarioAtualId == null) return

        val itens = obterSacolaAtual()
        val index = itens.indexOfFirst { it.produto.id == produtoId }

        if (index != -1) {
            val atual = itens[index]
            itens[index] = atual.copy(quantidade = atual.quantidade + 1)
        }
    }

    fun diminuirQuantidade(produtoId: Int) {
        if (usuarioAtualId == null) return

        val itens = obterSacolaAtual()
        val index = itens.indexOfFirst { it.produto.id == produtoId }

        if (index != -1) {
            val atual = itens[index]
            val novaQuantidade = atual.quantidade - 1

            if (novaQuantidade > 0) {
                itens[index] = atual.copy(quantidade = novaQuantidade)
            } else {
                itens.removeAt(index)
            }
        }
    }

    fun removerProduto(produtoId: Int) {
        if (usuarioAtualId == null) return
        obterSacolaAtual().removeAll { it.produto.id == produtoId }
    }

    fun calcularTotal(): Double = obterSacolaAtual().sumOf { it.subtotal }

    /**
     * Monta o pedido a partir da sacola, SEM salvar.
     * Quem salva é o PedidoRepository (Room), e só depois a sacola é limpa.
     * O id vazio faz o repositório criar um pedido novo.
     */
    fun montarPedido(): Pedido? {
        val itens = obterSacolaAtual()
        if (itens.isEmpty()) return null

        return Pedido(
            id = "",
            itens = itens.toList(),
            valorTotal = calcularTotal()
        )
    }

    fun limparSacolaAtual() {
        obterSacolaAtual().clear()
    }
}