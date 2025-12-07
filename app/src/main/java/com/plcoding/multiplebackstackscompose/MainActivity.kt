package com.plcoding.multiplebackstackscompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.plcoding.multiplebackstackscompose.ui.features.auth.AuthScreen
import com.plcoding.multiplebackstackscompose.ui.navigation.DeepLinkRouter
import com.plcoding.multiplebackstackscompose.ui.navigation.NavControllers
import com.plcoding.multiplebackstackscompose.ui.theme.MultipleBackstacksComposeTheme
import com.plcoding.multiplebackstackscompose.ui.widgets.GenericScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {

    private val deepLinkFlow = MutableSharedFlow<Intent>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("XXX onCreate")

        // если Activity стартовала с диплинком
        intent?.data?.let {
            lifecycleScope.launch { deepLinkFlow.emit(intent) }
        }

        setContent {
            MultipleBackstacksComposeTheme {
                AppRoot(deepLinkFlow)
            }
        }
    }

    @Composable
    private fun AppRoot(deeplinkFlow: MutableSharedFlow<Intent>) {
        // root nav (Auth -> App)
        val rootNavController = rememberNavController()

        // app level nav (tabs)
        val appNavController = rememberNavController()

        // вложенные nav controllers (hoisted)
        val homeNavController = rememberNavController()
        val chatNavController = rememberNavController()
        val settingsNavController = rememberNavController()

        val navControllers = remember(
            rootNavController,
            appNavController,
            homeNavController,
            chatNavController,
            settingsNavController,
        ) {
            NavControllers(
                root = rootNavController,
                app = appNavController,
                home = homeNavController,
                chat = chatNavController,
                settings = settingsNavController
            )
        }

        LaunchedEffect(Unit) {
            deepLinkFlow.collectLatest { DeepLinkRouter.handleDeepLink(it, navControllers) }
        }

        NavHost(navController = rootNavController, startDestination = Auth) {
            composable<Auth> {
                AuthScreen {
                    rootNavController.navigate(route = App) {
                        popUpTo<Auth> {
                            inclusive = true
                        }
                    }
                }
            }

            composable<App> {
                AppScreen(navControllers)
            }
        }
    }

    @Composable
    private fun AppScreen(
        navControllers: NavControllers,
    ) {
//        val appNavController = rememberNavController()
        val navBackStackEntry by navControllers.app.currentBackStackEntryAsState()
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
                                    imageVector = if (isSelected) {
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
                                navControllers.app.navigate(route) {
                                    popUpTo(navControllers.app.graph.findStartDestination().id) {
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
            NavHost(navControllers.app, startDestination = HomeMain, Modifier.padding(padding)) {
                composable<HomeMain> {
                    HomeNavHost(navControllers.home)
                }
                composable<ChatMain>(
                    deepLinks = listOf(navDeepLink { uriPattern = "rpm://ChatMain" })
                ) {
                    ChatNavHost(navControllers.chat)
                }
                composable<SettingsMain> {
                    SettingsNavHost(settingsNavController = navControllers.settings, navigateToDeepLink = { navControllers.app.navigate(deepLink = "rpm://ChatMain".toUri()) })
                }
            }
        }
    }
}

@Serializable
data object Auth
@Serializable
data object App

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
fun HomeNavHost(
    homeNavController: NavHostController,
) {
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
fun ChatNavHost(
    chatNavController: NavHostController,
) {
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
fun SettingsNavHost(
    settingsNavController: NavHostController,
    navigateToDeepLink: () -> Unit
) {
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