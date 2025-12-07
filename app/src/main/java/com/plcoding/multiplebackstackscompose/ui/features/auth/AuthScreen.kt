package com.plcoding.multiplebackstackscompose.ui.features.auth

import androidx.compose.runtime.Composable
import com.plcoding.multiplebackstackscompose.Auth
import com.plcoding.multiplebackstackscompose.ui.widgets.GenericScreen

@Composable
fun AuthScreen(navigation: () -> Unit) {
    GenericScreen(text = Auth.toString(), navigation)
}