package com.example.mytos.screens.produto

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mytos.Rotas
import com.example.mytos.data.MytosDatabase
import com.example.mytos.data.SacolaData
import com.example.mytos.data.toProduto
import com.example.mytos.model.Produto
import com.example.mytos.ui.components.MytosBottomBar
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosLilac
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosText
import com.example.mytos.ui.theme.MytosTextSecondary
import com.example.mytos.ui.theme.MytosYellow
import com.example.mytos.ui.theme.MytosYellowLight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

private const val TAG_PRODUTO = "ProdutoDetalhes"

@Composable
fun ProdutoDetalhesScreen(
    navController: NavController,
    produtoId: Long
) {
    val context = LocalContext.current
    val produtoDao = remember(context) {
        MytosDatabase.getDatabase(context.applicationContext).produtoDao()
    }

    var produto by remember { mutableStateOf<Produto?>(null) }
    var carregando by remember { mutableStateOf(true) }
    var quantidade by remember { mutableStateOf(1) }
    var adicionado by remember { mutableStateOf(false) }

    // Busca o produto DIRETO NO BANCO pelo id recebido na rota
    LaunchedEffect(produtoId) {
        carregando = true
        try {
            produto = withContext(Dispatchers.IO) {
                produtoDao.buscarPorId(produtoId)?.toProduto()
            }
        } catch (e: Exception) {
            Log.e(TAG_PRODUTO, "Erro ao buscar produto $produtoId", e)
            produto = null
        } finally {
            carregando = false
        }
    }

    Scaffold(
        containerColor = MytosCream,
        bottomBar = { MytosBottomBar(navController = navController) }
    ) { paddingValues ->

        val produtoAtual = produto

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

            produtoAtual == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(28.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Produto não encontrado",
                        color = MytosPurpleDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Ele pode ter sido removido do cardápio.",
                        color = MytosTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MytosPurple,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Voltar")
                    }
                }
            }

            else -> {
                val categoria = when (produtoAtual.categoriaId) {
                    2 -> "Doces"
                    3 -> "Salgados"
                    else -> "Bebidas"
                }
                val iconeCategoria: ImageVector = when (produtoAtual.categoriaId) {
                    2 -> Icons.Default.Cake
                    3 -> Icons.Default.Restaurant
                    else -> Icons.Default.Coffee
                }
                val subtotal = produtoAtual.preco * quantidade

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {

                    // ===== CABEÇALHO =====
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .background(
                                color = MytosPurpleDark,
                                shape = RoundedCornerShape(
                                    bottomStart = 50.dp,
                                    bottomEnd = 50.dp
                                )
                            )
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }

                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(MytosYellowLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconeCategoria,
                                    contentDescription = null,
                                    tint = MytosPurple,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = categoria.uppercase(),
                                color = MytosYellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // ===== CONTEÚDO =====
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {

                        Text(
                            text = produtoAtual.nome,
                            color = MytosPurpleDark,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = moeda(produtoAtual.preco),
                            color = MytosPurple,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = produtoAtual.descricao,
                            color = MytosTextSecondary,
                            fontSize = 16.sp,
                            lineHeight = 23.sp
                        )

                        Spacer(Modifier.height(24.dp))

                        // Quantidade + subtotal calculado
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 3.dp
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Quantidade",
                                        color = MytosText,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Row(
                                        modifier = Modifier.background(
                                            color = MytosLilac,
                                            shape = RoundedCornerShape(50)
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (quantidade > 1) quantidade--
                                                adicionado = false
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Diminuir quantidade",
                                                tint = MytosPurple
                                            )
                                        }

                                        Text(
                                            text = quantidade.toString(),
                                            color = MytosPurpleDark,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(28.dp)
                                        )

                                        IconButton(
                                            onClick = {
                                                if (quantidade < 20) quantidade++
                                                adicionado = false
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Aumentar quantidade",
                                                tint = MytosPurple
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Subtotal",
                                        color = MytosTextSecondary,
                                        fontSize = 15.sp
                                    )

                                    Text(
                                        text = moeda(subtotal),
                                        color = MytosPurple,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                repeat(quantidade) {
                                    SacolaData.adicionarProduto(produtoAtual)
                                }
                                adicionado = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(19.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MytosYellow,
                                contentColor = MytosPurpleDark
                            )
                        ) {
                            Text(
                                text = if (adicionado) {
                                    "ADICIONADO À SACOLA ✓"
                                } else {
                                    "ADICIONAR À SACOLA"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                navController.navigate(Rotas.Sacola) {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(19.dp)
                        ) {
                            Text(
                                text = "VER SACOLA",
                                color = MytosPurple,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun moeda(valor: Double): String =
    String.format(Locale("pt", "BR"), "R$ %.2f", valor)