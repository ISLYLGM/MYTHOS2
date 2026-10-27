package com.example.mytos.data

import com.example.mytos.model.ItemSacola
import com.example.mytos.model.Pedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date

class PedidoRepository(
    private val pedidoDao: PedidoDao,
    private val produtoDao: ProdutoDao
) {

    fun observarPedidos(usuarioId: Long): Flow<List<Pedido>> {
        return pedidoDao
            .observarPedidosDoUsuario(usuarioId)
            .map { pedidosEntity ->

                pedidosEntity.map { pedidoEntity ->

                    val itensEntity =
                        pedidoDao.buscarItensDoPedido(
                            pedidoEntity.id
                        )

                    val itens = itensEntity.mapNotNull { itemEntity ->

                        val produtoEntity =
                            produtoDao.buscarPorId(
                                itemEntity.produtoId
                            )

                        produtoEntity?.let { entity ->

                            val produto = entity.toProduto().copy(
                                preco = itemEntity
                                    .precoUnitarioCentavos
                                    .toDouble() / 100.0
                            )

                            ItemSacola(
                                produto = produto,
                                quantidade = itemEntity.quantidade
                            )
                        }
                    }

                    Pedido(
                        id = pedidoEntity.id.toString(),
                        itens = itens,
                        valorTotal =
                            pedidoEntity.totalCentavos.toDouble() / 100.0,
                        data = Date(pedidoEntity.dataCriacao)
                    )
                }
            }
    }

    suspend fun buscarPedido(pedidoId: String): Pedido? {
        val id = pedidoId.toLongOrNull() ?: return null

        val pedidoEntity =
            pedidoDao.buscarPedidoPorId(id) ?: return null

        val itensEntity =
            pedidoDao.buscarItensDoPedido(id)

        val itens = itensEntity.mapNotNull { itemEntity ->

            val produtoEntity =
                produtoDao.buscarPorId(itemEntity.produtoId)

            produtoEntity?.let { entity ->

                ItemSacola(
                    produto = entity.toProduto().copy(
                        preco = itemEntity
                            .precoUnitarioCentavos
                            .toDouble() / 100.0
                    ),
                    quantidade = itemEntity.quantidade
                )
            }
        }

        return Pedido(
            id = pedidoEntity.id.toString(),
            itens = itens,
            valorTotal =
                pedidoEntity.totalCentavos.toDouble() / 100.0,
            data = Date(pedidoEntity.dataCriacao)
        )
    }

    suspend fun salvarPedido(
        usuarioId: Long,
        pedido: Pedido
    ): String {
        require(pedido.itens.isNotEmpty()) {
            "Não é possível salvar um pedido sem itens."
        }

        val totalCentavos = converterParaCentavos(
            pedido.valorTotal
        )

        val itensEntity = pedido.itens.map { item ->

            ItemPedidoEntity(
                pedidoId = 0L,
                produtoId = item.produto.id.toLong(),
                quantidade = item.quantidade,
                precoUnitarioCentavos =
                    converterParaCentavos(item.produto.preco)
            )
        }

        val idExistente = pedido.id.toLongOrNull()

        if (idExistente != null) {

            val pedidoExistente =
                pedidoDao.buscarPedidoPorId(idExistente)

            if (
                pedidoExistente != null &&
                pedidoExistente.usuarioId == usuarioId
            ) {
                val pedidoAtualizado = pedidoExistente.copy(
                    dataCriacao = pedido.data.time,
                    totalCentavos = totalCentavos
                )

                pedidoDao.atualizarPedidoCompleto(
                    pedido = pedidoAtualizado,
                    itens = itensEntity
                )

                return idExistente.toString()
            }
        }

        val novoPedido = PedidoEntity(
            usuarioId = usuarioId,
            dataCriacao = pedido.data.time,
            status = "Pendente",
            totalCentavos = totalCentavos
        )

        val novoId = pedidoDao.inserirPedidoCompleto(
            pedido = novoPedido,
            itens = itensEntity
        )

        return novoId.toString()
    }

    suspend fun excluirPedido(
        usuarioId: Long,
        pedidoId: String
    ) {
        val id = pedidoId.toLongOrNull() ?: return

        val pedido = pedidoDao.buscarPedidoPorId(id)
            ?: return

        // Impede excluir o pedido de outra conta.
        if (pedido.usuarioId != usuarioId) {
            return
        }

        pedidoDao.excluirPedido(id)
    }

    private fun converterParaCentavos(
        valor: Double
    ): Long {
        return BigDecimal.valueOf(valor)
            .multiply(BigDecimal.valueOf(100))
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }
}