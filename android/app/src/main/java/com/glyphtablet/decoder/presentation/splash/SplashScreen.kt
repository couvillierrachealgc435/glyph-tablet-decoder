package com.glyphtablet.decoder.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.di.ServiceLocator
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.*

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val model: SplashViewModel = viewModel(factory = ServiceLocator.factory)
    val finished by model.finished.collectAsStateWithLifecycle()
    val state by model.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(finished) { if (finished && model.consume()) onFinished() }
    SplashScreen(state)
}

@Composable
fun SplashScreen(state: SplashUiState) {
    val loop = rememberInfiniteTransition(label = "tablet float")
    val lift by loop.animateFloat(-5f, 5f, infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "lift")
    val glow by loop.animateFloat(0.08f, 0.16f, infiniteRepeatable(tween(2400), RepeatMode.Reverse), label = "rings")
    AppBackground(Backdrop.Splash) {
        Canvas(Modifier.align(Alignment.Center).size(310.dp).alpha(glow)) {
            drawCircle(Sandstone, style = Stroke(2.dp.toPx()))
            drawCircle(Sandstone, radius = size.minDimension * 0.40f, style = Stroke(1.dp.toPx()))
        }
        Column(Modifier.fillMaxSize().padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(painterResource(R.drawable.logo), stringResource(R.string.logo_description),
                Modifier.size(216.dp).offset(y = lift.dp).scale(if (state.transitionConsumed) 1.02f else 1f), contentScale = ContentScale.Fit)
            Spacer(Modifier.height(32.dp))
            Text(stringResource(R.string.app_name), color = Parchment, style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.tagline), color = Sandstone, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(48.dp))
            LinearProgressIndicator(modifier = Modifier.width(144.dp).height(4.dp), color = Sandstone, trackColor = Parchment.copy(alpha = 0.18f))
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.opening_archive), color = Sandstone, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
