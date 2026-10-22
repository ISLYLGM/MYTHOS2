package com.example.mytos.screens.login

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.mytos.R
import com.example.mytos.data.AuthRepository
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosText
import com.example.mytos.ui.theme.MytosTextSecondary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLogin: () -> Unit,
    onCadastro: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mostrarSenha by remember { mutableStateOf(false) }
    var tentouEntrar by remember { mutableStateOf(false) }
    var erroLogin by remember { mutableStateOf<String?>(null) }
    var entrando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val emailValido = email.trim().matches(
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    )
    val senhaValida = senha.length >= 6
    val formularioValido = emailValido && senhaValida

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MytosCream)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    color = MytosPurpleDark,
                    shape = RoundedCornerShape(
                        bottomStart = 50.dp,
                        bottomEnd = 50.dp
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mytos_logo_white),
                    contentDescription = "Logo MYTOS",
                    modifier = Modifier.size(95.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "CAFÉ • SABOR • MITOLOGIA",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.8.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = 28.dp,
                    bottom = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Que bom ter você aqui!",
                color = MytosPurpleDark,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Entre na sua conta e escolha\nsua próxima aventura saborosa.",
                color = MytosTextSecondary,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    tentouEntrar = false
                    erroLogin = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,
                placeholder = { Text("E-mail", fontSize = 17.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "E-mail",
                        tint = MytosPurple
                    )
                },
                isError = tentouEntrar && !emailValido,
                shape = RoundedCornerShape(22.dp),
                colors = campoLogin()
            )

            if (tentouEntrar && !emailValido) {
                Text(
                    text = "Digite um e-mail válido.",
                    color = Color(0xFFD94C59),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = {
                    senha = it
                    tentouEntrar = false
                    erroLogin = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,
                placeholder = { Text("Senha", fontSize = 17.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Senha",
                        tint = MytosPurple
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { mostrarSenha = !mostrarSenha }
                    ) {
                        Icon(
                            imageVector = if (mostrarSenha) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (mostrarSenha) {
                                "Ocultar senha"
                            } else {
                                "Mostrar senha"
                            },
                            tint = MytosTextSecondary
                        )
                    }
                },
                visualTransformation = if (mostrarSenha) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = tentouEntrar && !senhaValida,
                shape = RoundedCornerShape(22.dp),
                colors = campoLogin()
            )

            if (tentouEntrar && !senhaValida) {
                Text(
                    text = "A senha deve ter pelo menos 6 caracteres.",
                    color = Color(0xFFD94C59),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 3.dp)
                )
            }

            if (erroLogin != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = erroLogin.orEmpty(),
                    color = Color(0xFFD94C59),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    tentouEntrar = true
                    erroLogin = null

                    if (formularioValido && !entrando) {
                        entrando = true

                        scope.launch {
                            try {
                                authRepository.entrar(email.trim(), senha)
                                onLogin()
                            } catch (e: Exception) {
                                erroLogin = "E-mail ou senha incorretos."
                            } finally {
                                entrando = false
                            }
                        }
                    }
                },
                enabled = !entrando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MytosPurple,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (entrando) "ENTRANDO..." else "ENTRAR",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Primeira visita?",
                    color = MytosTextSecondary,
                    fontSize = 15.sp
                )

                TextButton(onClick = onCadastro) {
                    Text(
                        text = "Criar conta",
                        color = MytosPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.instagram.com/bia__e__bel/".toUri()
                    )
                    context.startActivity(intent)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.size(7.dp))

                Text(
                    text = "Confira nosso Instagram",
                    color = MytosPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun campoLogin() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MytosText,
    unfocusedTextColor = MytosText,
    focusedBorderColor = MytosPurple,
    unfocusedBorderColor = Color(0xFFDCD5DF),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = MytosPurple,
    focusedPlaceholderColor = MytosTextSecondary,
    unfocusedPlaceholderColor = MytosTextSecondary,
    errorBorderColor = Color(0xFFD94C59),
    errorCursorColor = Color(0xFFD94C59)
)