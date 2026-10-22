
package com.example.mytos.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Update
    suspend fun atualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun excluir(usuario: UsuarioEntity)
}

@Dao
interface ProdutoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(produto: ProdutoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(produtos: List<ProdutoEntity>)

    @Query("SELECT * FROM produtos ORDER BY nome ASC")
    fun observarTodos(): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos WHERE categoria = :categoria ORDER BY nome ASC")
    fun observarPorCategoria(categoria: String): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): ProdutoEntity?

    @Update
    suspend fun atualizar(produto: ProdutoEntity)

    @Delete
    suspend fun excluir(produto: ProdutoEntity)
}

@Dao
interface PedidoDao {

    @Insert
    suspend fun inserirPedido(pedido: PedidoEntity): Long

    @Insert
    suspend fun inserirItens(itens: List<ItemPedidoEntity>)

    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY dataCriacao DESC")
    fun observarPedidosDoUsuario(usuarioId: Long): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM itens_pedido WHERE pedidoId = :pedidoId")
    suspend fun buscarItensDoPedido(pedidoId: Long): List<ItemPedidoEntity>

    @Update
    suspend fun atualizarPedido(pedido: PedidoEntity)

    @Query("SELECT * FROM pedidos ORDER BY dataCriacao DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>
}