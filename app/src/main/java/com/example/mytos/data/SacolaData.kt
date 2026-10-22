
package com.example.mytos.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mytos.model.ItemSacola
import com.example.mytos.model.Pedido
import com.example.mytos.model.Produto
import java.util.UUID

object SacolaData {

    // ID da conta atualmente conectada.
    private var usuarioAtualId by mutableStateOf<Long?>(null)

    // Cada usuário possui sua própria sacola.
    private val sacolas =
        mutableStateMapOf<Long, SnapshotStateList<ItemSacola>>()

    // Cada usuário possui sua própria lista de pedidos.
    private val pedidosPorUsuario =
        mutableStateMapOf<Long, SnapshotStateList<Pedido>>()

    // Mantém compatibilidade com a SacolaScreen atual.
    val itensSacola: SnapshotStateList<ItemSacola>
        get() = obterSacolaAtual()

    val pedidos: SnapshotStateList<Pedido>
        get() = obterPedidosAtuais()

    /**
     * Deve ser chamado quando uma conta entra no aplicativo.
     * Passe o ID real do usuário salvo no banco de dados.
     */
    fun definirUsuarioAtual(usuarioId: Long?) {
        usuarioAtualId = usuarioId
    }

    private fun obterSacolaAtual(): SnapshotStateList<ItemSacola> {
        val id = usuarioAtualId ?: return mutableStateListOf()

        return sacolas.getOrPut(id) {
            mutableStateListOf()
        }
    }

    private fun obterPedidosAtuais(): SnapshotStateList<Pedido> {
        val id = usuarioAtualId ?: return mutableStateListOf()

        return pedidosPorUsuario.getOrPut(id) {
            mutableStateListOf()
        }
    }

    fun adicionarProduto(produto: Produto) {
        if (usuarioAtualId == null) return

        val itens = obterSacolaAtual()
        val index = itens.indexOfFirst {
            it.produto.id == produto.id
        }

        if (index != -1) {
            val itemAtual = itens[index]
            itens[index] = itemAtual.copy(
                quantidade = itemAtual.quantidade + 1
            )
        } else {
            itens.add(
                ItemSacola(
                    produto = produto,
                    quantidade = 1
                )
            )
        }
    }

    fun aumentarQuantidade(produtoId: Int) {
        if (usuarioAtualId == null) return

        val itens = obterSacolaAtual()
        val index = itens.indexOfFirst {
            it.produto.id == produtoId
        }

        if (index != -1) {
            val itemAtual = itens[index]
            itens[index] = itemAtual.copy(
                quantidade = itemAtual.quantidade + 1
            )
        }
    }

    fun diminuirQuantidade(produtoId: Int) {
        if (usuarioAtualId == null) return

        val itens = obterSacolaAtual()
        val index = itens.indexOfFirst {
            it.produto.id == produtoId
        }

        if (index != -1) {
            val itemAtual = itens[index]
            val novaQuantidade = itemAtual.quantidade - 1

            if (novaQuantidade > 0) {
                itens[index] = itemAtual.copy(
                    quantidade = novaQuantidade
                )
            } else {
                itens.removeAt(index)
            }
        }
    }

    fun calcularTotal(): Double {
        return obterSacolaAtual().sumOf { it.subtotal }
    }

    fun finalizarPedido(): Pedido? {
        val idUsuario = usuarioAtualId ?: return null
        val itens = obterSacolaAtual()

        if (itens.isEmpty()) return null

        val novoPedido = Pedido(
            id = UUID.randomUUID().toString().take(8),
            itens = itens.toList(),
            valorTotal = calcularTotal()
        )

        val listaPedidos = pedidosPorUsuario.getOrPut(idUsuario) {
            mutableStateListOf()
        }

        listaPedidos.add(novoPedido)

        // Limpa apenas a sacola da conta atual.
        itens.clear()

        return novoPedido
    }

    /**
     * Limpa somente a sacola da conta atual.
     */
    fun limparSacolaAtual() {
        obterSacolaAtual().clear()
    }
}