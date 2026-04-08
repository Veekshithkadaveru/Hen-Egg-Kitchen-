package app.krafted.heneggkitchen.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.heneggkitchen.R
import app.krafted.heneggkitchen.ui.theme.TextPrimary
import app.krafted.heneggkitchen.ui.theme.WarmAmber
import app.krafted.heneggkitchen.ui.theme.WarmCream
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-15f) }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_icon"
    )

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        delay(2600L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_splash),
                contentDescription = "App Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .size(160.dp)
                    .scale(scale.value)
                    .rotate(rotation.value)
                    .alpha(alpha.value)
                    .clip(androidx.compose.foundation.shape.CircleShape)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Hen Egg Kitchen",
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                color = WarmAmber,
                modifier = Modifier.alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Healthy & Delicious",
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                color = TextPrimary.copy(alpha = 0.6f),
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
