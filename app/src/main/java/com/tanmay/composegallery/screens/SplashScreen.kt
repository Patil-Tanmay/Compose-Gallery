package com.tanmay.composegallery.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.tanmay.composegallery.R

@Composable
fun SplashScreen(onFinished: () -> Unit) = Box(
    Modifier
        .fillMaxWidth()
        .fillMaxHeight()
) {

    val scale = remember {
        Animatable(0.0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(1000, easing = {
                OvershootInterpolator(6f).getInterpolation(it)
            })
        )
        delay(1000)
        onFinished()
    }

    Image(
        painter = painterResource(id = R.drawable.ic_splash),
        contentDescription = "",
        alignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .scale(scale.value)
    )

    Text(
        text = "Gallery App",
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)
    )
}