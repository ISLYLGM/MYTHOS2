package com.example.mytos.screens.pedidos

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mytos.Rotas
import com.example.mytos.data.MytosDatabase
import com.example.mytos.data.PedidoRepository
import com.example.mytos.data.SacolaData
import com.example.mytos.data.produtos
import com.example.mytos.data.toProdutoEntity
import com.example.mytos.model.ItemSacola
import com.example.mytos.model.Pedido
import com.example.mytos.ui.components.MytosBottomBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

private val MytosPurple = Color(0xFF744B8F)
private val MytosPurpleDark = Color(0xFF432A55)
private val MytosCream = Color(0xFFFFF9F2)
private val MytosText = Color(0xFF302736)

@Composable
fun PedidosScreen(navController: NavController) {
    val context = LocalContext.current
    val database = remember(context) {
        MytosDatabase.getDatabase(context)
    }
    val repository = remember(database) {
        PedidoRepository(
            pedidoDao = database.pedidoDao(),
            produtoDao = database.produtoDao()
        )
    }

    val usuarioId = SacolaData.obterUsuarioAtualId()
    val scope = rememberCoroutineScope()

    var mostrarDialogoAdicionar by remember {
        mutableStateOf(false)
    }
    var mostrarDialogoEditar by remember {
        mutableStateOf(false)
    }
    var pedidoSelecionado by remember {
        mutableStateOf<Pedido?>(null)
    }
    var erroOperacao by remember {
        mutableStateOf<String?>(null)
    }

    val pedidosFlow = remember(repository, usuarioId) {
        if (usuarioId != null) {
            repository.observarPedidos(usuarioId)
        } else {
            flowOf(emptyList())
        }
    }

    val pedidos by pedidosFlow.collectAsState(
        initial = emptyList()
    )

    // Cadastra os produtos iniciais no Room somente
    // quando a tabela de produtos ainda estiver vazia.
    LaunchedEffect(database) {
        try {
            withContext(Dispatchers.IO) {
                val produtoDao = database.produtoDao()

                if (produtoDao.contarProdutos() == 0) {
                    produtoDao.inserirTodos(
                        produtos.map { it.toProdutoEntity() }
                    )
                }
            }
        } catch (e: Exception) {
            erroOperacao =
                "Não foi possível preparar os produtos: " +
                        (e.localizedMessage ?: "erro desconhecido")
        }
    }

    Scaffold(
        containerColor = MytosCream,
        bottomBar = {
            MytosBottomBar(navController = navController)
        }
    ) { paddingValues ->
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
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = MytosPurple
                    )
                }

                Text(
                    text = "Meus Pedidos",
                    color = MytosPurpleDark,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (usuarioId == null) {
                        erroOperacao =
                            "Não foi possível identificar o usuário logado."
                    } else {
                        mostrarDialogoAdicionar = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MytosPurple
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar pedido"
                )

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                Text(
                    text = "Novo pedido",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (usuarioId == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Entre na sua conta para ver os pedidos.",
                        color = MytosText.copy(alpha = 0.65f),
                        fontSize = 16.sp
                    )
                }
            } else if (pedidos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Você ainda não fez nenhum pedido.",
                        color = MytosText.copy(alpha = 0.65f),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = pedidos,
                        key = { it.id }
                    ) { pedido ->
                        val quantidadeItens =
                            pedido.itens.sumOf { it.quantidade }

                        val dataFormatada = remember(pedido.data) {
                            SimpleDateFormat(
                                "dd/MM/yyyy • HH:mm",
                                Locale("pt", "BR")
                            ).format(pedido.data)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        "${Rotas.PedidoDetalhes}/${pedido.id}"
                                    )
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween,
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Pedido #${pedido.id}",
                                            color = MytosPurpleDark,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(
                                            modifier = Modifier.height(6.dp)
                                        )

                                        Text(
                                            text = dataFormatada,
                                            color = MytosText.copy(
                                                alpha = 0.65f
                                            ),
                                            fontSize = 13.sp
                                        )
                                    }

                                    Text(
                                        text = "R$ " + String.format(
                                            Locale("pt", "BR"),
                                            "%.2f",
                                            pedido.valorTotal
                                        ),
                                        color = MytosPurple,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment =
                                        Alignment.CenterVertically,
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$quantidadeItens item(ns)",
                                        color = MytosText,
                                        fontSize = 14.sp
                                    )

                                    Row {
                                        IconButton(
                                            onClick = {
                                                pedidoSelecionado = pedido
                                                mostrarDialogoEditar = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector =
                                                    Icons.Default.Edit,
                                                contentDescription =
                                                    "Editar pedido",
                                                tint = MytosPurple
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val idUsuario = usuarioId
                                                if (idUsuario != null) {
                                                    scope.launch {
                                                        try {
                                                            repository.excluirPedido(
                                                                usuarioId = idUsuario,
                                                                pedidoId = pedido.id
                                                            )
                                                        } catch (e: Exception) {
                                                            erroOperacao =
                                                                "Não foi possível excluir o pedido: " +
                                                                        (e.localizedMessage
                                                                            ?: "erro desconhecido")
                                                        }
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector =
                                                    Icons.Default.Delete,
                                                contentDescription =
                                                    "Excluir pedido",
                                                tint = Color(0xFFD94C59)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAdicionar) {
        PedidoFormDialog(
            titulo = "Novo pedido",
            textoBotao = "Adicionar pedido",
            onDismiss = {
                mostrarDialogoAdicionar = false
            },
            onSalvar = { nomeProduto, quantidade ->
                val idUsuario = usuarioId

                val produtoEncontrado = produtos.find {
                    it.nome.equals(
                        nomeProduto.trim(),
                        ignoreCase = true
                    )
                }

                if (idUsuario == null || produtoEncontrado == null) {
                    false
                } else {
                    val item = ItemSacola(
                        produto = produtoEncontrado,
                        quantidade = quantidade
                    )

                    val novoPedido = Pedido(
                        id = UUID.randomUUID().toString(),
                        itens = listOf(item),
                        valorTotal = item.subtotal
                    )

                    scope.launch {
                        try {
                            repository.salvarPedido(
                                usuarioId = idUsuario,
                                pedido = novoPedido
                            )
                            mostrarDialogoAdicionar = false
                        } catch (e: Exception) {
                            erroOperacao =
                                "Não foi possível salvar o pedido: " +
                                        (e.localizedMessage
                                            ?: "erro desconhecido")
                        }
                    }

                    true
                }
            }
        )
    }

    if (mostrarDialogoEditar && pedidoSelecionado != null) {
        PedidoFormDialog(
            titulo = "Editar pedido",
            textoBotao = "Salvar alterações",
            pedidoInicial = pedidoSelecionado,
            onDismiss = {
                mostrarDialogoEditar = false
                pedidoSelecionado = null
            },
            onSalvar = { nomeProduto, quantidade ->
                val idUsuario = usuarioId
                val pedidoAtual = pedidoSelecionado

                val produtoEncontrado = produtos.find {
                    it.nome.equals(
                        nomeProduto.trim(),
                        ignoreCase = true
                    )
                }

                if (
                    idUsuario == null ||
                    pedidoAtual == null ||
                    produtoEncontrado == null
                ) {
                    false
                } else {
                    val novoItem = ItemSacola(
                        produto = produtoEncontrado,
                        quantidade = quantidade
                    )

                    val pedidoAtualizado = pedidoAtual.copy(
                        itens = listOf(novoItem),
                        valorTotal = novoItem.subtotal
                    )

                    scope.launch {
                        try {
                            repository.salvarPedido(
                                usuarioId = idUsuario,
                                pedido = pedidoAtualizado
                            )
                            mostrarDialogoEditar = false
                            pedidoSelecionado = null
                        } catch (e: Exception) {
                            erroOperacao =
                                "Não foi possível atualizar o pedido: " +
                                        (e.localizedMessage
                                            ?: "erro desconhecido")
                        }
                    }

                    true
                }
            }
        )
    }

    if (erroOperacao != null) {
        AlertDialog(
            onDismissRequest = {
                erroOperacao = null
            },
            title = {
                Text(
                    text = "Atenção",
                    color = MytosPurpleDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(erroOperacao.orEmpty())
            },
            confirmButton = {
                TextButton(
                    onClick = { erroOperacao = null }
                ) {
                    Text(
                        text = "OK",
                        color = MytosPurple
                    )
                }
            }
        )
    }
}

@Composable
private fun PedidoFormDialog(
    titulo: String,
    textoBotao: String,
    pedidoInicial: Pedido? = null,
    onDismiss: () -> Unit,
    onSalvar: (String, Int) -> Boolean
) {
    val focusManager = LocalFocusManager.current

    var nomeProduto by remember(pedidoInicial?.id) {
        mutableStateOf(
            pedidoInicial?.itens?.firstOrNull()?.produto?.nome ?: ""
        )
    }

    var quantidadeTexto by remember(pedidoInicial?.id) {
        mutableStateOf(
            pedidoInicial?.itens?.firstOrNull()?.quantidade?.toString()
                ?: ""
        )
    }

    var erroProduto by remember {
        mutableStateOf(false)
    }

    var erroQuantidade by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = titulo,
                color = MytosPurpleDark,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nomeProduto,
                    onValueChange = {
                        nomeProduto = it
                        erroProduto = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Produto") },
                    placeholder = {
                        Text("Ex.: Café de Apolo")
                    },
                    singleLine = true,
                    isError = erroProduto,
                    supportingText = {
                        if (erroProduto) {
                            Text(
                                "Digite um produto válido.",
                                color = Color(0xFFD94C59)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = quantidadeTexto,
                    onValueChange = {
                        quantidadeTexto = it.filter(Char::isDigit)
                        erroQuantidade = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Quantidade") },
                    placeholder = { Text("Ex.: 2") },
                    singleLine = true,
                    isError = erroQuantidade,
                    supportingText = {
                        if (erroQuantidade) {
                            Text(
                                "Informe uma quantidade maior que 0.",
                                color = Color(0xFFD94C59)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Produtos disponíveis:",
                    color = MytosText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = produtos.joinToString(" • ") { it.nome },
                    color = MytosText.copy(alpha = 0.65f),
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantidade = quantidadeTexto.toIntOrNull()
                    val produtoValido = produtos.any {
                        it.nome.equals(
                            nomeProduto.trim(),
                            ignoreCase = true
                        )
                    }
                    val quantidadeValida =
                        quantidade != null && quantidade > 0

                    erroProduto = !produtoValido
                    erroQuantidade = !quantidadeValida

                    if (produtoValido && quantidadeValida) {
                        onSalvar(nomeProduto, quantidade!!)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MytosPurple
                )
            ) {
                Text(textoBotao)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = MytosPurple
                )
            }
        }
    )
}