package com.example.mytos.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun inserir(usuario: UsuarioEntity): Long

    @Update
    suspend fun atualizar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :usuarioId LIMIT 1")
    suspend fun buscarPorId(usuarioId: Long): UsuarioEntity?
}

@Dao
interface ProdutoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(produto: ProdutoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(produtos: List<ProdutoEntity>)

    @Update
    suspend fun atualizar(produto: ProdutoEntity)

    @Query("SELECT * FROM produtos ORDER BY nome ASC")
    fun observarTodos(): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos WHERE id = :produtoId")
    suspend fun buscarPorId(produtoId: Long): ProdutoEntity?

    @Query("SELECT * FROM produtos WHERE categoria = :categoria")
    fun observarPorCategoria(categoria: String): Flow<List<ProdutoEntity>>

    @Query("SELECT COUNT(*) FROM produtos")
    suspend fun contarProdutos(): Int

    @Query("DELETE FROM produtos WHERE id = :produtoId")
    suspend fun excluir(produtoId: Long)

    @Query("DELETE FROM produtos")
    suspend fun excluirTodos()
}

@Dao
abstract class PedidoDao {

    @Insert
    abstract suspend fun inserirPedido(pedido: PedidoEntity): Long

    @Insert
    abstract suspend fun inserirItens(itens: List<ItemPedidoEntity>)

    @Update
    abstract suspend fun atualizarPedido(pedido: PedidoEntity)

    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY dataCriacao DESC")
    abstract fun observarPedidosDoUsuario(usuarioId: Long): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE id = :pedidoId")
    abstract suspend fun buscarPedidoPorId(pedidoId: Long): PedidoEntity?

    @Query("SELECT * FROM itens_pedido WHERE pedidoId = :pedidoId")
    abstract suspend fun buscarItensDoPedido(pedidoId: Long): List<ItemPedidoEntity>

    @Query("DELETE FROM itens_pedido WHERE pedidoId = :pedidoId")
    abstract suspend fun excluirItensDoPedido(pedidoId: Long)

    @Query("DELETE FROM pedidos WHERE id = :pedidoId")
    abstract suspend fun excluirPedido(pedidoId: Long)

    @Transaction
    open suspend fun inserirPedidoCompleto(
        pedido: PedidoEntity,
        itens: List<ItemPedidoEntity>
    ): Long {
        val novoId = inserirPedido(pedido)
        inserirItens(itens.map { it.copy(pedidoId = novoId) })
        return novoId
    }

    @Transaction
    open suspend fun atualizarPedidoCompleto(
        pedido: PedidoEntity,
        itens: List<ItemPedidoEntity>
    ) {
        atualizarPedido(pedido)
        excluirItensDoPedido(pedido.id)
        inserirItens(itens.map { it.copy(pedidoId = pedido.id) })
    }
}