package app.krafted.heneggkitchen.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.heneggkitchen.ui.theme.TextPrimary
import app.krafted.heneggkitchen.ui.theme.TextSecondary
import app.krafted.heneggkitchen.ui.theme.WarmAmber

@Composable
fun ServingAdjuster(
    servings: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedServings by animateIntAsState(
        targetValue = servings,
        animationSpec = tween(200),
        label = "servings_anim"
    )

    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AdjusterButton(
            text = "\u2212",
            enabled = servings > 1,
            onClick = onDecrement
        )

        Text(
            text = "$animatedServings",
            fontSize = 22.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            letterSpacing = (-0.3).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 32.dp)
        )

        AdjusterButton(
            icon = true,
            enabled = servings < 12,
            onClick = onIncrement
        )
    }
}

@Composable
private fun AdjusterButton(
    text: String = "",
    icon: Boolean = false,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    val containerColor = if (enabled) WarmAmber else WarmAmber.copy(alpha = 0.3f)
    val contentColor = if (enabled) Color.White else Color.White.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .size(38.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = CircleShape,
                ambientColor = WarmAmber.copy(alpha = 0.3f),
                spotColor = WarmAmber.copy(alpha = 0.3f)
            )
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = Color.White),
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (icon) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase servings",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}
