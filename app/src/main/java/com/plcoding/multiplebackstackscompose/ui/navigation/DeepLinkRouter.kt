package com.plcoding.multiplebackstackscompose.ui.navigation

import android.content.Intent
import androidx.navigation.NavHostController

object DeepLinkRouter {
    fun handleDeepLink(deepLinkIntent: Intent, navControllers: NavControllers) {
        println("XXX handleDeepLink")
        val deepLink = deepLinkIntent.data ?: return
        if (navControllers.root.graph.hasDeepLink(deepLink = deepLink)) navControllers.app.handleDeepLink(
            deepLinkIntent
        )
        if (navControllers.app.graph.hasDeepLink(deepLink = deepLink)) navControllers.app.handleDeepLink(
            deepLinkIntent
        )
        if (navControllers.home.graph.hasDeepLink(deepLink = deepLink)) navControllers.home.handleDeepLink(
            deepLinkIntent
        )
        if (navControllers.chat.graph.hasDeepLink(deepLink = deepLink)) navControllers.chat.handleDeepLink(
            deepLinkIntent
        )
        if (navControllers.settings.graph.hasDeepLink(deepLink = deepLink)) navControllers.settings.handleDeepLink(
            deepLinkIntent
        )
    }
}

data class NavControllers(
    val root: NavHostController,
    val app: NavHostController,
    val home: NavHostController,
    val chat: NavHostController,
    val settings: NavHostController,
)