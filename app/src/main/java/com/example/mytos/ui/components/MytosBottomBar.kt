package com.example.mytos.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mytos.ui.theme.MytosPurple
import com.example.mytos.ui.theme.MytosYellow
import com.example.mytos.Rotas

@Composable
fun MytosBottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {

        NavigationBarItem(
            selected = currentRoute == Rotas.Home,
            onClick = {
                navController.navigate(Rotas.Home) {
                    launchSingleTop = true
                    popUpTo(Rotas.Home) {
                        inclusive = false
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text("Home")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MytosPurple,
                selectedTextColor = MytosPurple,
                indicatorColor = MytosYellow
            )
        )

        NavigationBarItem(
            selected = currentRoute == Rotas.Cardapio,
            onClick = {
                navController.navigate(Rotas.Cardapio) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Cardápio"
                )
            },
            label = {
                Text("Cardápio")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MytosPurple,
                selectedTextColor = MytosPurple,
                indicatorColor = MytosYellow
            )
        )

        NavigationBarItem(
            selected = currentRoute == Rotas.Sacola,
            onClick = {
                navController.navigate(Rotas.Sacola) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Sacola"
                )
            },
            label = {
                Text("Sacola")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MytosPurple,
                selectedTextColor = MytosPurple,
                indicatorColor = MytosYellow
            )
        )

        NavigationBarItem(
            selected = currentRoute == Rotas.Perfil,
            onClick = {
                navController.navigate(Rotas.Perfil) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text("Perfil")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MytosPurple,
                selectedTextColor = MytosPurple,
                indicatorColor = MytosYellow
            )
        )
    }
}