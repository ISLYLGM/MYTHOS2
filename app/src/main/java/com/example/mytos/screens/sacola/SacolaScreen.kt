package com.example.mytos.screens.sacola

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mytos.Rotas
import com.example.mytos.data.MytosDatabase
import com.example.mytos.data.PedidoRepository
import com.example.mytos.data.SacolaData
import com.example.mytos.model.Pedido
import com.example.mytos.ui.components.MytosBottomBar
import com.example.mytos.ui.components.SacolaItemCard
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosText
import com.example.mytos.ui.theme.MytosTextSecondary
import com.example.mytos.ui.theme.MytosYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private const val TAG_SACOLA = "SacolaScreen"

// Mesmas cores de fundo da SplashScreen
private val FundoTopo = Color(0xFF3A1A63)
private val FundoMeio = Color(0xFF2A1248)
private val FundoBase = Color(0xFF1B0B33)
private val TextoClaro = Color(0xFFFBF3E4)

@Composable
fun SacolaScreen(
    navController: NavController,
    onPedidoFinalizado: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val repository = remember(context) {
        val db = MytosDatabase.getDatabase(context.applicationContext)
        PedidoRepository(
            pedidoDao = db.pedidoDao(),
            produtoDao = db.produtoDao()
        )
    }

    val itens = SacolaData.itensSacola

    var salvando by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(FundoTopo, FundoMeio, FundoBase)
                )
            )
    ) {

        // Montanhas decorativas (sem animação)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val montanhaTras = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.72f)
                lineTo(w * 0.18f, h * 0.58f)
                lineTo(w * 0.35f, h * 0.72f)
                lineTo(w * 0.52f, h * 0.55f)
                lineTo(w * 0.70f, h * 0.70f)
                lineTo(w * 0.86f, h * 0.58f)
                lineTo(w, h * 0.68f)
                lineTo(w, h)
                close()
            }
            drawPath(
                path = montanhaTras,
                color = Color.White.copy(alpha = 0.05f)
            )

            val montanhaFrente = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.84f)
                lineTo(w * 0.22f, h * 0.72f)
                lineTo(w * 0.42f, h * 0.84f)
                lineTo(w * 0.62f, h * 0.70f)
                lineTo(w * 0.80f, h * 0.82f)
                lineTo(w, h * 0.74f)
                lineTo(w, h)
                close()
            }
            drawPath(
                path = montanhaFrente,
                color = Color.White.copy(alpha = 0.07f)
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { MytosBottomBar(navController = navController) }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {

                Text(
                    text = "Minha sacola",
                    color = TextoClaro,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
                )

                if (itens.isEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sua sacola está vazia",
                            color = TextoClaro,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Adicione produtos pelo cardápio.",
                            color = TextoClaro.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                navController.navigate(Rotas.Cardapio) {
                                    launchSingleTop = true
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MytosYellow,
                                contentColor = MytosPurpleDark
                            )
                        ) {
                            Text("Ver cardápio")
                        }
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = itens,
                            key = { it.produto.id }
                        ) { item ->
                            Box(
                                modifier = Modifier.background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            ) {
                                SacolaItemCard(
                                    itemSacola = item,
                                    onAumentar = {
                                        SacolaData.aumentarQuantidade(item.produto.id)
                                    },
                                    onDiminuir = {
                                        SacolaData.diminuirQuantidade(item.produto.id)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total",
                                    color = MytosText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = formatarMoeda(
                                        SacolaData.calcularTotal()
                                    ),
                                    color = MytosPurple,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (erro != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = erro.orEmpty(),
                                    color = Color(0xFFD94C59),
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val usuarioId = SacolaData.obterUsuarioAtualId()
                                    val itensAtuais = itens.toList()

                                    if (usuarioId == null || itensAtuais.isEmpty()) {
                                        erro = "Não foi possível identificar o usuário ou a sacola está vazia."
                                    } else if (!salvando) {
                                        salvando = true
                                        erro = null

                                        // Id vazio: o repositório cria um pedido novo
                                        val pedido = Pedido(
                                            id = "",
                                            itens = itensAtuais,
                                            valorTotal = SacolaData.calcularTotal()
                                        )

                                        scope.launch {
                                            try {
                                                // Grava o pedido no Room (Dispatchers.IO)
                                                val novoId = withContext(Dispatchers.IO) {
                                                    repository.salvarPedido(
                                                        usuarioId = usuarioId,
                                                        pedido = pedido
                                                    )
                                                }

                                                // Só limpa a sacola depois de salvar com sucesso
                                                SacolaData.limparSacolaAtual()
                                                onPedidoFinalizado(novoId)
                                            } catch (e: Exception) {
                                                Log.e(
                                                    TAG_SACOLA,
                                                    "Erro ao salvar pedido",
                                                    e
                                                )
                                                erro = "Não foi possível finalizar o pedido. Tente novamente."
                                            } finally {
                                                salvando = false
                                            }
                                        }
                                    }
                                },
                                enabled = !salvando,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MytosYellow,
                                    contentColor = MytosPurpleDark
                                )
                            ) {
                                Text(
                                    text = if (salvando) {
                                        "SALVANDO..."
                                    } else {
                                        "FINALIZAR PEDIDO"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

private fun formatarMoeda(valor: Double): String =
    String.format(Locale("pt", "BR"), "R$ %.2f", valor)