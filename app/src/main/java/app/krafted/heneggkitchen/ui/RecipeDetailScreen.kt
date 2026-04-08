package app.krafted.heneggkitchen.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.heneggkitchen.data.formatAmount
import app.krafted.heneggkitchen.data.models.Ingredient
import app.krafted.heneggkitchen.ui.components.ServingAdjuster
import app.krafted.heneggkitchen.ui.theme.DarkBrown
import app.krafted.heneggkitchen.ui.theme.DetailPillBrown
import app.krafted.heneggkitchen.ui.theme.StepNumberBg
import app.krafted.heneggkitchen.ui.theme.TextPrimary
import app.krafted.heneggkitchen.ui.theme.TextSecondary
import app.krafted.heneggkitchen.ui.theme.WarmAmber
import app.krafted.heneggkitchen.ui.theme.WarmAmberLight
import app.krafted.heneggkitchen.ui.theme.WarmCream
import app.krafted.heneggkitchen.ui.theme.WarmOffWhite
import app.krafted.heneggkitchen.viewmodel.RecipeViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SurfaceColor = WarmOffWhite
private val AccentAmber = WarmAmber
private val BackgroundWarm = WarmCream

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeViewModel,
    recipeId: Int,
    onBackClick: () -> Unit,
    onStartCooking: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val scrollState = rememberScrollState()
    val contentAlpha = remember { Animatable(0f) }
    val contentTranslationY = remember { Animatable(40f) }

    val metadataAlpha = remember { Animatable(0f) }
    val servingsAlpha = remember { Animatable(0f) }
    val ingredientsAlpha = remember { Animatable(0f) }
    val stepsAlpha = remember { Animatable(0f) }
    val tipAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    val recipe = state.recipe ?: return
    val context = LocalContext.current
    val backgroundResId = state.categoryBackground?.let {
        context.resources.getIdentifier(it, "drawable", context.packageName)
    } ?: 0

    LaunchedEffect(recipe) {
        delay(150)
        coroutineScope {
            launch { contentAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
            launch { contentTranslationY.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 200f)) }
        }
        delay(100)
        launch { metadataAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
        delay(80)
        launch { servingsAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
        delay(80)
        launch { ingredientsAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
        delay(80)
        launch { stepsAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
        delay(80)
        launch { tipAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
        delay(80)
        launch { buttonAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWarm)
    ) {
        if (backgroundResId != 0) {
            val scrollOffset = scrollState.value
            val backgroundOffset = (scrollOffset * 0.3f).coerceAtMost(200f)
            val backgroundScale = 1f + (scrollOffset * 0.0002f).coerceAtMost(0.08f)

            Image(
                painter = painterResource(id = backgroundResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = backgroundScale
                        scaleY = backgroundScale
                        translationY = -backgroundOffset
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Black.copy(alpha = 0.3f),
                                0.12f to Color.Black.copy(alpha = 0.15f),
                                0.25f to DarkBrown.copy(alpha = 0.1f),
                                0.4f to BackgroundWarm.copy(alpha = 0.85f),
                                0.55f to BackgroundWarm.copy(alpha = 0.97f),
                                1.0f to BackgroundWarm
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(statusBarPadding.calculateTopPadding()))

            DetailHeader(
                title = recipe.title,
                isBookmarked = state.isBookmarked,
                onBackClick = onBackClick,
                onBookmarkToggle = { viewModel.toggleBookmark() },
                hasBackground = backgroundResId != 0
            )

            Column(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentTranslationY.value
                    }
            ) {
                Box(
                    modifier = Modifier.graphicsLayer { alpha = metadataAlpha.value }
                ) {
                    MetadataRow(
                        prepMins = recipe.prepMins,
                        cookMins = recipe.cookMins,
                        difficulty = recipe.difficulty,
                        tags = recipe.tags
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.graphicsLayer { alpha = servingsAlpha.value }
                ) {
                    ServingsSection(
                        servings = state.currentServings,
                        onDecrement = { viewModel.updateServings(state.currentServings - 1) },
                        onIncrement = { viewModel.updateServings(state.currentServings + 1) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.graphicsLayer { alpha = ingredientsAlpha.value }
                ) {
                    IngredientsSection(ingredients = state.scaledIngredients)
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier.graphicsLayer { alpha = stepsAlpha.value }
                ) {
                    StepsSection(steps = recipe.steps)
                }

                if (recipe.tip.isNotBlank()) {
                    Spacer(modifier = Modifier.height(28.dp))
                    Box(
                        modifier = Modifier.graphicsLayer { alpha = tipAlpha.value }
                    ) {
                        TipSection(tip = recipe.tip)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier.graphicsLayer { alpha = buttonAlpha.value }
                ) {
                    StartCookingButton(onClick = onStartCooking)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun DetailHeader(
    title: String,
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    hasBackground: Boolean = false
) {
    val backInteraction = remember { MutableInteractionSource() }
    val backPressed by backInteraction.collectIsPressedAsState()
    val backScale by animateFloatAsState(
        targetValue = if (backPressed) 0.82f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "back_scale"
    )

    val bookmarkInteraction = remember { MutableInteractionSource() }
    val bookmarkPressed by bookmarkInteraction.collectIsPressedAsState()
    val bookmarkScale by animateFloatAsState(
        targetValue = if (bookmarkPressed) 0.75f else if (isBookmarked) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMedium),
        label = "bookmark_scale"
    )

    val bookmarkPulse = remember { Animatable(1f) }
    LaunchedEffect(isBookmarked) {
        if (isBookmarked) {
            bookmarkPulse.animateTo(
                1.3f,
                spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessMediumLow)
            )
            bookmarkPulse.animateTo(
                1f,
                spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(backScale)
                    .clip(CircleShape)
                    .background(
                        if (hasBackground) Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.28f),
                                Color.White.copy(alpha = 0.12f)
                            )
                        ) else Brush.linearGradient(
                            colors = listOf(
                                TextPrimary.copy(alpha = 0.08f),
                                TextPrimary.copy(alpha = 0.04f)
                            )
                        )
                    )
                    .clickable(
                        interactionSource = backInteraction,
                        indication = androidx.compose.material3.ripple(),
                        onClick = onBackClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = if (hasBackground) Color.White else TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(bookmarkScale * bookmarkPulse.value)
                    .clip(CircleShape)
                    .background(
                        if (isBookmarked) AccentAmber.copy(alpha = 0.12f)
                        else if (hasBackground) Color.White.copy(alpha = 0.15f)
                        else TextPrimary.copy(alpha = 0.06f)
                    )
                    .clickable(
                        interactionSource = bookmarkInteraction,
                        indication = androidx.compose.material3.ripple(),
                        onClick = onBookmarkToggle
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (isBookmarked) AccentAmber else if (hasBackground) Color.White else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            fontSize = 28.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.8).sp,
            color = if (hasBackground) Color.White else TextPrimary,
            lineHeight = 34.sp
        )
    }
}

@Composable
private fun MetadataRow(
    prepMins: Int,
    cookMins: Int,
    difficulty: String,
    tags: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailPill(text = "${prepMins}m prep")
        DetailPill(text = "${cookMins}m cook")
        DetailPill(text = difficulty)
        tags.firstOrNull()?.let { tag ->
            DetailPill(text = tag)
        }
    }
}

@Composable
private fun DetailPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DetailPillBrown)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 0.2.sp
        )
    }
}

@Composable
private fun ServingsSection(
    servings: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Servings",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "Adjust to your needs",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }

            ServingAdjuster(
                servings = servings,
                onDecrement = onDecrement,
                onIncrement = onIncrement
            )
        }
    }
}

@Composable
private fun IngredientsSection(ingredients: List<Ingredient>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ingredients",
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.4).sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(AccentAmber.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentAmber.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${ingredients.size}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = AccentAmber.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                ingredients.forEachIndexed { index, ingredient ->
                    IngredientItem(ingredient = ingredient)
                    if (index < ingredients.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = TextPrimary.copy(alpha = 0.06f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientItem(ingredient: Ingredient) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient.name,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${formatAmount(ingredient.amount)} ${ingredient.unit}",
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            color = AccentAmber.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun StepsSection(steps: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Steps",
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.4).sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(AccentAmber.copy(alpha = 0.5f))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        steps.forEachIndexed { index, step ->
            StepItem(stepNumber = index + 1, text = step)
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .width(2.dp)
                        .height(12.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    AccentAmber.copy(alpha = 0.2f),
                                    AccentAmber.copy(alpha = 0.08f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun StepItem(stepNumber: Int, text: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                WarmAmberLight.copy(alpha = 0.35f),
                                StepNumberBg
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stepNumber",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            Text(
                text = text,
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                color = TextPrimary.copy(alpha = 0.85f),
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TipSection(tip: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = AccentAmber.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\uD83D\uDCA1",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tip",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentAmber.copy(alpha = 0.9f),
                    letterSpacing = (-0.2).sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tip,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                color = TextPrimary.copy(alpha = 0.75f),
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun StartCookingButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium),
        label = "cooking_btn_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "button_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .drawBehind {
                    drawRoundRect(
                        color = AccentAmber.copy(alpha = glowAlpha),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(size.width + 8.dp.toPx(), size.height + 8.dp.toPx()),
                        topLeft = Offset(-4.dp.toPx(), -4.dp.toPx())
                    )
                }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            AccentAmber,
                            AccentAmber.copy(alpha = 0.85f)
                        )
                    )
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.material3.ripple(),
                    onClick = onClick
                )
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "START COOKING MODE",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
