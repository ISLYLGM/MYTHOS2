package com.example.mytos.screens.perfil

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mytos.data.AuthRepository
import com.example.mytos.Rotas
import com.example.mytos.ui.components.MytosBottomBar
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosText
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    navController: NavController,
    authRepository: AuthRepository
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var usuarioId by remember { mutableStateOf<Long?>(null) }
    var carregando by remember { mutableStateOf(true) }
    var editandoPerfil by remember { mutableStateOf(false) }
    var erroPerfil by remember { mutableStateOf<String?>(null) }
    var salvando by remember { mutableStateOf(false) }

    LaunchedEffect(authRepository) {
        carregando = true

        try {
            val usuario = authRepository.obterUsuarioLogado()

            if (usuario != null) {
                usuarioId = usuario.id
                nome = usuario.nome
                email = usuario.email
                erroPerfil = null
            } else {
                erroPerfil = "Sua sessão expirou. Entre novamente."
            }
        } catch (_: Exception) {
            erroPerfil = "Não foi possível carregar seu perfil."
        } finally {
            carregando = false
        }
    }

    Scaffold(
        bottomBar = {
            MytosBottomBar(navController = navController)
        },
        containerColor = MytosCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (editandoPerfil) {
                    IconButton(
                        onClick = {
                            editandoPerfil = false
                            focusManager.clearFocus()

                            scope.launch {
                                val usuario = authRepository.obterUsuarioLogado()

                                if (usuario != null) {
                                    nome = usuario.nome
                                    email = usuario.email
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancelar edição",
                            tint = MytosPurple
                        )
                    }
                }

                Text(
                    text = "Meu perfil",
                    color = MytosPurple,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MytosPurple.copy(alpha = 0.10f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    if (carregando) {
                        Text(
                            text = "Carregando perfil...",
                            color = MytosText
                        )
                    } else if (usuarioId == null) {
                        Text(
                            text = erroPerfil ?: "Nenhum usuário conectado.",
                            color = MytosPurpleDark
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                authRepository.sair()

                                navController.navigate(Rotas.Login) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Text("Ir para o login")
                        }
                    } else if (!editandoPerfil) {
                        Text(
                            text = "Olá, $nome! 👋",
                            color = MytosPurpleDark,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = email,
                            color = MytosText.copy(alpha = 0.8f),
                            fontSize = 15.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                editandoPerfil = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MytosPurple
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text("Editar perfil")
                        }
                    } else {
                        Text(
                            text = "Alterar dados pessoais",
                            color = MytosPurpleDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = nome,
                            onValueChange = {
                                nome = it
                                erroPerfil = null
                            },
                            label = { Text("Nome") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = campoPerfil()
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                erroPerfil = null
                            },
                            label = { Text("E-mail") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = campoPerfil()
                        )

                        erroPerfil?.let {
                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = it,
                                color = MytosPurpleDark,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            enabled = !salvando,
                            onClick = {
                                val idAtual = usuarioId

                                if (nome.trim().length < 3) {
                                    erroPerfil =
                                        "O nome precisa ter pelo menos 3 caracteres."
                                } else if (
                                    !email.trim().matches(
                                        Regex(
                                            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                                        )
                                    )
                                ) {
                                    erroPerfil = "Digite um e-mail válido."
                                } else if (idAtual != null) {
                                    salvando = true

                                    scope.launch {
                                        try {
                                            val usuario =
                                                authRepository.obterUsuarioLogado()

                                            if (
                                                usuario == null ||
                                                usuario.id != idAtual
                                            ) {
                                                erroPerfil =
                                                    "Sessão inválida. Entre novamente."
                                            } else {
                                                authRepository.atualizarPerfil(
                                                    usuario.copy(
                                                        nome = nome.trim(),
                                                        email = email.trim().lowercase()
                                                    )
                                                )

                                                nome = nome.trim()
                                                email = email.trim().lowercase()
                                                erroPerfil = null
                                                editandoPerfil = false
                                                focusManager.clearFocus()
                                            }
                                        } catch (e: IllegalArgumentException) {
                                            erroPerfil = e.message
                                                ?: "Verifique os dados informados."
                                        } catch (_: Exception) {
                                            erroPerfil =
                                                "Não foi possível salvar as alterações."
                                        } finally {
                                            salvando = false
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MytosPurple
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                if (salvando) {
                                    "Salvando..."
                                } else {
                                    "Salvar alterações"
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Atividades",
                color = MytosText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate(Rotas.Pedidos)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = null,
                    tint = MytosPurple
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Meus Pedidos",
                    color = MytosPurple,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    authRepository.sair()

                    navController.navigate(Rotas.Login) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MytosPurpleDark
                )
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text("Sair da conta")
            }
        }
    }
}

@Composable
private fun campoPerfil() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MytosPurple,
    unfocusedBorderColor = MytosPurple.copy(alpha = 0.45f),
    focusedLabelColor = MytosPurple,
    focusedTextColor = MytosText,
    unfocusedTextColor = MytosText,
    cursorColor = MytosPurple,
    errorTextColor = MytosText
)