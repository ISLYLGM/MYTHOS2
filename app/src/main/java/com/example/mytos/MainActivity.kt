
package com.example.mytos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.mytos.data.MytosDatabase
import com.example.mytos.data.SacolaData
import com.example.mytos.data.SessaoManager
import com.example.mytos.data.AuthRepository
import com.example.mytos.ui.theme.MytosTheme

import com.example.mytos.screens.splash.SplashScreen
import com.example.mytos.screens.login.LoginScreen
import com.example.mytos.screens.cadastro.CadastroScreen
import com.example.mytos.screens.acessibilidade.AcessibilidadeScreen
import com.example.mytos.screens.home.HomeScreen
import com.example.mytos.screens.cardapio.CardapioScreen
import com.example.mytos.screens.sacola.SacolaScreen
import com.example.mytos.screens.perfil.PerfilScreen
import com.example.mytos.screens.pedidos.PedidosScreen
import com.example.mytos.screens.pedidos.PedidoDetalhesScreen
import com.example.mytos.screens.produto.ProdutoDetalhesScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MytosDatabase.getDatabase(applicationContext)
        val sessaoManager = SessaoManager(applicationContext)

        val authRepository = AuthRepository(
            database.usuarioDao(),
            sessaoManager
        )

        setContent {
            MytosTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Rotas.Splash,
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { fadeOut() }
                ) {

                    composable(Rotas.Splash) {
                        SplashScreen(
                            onFinished = {
                                navController.navigate(Rotas.Login) {
                                    popUpTo(Rotas.Splash) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(Rotas.Login) {
                        LoginScreen(
                            authRepository = authRepository,
                            onLogin = {
                                SacolaData.definirUsuarioAtual(
                                    sessaoManager.obterUsuarioLogado()
                                )

                                navController.navigate(Rotas.Home) {
                                    popUpTo(Rotas.Login) {
                                        inclusive = true
                                    }
                                }
                            },
                            onCadastro = {
                                navController.navigate(Rotas.Cadastro)
                            }
                        )
                    }

                    composable(Rotas.Cadastro) {
                        CadastroScreen(
                            authRepository = authRepository,
                            onCadastroConcluido = {
                                SacolaData.definirUsuarioAtual(
                                    sessaoManager.obterUsuarioLogado()
                                )

                                navController.navigate(Rotas.Acessibilidade) {
                                    popUpTo(Rotas.Cadastro) {
                                        inclusive = true
                                    }
                                }
                            },
                            onVoltarLogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Rotas.Acessibilidade) {
                        AcessibilidadeScreen(
                            onContinuar = {
                                navController.navigate(Rotas.Home) {
                                    popUpTo(Rotas.Acessibilidade) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(Rotas.Home) {
                        HomeScreen(
                            navController = navController
                        )
                    }

                    composable(Rotas.Cardapio) {
                        CardapioScreen(
                            navController = navController
                        )
                    }

                    composable(Rotas.Sacola) {
                        SacolaScreen(
                            navController = navController,
                            onNavegarParaMeusPedidos = {
                                navController.navigate(Rotas.Pedidos)
                            }
                        )
                    }

                    composable(Rotas.Perfil) {
                        PerfilScreen(
                            navController = navController,
                            authRepository = authRepository
                        )
                    }

                    composable(Rotas.Pedidos) {
                        PedidosScreen(
                            navController = navController
                        )
                    }

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

                    composable(
                        route = "${Rotas.ProdutoDetalhes}/{produtoId}",
                        arguments = listOf(
                            navArgument("produtoId") {
                                type = NavType.IntType
                            }
                        )
                    ) { backStackEntry ->
                        val produtoId =
                            backStackEntry.arguments
                                ?.getInt("produtoId") ?: 0

                        ProdutoDetalhesScreen(
                            navController = navController,
                            produtoId = produtoId
                        )
                    }
                }
            }
        }
    }
}