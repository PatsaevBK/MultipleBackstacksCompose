package com.plcoding.multiplebackstackscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.plcoding.multiplebackstackscompose.ui.theme.MultipleBackstacksComposeTheme
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultipleBackstacksComposeTheme {
                val rootNavController = rememberNavController()
                val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            BottomNavigationItem.entries.forEach { item ->
                                val isSelected =
                                        navBackStackEntry?.destination?.hasRoute(item.route) == true
                                NavigationBarItem(
                                    selected = isSelected,
                                    label = {
                                        Text(text = item.title)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if(isSelected) {
                                                item.selectedIcon
                                            } else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    },
                                    onClick = {
                                        val route = when (item) {
                                            BottomNavigationItem.HOME -> HomeMain
                                            BottomNavigationItem.CHAT -> ChatMain
                                            BottomNavigationItem.SETTINGS -> SettingsMain
                                        }
                                        rootNavController.navigate(route) {
                                            popUpTo(rootNavController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { padding ->
                    NavHost(rootNavController, startDestination = HomeMain, Modifier.padding(padding)) {
                        composable<HomeMain> {
                            HomeNavHost()
                        }
                        composable<ChatMain>(
                            deepLinks = listOf(navDeepLink { uriPattern = "rpm://ChatMain" })
                        ) {
                            ChatNavHost()
                        }
                        composable<SettingsMain> {
                            SettingsNavHost(navigateToDeepLink = { rootNavController.navigate(deepLink = "rpm://Chat2".toUri()) })
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data object HomeMain
@Serializable
data object Home1
@Serializable
data object Home2
@Serializable
data object Home3

@Serializable
data object ChatMain
@Serializable
data object Chat1
@Serializable
data object Chat2
@Serializable
data object Chat3


@Serializable
data object SettingsMain
@Serializable
data object Settings1
@Serializable
data object Settings2
@Serializable
data object Settings3

@Composable
fun HomeNavHost() {
    val homeNavController = rememberNavController()
    NavHost(homeNavController, startDestination = Home1) {
        composable<Home1> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = {
                    homeNavController.navigate(Home2)
                }
            )
        }

        composable<Home2> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = {
                    homeNavController.navigate(Home3)
                }
            )
        }

        composable<Home3> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = { }
            )
        }
    }
}

@Composable
fun ChatNavHost() {
    val chatNavController = rememberNavController()
    NavHost(chatNavController, startDestination = Chat1) {
        composable<Chat1> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = {
                    chatNavController.navigate(Chat2)
                }
            )
        }

        composable<Chat2>(deepLinks = listOf(navDeepLink { uriPattern = "rpm://Chat2" })) {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = {
                    chatNavController.navigate(Chat3)
                }
            )
        }

        composable<Chat3> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = { }
            )
        }
    }
}

@Composable
fun SettingsNavHost(navigateToDeepLink: () -> Unit) {
    val settingsNavController = rememberNavController()
    NavHost(settingsNavController, startDestination = Settings1) {
        composable<Settings1> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = {
                    settingsNavController.navigate(Settings2)
                }
            )
        }

        composable<Settings2> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = {
                    settingsNavController.navigate(Settings3)
                }
            )
        }

        composable<Settings3> {
            GenericScreen(
                text = it.destination.route.toString(),
                onNextClick = navigateToDeepLink
            )
        }
    }
}

@Composable
fun GenericScreen(
    text: String,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNextClick) {
            Text("Next")
        }
    }
}

enum class BottomNavigationItem(
    val title: String,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME(
        title = "Home",
        route = HomeMain::class,
        baseRoute = HomeMain::class,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    CHAT(
        title = "Chat",
        route = ChatMain::class,
        baseRoute = ChatMain::class,
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
    ),
    SETTINGS(
        title = "Settings",
        route = SettingsMain::class,
        baseRoute = SettingsMain::class,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
}