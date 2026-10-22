
package com.example.mytos.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val email: String,
    val senhaHash: String
)

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val descricao: String,
    val precoCentavos: Long,
    val categoria: String,
    val disponivel: Boolean = true
)

@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("usuarioId")]
)
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val dataCriacao: Long = System.currentTimeMillis(),
    val status: String = "Pendente",
    val totalCentavos: Long
)

@Entity(
    tableName = "itens_pedido",
    foreignKeys = [
        ForeignKey(
            entity = PedidoEntity::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["id"],
            childColumns = ["produtoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("pedidoId"),
        Index("produtoId")
    ]
)
data class ItemPedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pedidoId: Long,
    val produtoId: Long,
    val quantidade: Int,
    val precoUnitarioCentavos: Long
)