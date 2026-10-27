package com.example.mytos.screens.pedidos

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mytos.data.MytosDatabase
import com.example.mytos.data.PedidoRepository
import com.example.mytos.model.Pedido
import com.example.mytos.ui.components.MytosBottomBar
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG_DETALHES = "PedidoDetalhes"

@Composable
fun PedidoDetalhesScreen(
    navController: NavController,
    pedidoId: String
) {
    val context = LocalContext.current

    val repository = remember(context) {
        val db = MytosDatabase.getDatabase(context.applicationContext)
        PedidoRepository(
            pedidoDao = db.pedidoDao(),
            produtoDao = db.produtoDao()
        )
    }

    var pedido by remember { mutableStateOf<Pedido?>(null) }
    var carregando by remember { mutableStateOf(true) }

    // Busca o pedido DIRETO NO BANCO pelo id recebido na rota (seção 3.3)
    LaunchedEffect(pedidoId) {
        carregando = true
        try {
            pedido = withContext(Dispatchers.IO) {
                repository.buscarPedido(pedidoId)
            }
        } catch (e: Exception) {
            Log.e(TAG_DETALHES, "Erro ao buscar pedido $pedidoId", e)
            pedido = null
        } finally {
            carregando = false
        }
    }

    Scaffold(
        containerColor = MytosCream,
        bottomBar = { MytosBottomBar(navController = navController) }
    ) { paddingValues ->

        val pedidoAtual = pedido

        when {
            carregando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MytosPurple)
                }
            }

            pedidoAtual == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pedido não encontrado.",
                        color = MytosText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Volte para Meus Pedidos e tente novamente.",
                        color = MytosText.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(16.dp))

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MytosPurple
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = MytosPurple
                            )
                        }

                        Text(
                            text = "Detalhes do Pedido",
                            color = MytosPurpleDark,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Pedido #${pedidoAtual.id}",
                        color = MytosPurple,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy • HH:mm",
                            Locale("pt", "BR")
                        ).format(pedidoAtual.data),
                        color = MytosText.copy(alpha = 0.65f),
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = pedidoAtual.itens,
                            key = { "${pedidoAtual.id}-${it.produto.id}" }
                        ) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = item.produto.nome,
                                        color = MytosPurpleDark,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = "Quantidade: ${item.quantidade}",
                                        color = MytosText,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        text = "Preço unitário: " + moeda(item.produto.preco),
                                        color = MytosText.copy(alpha = 0.7f),
                                        fontSize = 13.sp
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = "Subtotal: " + moeda(item.subtotal),
                                        color = MytosPurple,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MytosPurple
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = moeda(pedidoAtual.valorTotal),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

private fun moeda(valor: Double): String =
    String.format(Locale("pt", "BR"), "R$ %.2f", valor)