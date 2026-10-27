
package com.example.mytos.data

import com.example.mytos.model.Produto
import java.math.RoundingMode

fun ProdutoEntity.toProduto(): Produto {
    return Produto(
        id = id.toInt(),
        nome = nome,
        descricao = descricao,
        preco = precoCentavos / 100.0,
        categoriaId = when (categoria.lowercase()) {
            "bebidas" -> 1
            "doces" -> 2
            "salgados" -> 3
            else -> 1
        }
    )
}

fun Produto.toProdutoEntity(): ProdutoEntity {
    val precoEmCentavos = (preco * 100)
        .toBigDecimal()
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()

    val nomeCategoria = when (categoriaId) {
        1 -> "Bebidas"
        2 -> "Doces"
        3 -> "Salgados"
        else -> "Bebidas"
    }

    return ProdutoEntity(
        id = id.toLong(),
        nome = nome.trim(),
        descricao = descricao.trim(),
        precoCentavos = precoEmCentavos,
        categoria = nomeCategoria,
        disponivel = true
    )
}
