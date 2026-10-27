
package com.example.mytos.screens.cardapio

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mytos.Rotas
import com.example.mytos.data.MytosDatabase
import com.example.mytos.data.ProdutoEntity
import com.example.mytos.data.toProduto
import com.example.mytos.model.Produto
import com.example.mytos.ui.components.MytosBottomBar
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosLilac
import com.example.mytos.ui.theme.MytosLilacLight
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosText
import com.example.mytos.ui.theme.MytosTextSecondary
import com.example.mytos.ui.theme.MytosYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.util.Locale

private const val TAG_CARDAPIO = "CardapioScreen"

@Composable
fun CardapioScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = remember(context) {
        MytosDatabase.getDatabase(context.applicationContext)
    }
    val produtoDao = remember(database) {
        database.produtoDao()
    }

    var produtos by remember { mutableStateOf<List<Produto>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erroBanco by remember { mutableStateOf(false) }

    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarDialogoEdicao by remember { mutableStateOf(false) }
    var produtoParaEditar by remember { mutableStateOf<Produto?>(null) }
    var busca by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf("Todos") }

    LaunchedEffect(produtoDao) {
        try {
            val listaInicial = withContext(Dispatchers.IO) {
                produtoDao.observarTodos().first()
            }

            if (listaInicial.isEmpty()) {
                val produtosIniciais = listOf(
                    ProdutoEntity(
                        nome = "Café de Apolo",
                        descricao = "Café inspirado em Apolo, deus grego associado ao Sol.",
                        precoCentavos = 1490L,
                        categoria = "Bebidas"
                    ),
                    ProdutoEntity(
                        nome = "Néctar de Poseidon",
                        descricao = "Bebida refrescante inspirada em Poseidon, deus dos mares.",
                        precoCentavos = 1690L,
                        categoria = "Bebidas"
                    ),
                    ProdutoEntity(
                        nome = "Torta de Hera",
                        descricao = "Doce inspirado em Hera, rainha dos deuses gregos.",
                        precoCentavos = 1890L,
                        categoria = "Doces"
                    ),
                    ProdutoEntity(
                        nome = "Chá de Kitsune",
                        descricao = "Chá inspirado nas lendárias kitsunes da mitologia japonesa.",
                        precoCentavos = 1390L,
                        categoria = "Bebidas"
                    )
                )

                withContext(Dispatchers.IO) {
                    produtoDao.inserirTodos(produtosIniciais)
                }
            }

            produtoDao.observarTodos().collect { entidades ->
                produtos = entidades.map { it.toProduto() }
                carregando = false
                erroBanco = false
            }
        } catch (e: Exception) {
            Log.e(TAG_CARDAPIO, "Erro ao carregar produtos do Room", e)
            erroBanco = true
            carregando = false
        }
    }

    val produtosFiltrados = produtos.filter { produto ->
        val correspondeBusca =
            produto.nome.contains(busca, ignoreCase = true) ||
                    produto.descricao.contains(busca, ignoreCase = true)

        val correspondeCategoria = when (categoriaSelecionada) {
            "Todos" -> true
            "Bebidas" -> produto.categoriaId == 1
            "Doces" -> produto.categoriaId == 2
            "Salgados" -> produto.categoriaId == 3
            else -> true
        }

        correspondeBusca && correspondeCategoria
    }

    Scaffold(
        containerColor = MytosCream,
        bottomBar = { MytosBottomBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MytosCream),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, top = 14.dp)
                        .height(128.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(MytosPurpleDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 20.dp,
                                end = 12.dp,
                                top = 16.dp,
                                bottom = 16.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "MYTOS • CAFÉ E MITOLOGIA",
                                    color = MytosYellow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    "Nosso cardápio",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            IconButton(
                                onClick = { mostrarDialogo = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MytosYellow, CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Adicionar novo produto",
                                    tint = MytosPurpleDark,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Escolha sua próxima história para saborear.",
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = busca,
                    onValueChange = { busca = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    singleLine = true,
                    placeholder = {
                        Text(
                            "O que você deseja saborear?",
                            color = MytosTextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar produtos",
                            tint = MytosPurple
                        )
                    },
                    shape = RoundedCornerShape(17.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MytosPurple,
                        unfocusedBorderColor = MytosLilac,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = MytosText,
                        unfocusedTextColor = MytosText
                    )
                )
            }

            item {
                Spacer(Modifier.height(18.dp))
                Text(
                    "Categorias",
                    color = MytosPurpleDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoriaFiltro(
                        nome = "Todos",
                        selecionada = categoriaSelecionada == "Todos",
                        icon = Icons.Default.Category
                    ) { categoriaSelecionada = "Todos" }

                    CategoriaFiltro(
                        nome = "Bebidas",
                        selecionada = categoriaSelecionada == "Bebidas",
                        icon = Icons.Default.Coffee
                    ) { categoriaSelecionada = "Bebidas" }

                    CategoriaFiltro(
                        nome = "Doces",
                        selecionada = categoriaSelecionada == "Doces",
                        icon = Icons.Default.Cake
                    ) { categoriaSelecionada = "Doces" }

                    CategoriaFiltro(
                        nome = "Salgados",
                        selecionada = categoriaSelecionada == "Salgados",
                        icon = Icons.Default.Restaurant
                    ) { categoriaSelecionada = "Salgados" }
                }
            }

            item {
                Spacer(Modifier.height(22.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Todos os sabores",
                        color = MytosPurpleDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${produtosFiltrados.size} opções",
                        color = MytosTextSecondary,
                        fontSize = 11.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            if (carregando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MytosPurple)
                    }
                }
            } else if (erroBanco) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Não foi possível carregar o cardápio.",
                            color = MytosPurpleDark
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    carregando = true
                                    erroBanco = false
                                    try {
                                        produtos = withContext(Dispatchers.IO) {
                                            produtoDao.observarTodos()
                                                .first()
                                                .map { it.toProduto() }
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            TAG_CARDAPIO,
                                            "Falha ao recarregar produtos",
                                            e
                                        )
                                        erroBanco = true
                                    } finally {
                                        carregando = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MytosPurple
                            )
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            } else {
                items(
                    items = produtosFiltrados,
                    key = { it.id }
                ) { produto ->
                    ProdutoCardMytos(
                        produto = produto,
                        onClick = {
                            navController.navigate(
                                "${Rotas.ProdutoDetalhes}/${produto.id}"
                            )
                        },
                        onEdit = {
                            produtoParaEditar = produto
                            mostrarDialogoEdicao = true
                        },
                        onDelete = {
                            scope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        produtoDao.excluir(produto.id.toLong())
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG_CARDAPIO,
                                        "Erro ao excluir produto ${produto.id}",
                                        e
                                    )
                                }
                            }
                        }
                    )
                }

                if (produtosFiltrados.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 50.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Coffee,
                                contentDescription = null,
                                tint = MytosPurple.copy(alpha = 0.4f),
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Nenhum sabor encontrado",
                                color = MytosPurpleDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                "Tente outra busca ou categoria.",
                                color = MytosTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        NovoProdutoDialog(
            onDismiss = { mostrarDialogo = false },
            onAdicionar = { nome, descricao, preco, categoriaId ->
                scope.launch {
                    try {
                        val entidade = Produto(
                            id = 0,
                            nome = nome,
                            descricao = descricao,
                            preco = preco,
                            categoriaId = categoriaId
                        ).toProdutoEntity().copy(id = 0L)

                        withContext(Dispatchers.IO) {
                            produtoDao.inserir(entidade)
                        }

                        mostrarDialogo = false
                    } catch (e: Exception) {
                        Log.e(TAG_CARDAPIO, "Erro ao cadastrar produto", e)
                    }
                }
            }
        )
    }

    if (mostrarDialogoEdicao && produtoParaEditar != null) {
        EditarProdutoDialog(
            produto = produtoParaEditar!!,
            onDismiss = {
                mostrarDialogoEdicao = false
                produtoParaEditar = null
            },
            onSalvar = { produtoAtualizado ->
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            produtoDao.atualizar(
                                produtoAtualizado.toProdutoEntity()
                            )
                        }

                        mostrarDialogoEdicao = false
                        produtoParaEditar = null
                    } catch (e: Exception) {
                        Log.e(
                            TAG_CARDAPIO,
                            "Erro ao editar produto ${produtoAtualizado.id}",
                            e
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun CategoriaFiltro(
    nome: String,
    selecionada: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selecionada) MytosPurple else Color.White)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { selected = selecionada }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selecionada) Color.White else MytosPurple,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            nome,
            color = if (selecionada) Color.White else MytosText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProdutoCardMytos(
    produto: Produto,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(MytosLilacLight),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (produto.categoriaId == 2) {
                            Icons.Default.Cake
                        } else {
                            Icons.Default.Coffee
                        },
                        contentDescription = null,
                        tint = MytosPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    produto.nome,
                    color = MytosPurpleDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    produto.descricao,
                    color = MytosTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    String.format(
                        Locale("pt", "BR"),
                        "R$ %.2f",
                        produto.preco
                    ),
                    color = MytosPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar ${produto.nome}",
                    tint = MytosPurple,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remover ${produto.nome}",
                    tint = MytosTextSecondary.copy(alpha = 0.65f),
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun CoresCamposProduto() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.6f),
    focusedContainerColor = MytosPurpleDark,
    unfocusedContainerColor = MytosPurpleDark,
    disabledContainerColor = MytosPurpleDark,
    cursorColor = MytosYellow,
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White.copy(alpha = 0.75f),
    disabledBorderColor = Color.White.copy(alpha = 0.4f),
    focusedLabelColor = MytosYellow,
    unfocusedLabelColor = Color.White,
    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
    focusedLeadingIconColor = Color.White,
    unfocusedLeadingIconColor = Color.White
)

@Composable
private fun TituloDialogoProduto(texto: String) {
    Text(
        texto,
        color = MytosYellow,
        fontSize = 21.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun BotoesDialogoProduto(
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancelar) {
            Text(
                "Cancelar",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onConfirmar,
            colors = ButtonDefaults.buttonColors(
                containerColor = MytosYellow,
                contentColor = MytosPurpleDark
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                textoConfirmar,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun NovoProdutoDialog(
    onDismiss: () -> Unit,
    onAdicionar: (String, String, Double, Int) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableIntStateOf(1) }
    var erro by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { TituloDialogoProduto("Novo Produto") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        erro = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nome do produto") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    colors = CoresCamposProduto(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = descricao,
                    onValueChange = {
                        descricao = it
                        erro = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Descrição") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    colors = CoresCamposProduto(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = preco,
                    onValueChange = {
                        preco = it.filter { c ->
                            c.isDigit() || c == ',' || c == '.'
                        }
                        erro = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Preço (ex.: 14,90)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = CoresCamposProduto(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (erro) {
                    Text(
                        "Preencha os campos e informe um preço maior que zero.",
                        color = MytosYellow,
                        fontSize = 12.sp
                    )
                }

                Text(
                    "Categoria",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    listOf(
                        "Bebidas" to 1,
                        "Doces" to 2,
                        "Salgados" to 3
                    ).forEach { (texto, id) ->
                        CategoriaDialogButton(
                            nome = texto,
                            selecionada = categoriaSelecionada == id,
                            onClick = { categoriaSelecionada = id }
                        )
                    }
                }
            }
        },
        confirmButton = {
            BotoesDialogoProduto(
                textoConfirmar = "Adicionar",
                onCancelar = onDismiss,
                onConfirmar = {
                    val valor = preco.replace(",", ".").toDoubleOrNull()

                    if (
                        nome.isBlank() ||
                        descricao.isBlank() ||
                        valor == null ||
                        !valor.isFinite() ||
                        valor <= 0
                    ) {
                        erro = true
                    } else {
                        onAdicionar(
                            nome.trim(),
                            descricao.trim(),
                            valor,
                            categoriaSelecionada
                        )
                    }
                }
            )
        },
        containerColor = MytosPurple,
        shape = RoundedCornerShape(24.dp),
        titleContentColor = MytosYellow,
        textContentColor = Color.White
    )
}

@Composable
private fun EditarProdutoDialog(
    produto: Produto,
    onDismiss: () -> Unit,
    onSalvar: (Produto) -> Unit
) {
    var nome by remember(produto.id) { mutableStateOf(produto.nome) }
    var descricao by remember(produto.id) { mutableStateOf(produto.descricao) }
    var preco by remember(produto.id) {
        mutableStateOf(produto.preco.toString().replace(".", ","))
    }
    var categoriaSelecionada by remember(produto.id) {
        mutableIntStateOf(produto.categoriaId)
    }
    var erro by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { TituloDialogoProduto("Editar Produto") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        erro = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nome do produto") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    colors = CoresCamposProduto(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = descricao,
                    onValueChange = {
                        descricao = it
                        erro = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Descrição") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    colors = CoresCamposProduto(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = preco,
                    onValueChange = {
                        preco = it.filter { c ->
                            c.isDigit() || c == ',' || c == '.'
                        }
                        erro = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Preço (ex.: 14,90)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = CoresCamposProduto(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (erro) {
                    Text(
                        "Preencha os campos e informe um preço maior que zero.",
                        color = MytosYellow,
                        fontSize = 12.sp
                    )
                }

                Text(
                    "Categoria",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    listOf(
                        "Bebidas" to 1,
                        "Doces" to 2,
                        "Salgados" to 3
                    ).forEach { (texto, id) ->
                        CategoriaDialogButton(
                            nome = texto,
                            selecionada = categoriaSelecionada == id,
                            onClick = { categoriaSelecionada = id }
                        )
                    }
                }
            }
        },
        confirmButton = {
            BotoesDialogoProduto(
                textoConfirmar = "Salvar",
                onCancelar = onDismiss,
                onConfirmar = {
                    val valor = preco.replace(",", ".").toDoubleOrNull()

                    if (
                        nome.isBlank() ||
                        descricao.isBlank() ||
                        valor == null ||
                        !valor.isFinite() ||
                        valor <= 0
                    ) {
                        erro = true
                    } else {
                        onSalvar(
                            Produto(
                                id = produto.id,
                                nome = nome.trim(),
                                descricao = descricao.trim(),
                                preco = valor,
                                categoriaId = categoriaSelecionada
                            )
                        )
                    }
                }
            )
        },
        containerColor = MytosPurple,
        shape = RoundedCornerShape(24.dp),
        titleContentColor = MytosYellow,
        textContentColor = Color.White
    )
}

@Composable
private fun CategoriaDialogButton(
    nome: String,
    selecionada: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = nome,
        color = if (selecionada) MytosPurpleDark else Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (selecionada) MytosYellow else Color.Transparent
            )
            .then(
                if (selecionada) Modifier else Modifier.border(
                    width = 1.dp,
                    color = Color.White,
                    shape = CircleShape
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp)
    )
}

private fun Produto.toProdutoEntity(): ProdutoEntity {
    val centavos = (preco * 100)
        .toBigDecimal()
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()

    val categoria = when (categoriaId) {
        1 -> "Bebidas"
        2 -> "Doces"
        3 -> "Salgados"
        else -> "Bebidas"
    }

    return ProdutoEntity(
        id = id.toLong(),
        nome = nome.trim(),
        descricao = descricao.trim(),
        precoCentavos = centavos,
        categoria = categoria,
        disponivel = true
    )
}
