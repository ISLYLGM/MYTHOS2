package com.example.mytos.screens.acessibilidade

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytos.R
import com.example.mytos.ui.theme.MytosCream
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosPurpleDark
import com.example.mytos.ui.theme.MytosTextSecondary
import com.example.mytos.ui.theme.MytosYellow

@Composable
fun AcessibilidadeScreen(
    onContinuar: (Boolean) -> Unit
) {
    var usaTalkBack by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MytosCream)
            .verticalScroll(rememberScrollState())
    ) {

        // ==========================================
        // CABEÇALHO COM BAST
        // ==========================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(
                    color = MytosPurpleDark,
                    shape = RoundedCornerShape(
                        bottomStart = 55.dp,
                        bottomEnd = 55.dp
                    )
                )
        ) {



            // Bast centralizada
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.bast_acessibilidade
                    ),
                    contentDescription = "Bast segurando o símbolo de acessibilidade",
                    modifier = Modifier
                        .width(230.dp)
                        .height(285.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // ==========================================
        // CONTEÚDO
        // ==========================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = 28.dp,
                    bottom = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ==========================================
            // TÍTULO
            // ==========================================

            Text(
                text = "ATENÇÃO",
                color = MytosPurpleDark,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Você pode escolher recursos que tornam sua experiência mais confortável.",
                color = MytosTextSecondary,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ==========================================
            // CARD TALKBACK
            // ==========================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // ÍCONE DE ACESSIBILIDADE
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(
                                    color = Color(0xFFE5F1DA),
                                    shape = RoundedCornerShape(22.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Accessibility,
                                contentDescription = "Acessibilidade",
                                tint = MytosPurple,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Leitura de tela",
                                color = MytosPurpleDark,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "Use o TalkBack para ouvir os elementos do app.",
                                color = MytosTextSecondary,
                                fontSize = 15.sp,
                                lineHeight = 21.sp
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    // ==========================================
                    // CONFIGURAÇÕES DO ANDROID
                    // ==========================================

                    OutlinedButton(
                        onClick = {

                            val intent = Intent(
                                Settings.ACTION_ACCESSIBILITY_SETTINGS
                            )

                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MytosPurple
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = "ABRIR CONFIGURAÇÕES DO ANDROID",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "O TalkBack é ativado nas configurações de acessibilidade do aparelho. Esta tela não altera essa configuração do Android.",
                        color = MytosTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // ==========================================
            // CARD DE PREFERÊNCIA
            // ==========================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = "Recursos acessíveis do MYTOS",
                        color = MytosPurpleDark,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Informe sua preferência para continuar.",
                        color = MytosTextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // ==========================================
                    // USO LEITOR DE TELA
                    // ==========================================

                    Button(
                        onClick = {
                            usaTalkBack = !usaTalkBack
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (usaTalkBack) {
                                MytosPurple
                            } else {
                                MytosYellow
                            },
                            contentColor = if (usaTalkBack) {
                                Color.White
                            } else {
                                MytosPurpleDark
                            }
                        )
                    ) {

                        Text(
                            text = if (usaTalkBack) {
                                "PREFERÊNCIA ATIVADA"
                            } else {
                                "USO LEITOR DE TELA"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    // ==========================================
                    // CONTINUAR PADRÃO
                    // ==========================================

                    OutlinedButton(
                        onClick = { onContinuar(usaTalkBack) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MytosPurple
                        )
                    ) {

                        Text(
                            text = if (usaTalkBack) {
                                "CONTINUAR"
                            } else {
                                "CONTINUAR PADRÃO"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Continuar no modo padrão"
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ==========================================
            // TEXTO FINAL
            // ==========================================

            Text(
                text = "Você poderá mudar essa preferência depois no seu perfil.",
                color = MytosTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}