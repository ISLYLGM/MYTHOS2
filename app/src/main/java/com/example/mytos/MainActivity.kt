
package com.example.mytos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mytos.data.AcessibilidadeConfig
import com.example.mytos.data.AuthRepository
import com.example.mytos.data.MytosDatabase
import com.example.mytos.data.SacolaData
import com.example.mytos.data.SessaoManager
import com.example.mytos.screens.acessibilidade.AcessibilidadeScreen
import com.example.mytos.screens.cadastro.CadastroScreen
import com.example.mytos.screens.cardapio.CardapioScreen
import com.example.mytos.screens.home.HomeScreen
import com.example.mytos.screens.login.LoginScreen
import com.example.mytos.screens.pedidos.PedidoDetalhesScreen
import com.example.mytos.screens.pedidos.PedidosScreen
import com.example.mytos.screens.perfil.PerfilScreen
import com.example.mytos.screens.produto.ProdutoDetalhesScreen
import com.example.mytos.screens.sacola.SacolaScreen
import com.example.mytos.screens.splash.SplashScreen
import com.example.mytos.ui.theme.MytosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MytosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MytosNavigation()
                }
            }
        }
    }
}

@Composable
private fun MytosNavigation() {

    val context = LocalContext.current
    val navController = rememberNavController()

    val sessaoManager = remember {
        SessaoManager(context.applicationContext)
    }

    val authRepository = remember {
        AuthRepository(
            usuarioDao = MytosDatabase
                .getDatabase(context.applicationContext)
                .usuarioDao(),
            sessaoManager = sessaoManager
        )
    }

    // Prepara a sacola para o usuário conectado.
    val prepararSacola = {
        SacolaData.definirUsuarioAtual(
            sessaoManager.obterUsuarioLogado()
        )
    }

    // Login concluído: segue para a Home.
    val entrarNoApp: () -> Unit = {
        prepararSacola()

        navController.navigate(Rotas.Home) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    // Splash -> Login.
    // A sessão existente será validada no próprio fluxo de login.
    NavHost(
        navController = navController,
        startDestination = Rotas.Splash
    ) {

        composable(Rotas.Splash) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Rotas.Login) {
                        popUpTo(Rotas.Splash) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Login.
        composable(Rotas.Login) {
            LoginScreen(
                authRepository = authRepository,
                onLogin = entrarNoApp,
                onCadastro = {
                    navController.navigate(Rotas.Cadastro)
                }
            )
        }

        // Cadastro concluído -> Acessibilidade.
        composable(Rotas.Cadastro) {
            CadastroScreen(
                authRepository = authRepository,
                onCadastroConcluido = {
                    prepararSacola()

                    navController.navigate(Rotas.Acessibilidade) {
                        popUpTo(Rotas.Cadastro) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onVoltarLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Configuração inicial de acessibilidade.
        composable(Rotas.Acessibilidade) {
            AcessibilidadeScreen(
                onContinuar = { leitorAtivo: Boolean ->

                    AcessibilidadeConfig.definir(
                        context = context,
                        ativo = leitorAtivo
                    )

                    entrarNoApp()
                }
            )
        }

        // Home.
        composable(Rotas.Home) {
            HomeScreen(
                navController = navController
            )
        }

        // Cardápio.
        composable(Rotas.Cardapio) {
            CardapioScreen(
                navController = navController
            )
        }

        // Detalhes do produto.
        composable(
            route = "${Rotas.ProdutoDetalhes}/{produtoId}",
            arguments = listOf(
                navArgument("produtoId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val produtoId =
                backStackEntry.arguments?.getLong("produtoId") ?: 0L

            ProdutoDetalhesScreen(
                navController = navController,
                produtoId = produtoId
            )
        }

        // Sacola.
        composable(Rotas.Sacola) {
            SacolaScreen(
                navController = navController,
                onPedidoFinalizado = { pedidoId ->

                    navController.navigate(
                        "${Rotas.PedidoDetalhes}/$pedidoId"
                    ) {
                        popUpTo(Rotas.Sacola) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Lista de pedidos.
        composable(Rotas.Pedidos) {
            PedidosScreen(
                navController = navController
            )
        }

        // Detalhes do pedido.
        composable(
            route = "${Rotas.PedidoDetalhes}/{pedidoId}",
            arguments = listOf(
                navArgument("pedidoId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val pedidoId =
                backStackEntry.arguments
                    ?.getString("pedidoId")
                    .orEmpty()

            PedidoDetalhesScreen(
                navController = navController,
                pedidoId = pedidoId
            )
        }

        // Perfil.
        composable(Rotas.Perfil) {
            PerfilScreen(
                navController = navController,
                authRepository = authRepository
            )
        }
    }
}
