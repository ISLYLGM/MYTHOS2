package com.example.mytos.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mytos.R
import com.example.mytos.data.SacolaData
import com.example.mytos.Rotas
import com.example.mytos.ui.components.MytosBottomBar
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosLilac
import com.example.mytos.ui.theme.MytosLilacLight
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosText
import com.example.mytos.ui.theme.MytosTextSecondary
import com.example.mytos.ui.theme.MytosYellow
import java.util.Locale
import com.example.mytos.data.produtos

@Composable
fun HomeScreen(
    navController: NavController
) {

    Scaffold(
        containerColor = MytosCream,

        bottomBar = {
            MytosBottomBar(navController)
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            // =====================================================
            // CABEÇALHO
            // =====================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 38.dp,
                            bottomEnd = 38.dp
                        )
                    )
                    .background(MytosPurpleDark)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 18.dp,
                            bottom = 20.dp
                        )
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.12f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                painter = painterResource(
                                    id = R.drawable.mytos_logo_white
                                ),
                                contentDescription = "Logo MYTOS",
                                tint = Color.White,
                                modifier = Modifier.size(29.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "MYTOS",
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "CAFÉ • MITOLOGIA • EXPERIÊNCIA",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 8.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                navController.navigate(
                                    Rotas.Perfil
                                )
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.13f),
                                    shape = CircleShape
                                )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Abrir perfil",
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )

                    Text(
                        text = "Sabores que contam histórias.",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Descubra uma experiência diferente a cada visita.",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 13.sp
                    )
                }
            }


            // =====================================================
            // SAUDAÇÃO
            // =====================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 22.dp
                    )
            ) {

                Text(
                    text = "Olá, seja bem-vindo! ✨",
                    color = MytosPurpleDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Que tal descobrir um novo sabor hoje?",
                    color = MytosTextSecondary,
                    fontSize = 14.sp
                )
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =====================================================
            // DESTAQUE
            // =====================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable {
                        navController.navigate(
                            Rotas.Cardapio
                        )
                    },

                shape = RoundedCornerShape(26.dp),

                colors = CardDefaults.cardColors(
                    containerColor = MytosPurple
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "O sabor dos deuses",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Explore bebidas e doces inspirados em diferentes mitologias.",
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Text(
                            text = "EXPLORAR  →",
                            color = MytosYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Color.White.copy(alpha = 0.13f),
                                CircleShape
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Coffee,
                            contentDescription = "Explorar cardápio",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =====================================================
            // CATEGORIAS
            // =====================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Explore o cardápio",
                    color = MytosPurpleDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Ver tudo",
                    color = MytosPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.clickable {
                        navController.navigate(
                            Rotas.Cardapio
                        )
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    )
                    .padding(horizontal = 20.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                CategoriaHomeCard(
                    nome = "Bebidas",
                    icon = Icons.Default.Coffee,
                    onClick = {
                        navController.navigate(
                            Rotas.Cardapio
                        )
                    }
                )

                CategoriaHomeCard(
                    nome = "Doces",
                    icon = Icons.Default.Cake,
                    onClick = {
                        navController.navigate(
                            Rotas.Cardapio
                        )
                    }
                )

                CategoriaHomeCard(
                    nome = "Salgados",
                    icon = Icons.Default.Restaurant,
                    onClick = {
                        navController.navigate(
                            Rotas.Cardapio
                        )
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =====================================================
            // DESTAQUES DO MYTOS
            // =====================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Destaques do MYTOS",
                    color = MytosPurpleDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Ver cardápio",
                    color = MytosPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.clickable {
                        navController.navigate(
                            Rotas.Cardapio
                        )
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    )
                    .padding(horizontal = 20.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                produtos.take(3).forEach { produto ->

                    ProdutoDestaqueCard(

                        produtoId = produto.id,

                        nome = produto.nome,

                        descricao = produto.descricao,

                        preco = produto.preco,

                        onClick = {
                            navController.navigate(
                                "produto/${produto.id}"
                            )
                        }
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // =====================================================
            // PERSONAGENS
            // =====================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),

                shape = RoundedCornerShape(26.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Conheça nossos personagens",
                                color = MytosPurpleDark,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )

                            Text(
                                text = "Leve um pedacinho do MYTOS com você e conheça nossa coleção de personagens.",
                                color = MytosTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Personagens MYTOS",
                            tint = MytosYellow,
                            modifier = Modifier.size(25.dp)
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                MytosLilacLight
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(
                                id = R.drawable.toys
                            ),

                            contentDescription =
                                "Personagens Iago, Bast e Mika",

                            modifier =
                                Modifier.fillMaxSize(),

                            contentScale =
                                ContentScale.Fit
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // =====================================================
            // FRASE FINAL
            // =====================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 28.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MytosYellow,
                    modifier = Modifier.size(25.dp)
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = "Café, sabor e lendas.",
                    color = MytosPurpleDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Um universo inspirado em mitologias.",
                    color = MytosTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}


// =============================================================
// CARD DE CATEGORIA
// =============================================================

@Composable
private fun CategoriaHomeCard(
    nome: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .width(82.dp)
            .clickable {
                onClick()
            },

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(62.dp)
                .background(
                    color = MytosLilac,
                    shape = RoundedCornerShape(20.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = nome,
                tint = MytosPurple,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        Text(
            text = nome,
            color = MytosText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// =============================================================
// CARD DE PRODUTO DA HOME
// =============================================================

@Composable
private fun ProdutoDestaqueCard(
    produtoId: Int,
    nome: String,
    descricao: String,
    preco: Double,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .width(185.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(10.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(
                        RoundedCornerShape(15.dp)
                    )
                    .background(
                        MytosLilacLight
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Coffee,
                    contentDescription = null,
                    tint = MytosPurple.copy(alpha = 0.35f),
                    modifier = Modifier.size(42.dp)
                )
            }


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Text(
                text = nome,
                color = MytosPurpleDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            Text(
                text = descricao,
                color = MytosTextSecondary,
                fontSize = 10.sp,
                lineHeight = 14.sp,

                maxLines = 2,

                overflow =
                    TextOverflow.Ellipsis
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = String.format(
                        Locale.forLanguageTag("pt-BR"),
                        "R$ %.2f",
                        preco
                    ),
                    color = MytosPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        val produtoEncontrado = produtos.find { it.id == produtoId }
                        if (produtoEncontrado != null) {
                            SacolaData.adicionarProduto(produtoEncontrado)
                        }
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = MytosPurple,
                            shape = CircleShape
                        )
                ) {
                    // Aqui deve estar o Icon do botão, por exemplo:
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar à sacola",
                        tint = Color.White
                    )
                }
            }
        }
    }
}