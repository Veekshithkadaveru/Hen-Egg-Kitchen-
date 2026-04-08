package app.krafted.heneggkitchen.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.heneggkitchen.data.RecipeRepository
import app.krafted.heneggkitchen.data.models.Recipe
import app.krafted.heneggkitchen.ui.theme.TextPrimary
import app.krafted.heneggkitchen.ui.theme.TextSecondaryLight
import app.krafted.heneggkitchen.ui.theme.WarmAmber
import app.krafted.heneggkitchen.ui.theme.WarmOffWhite
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SurfaceColor = WarmOffWhite
private val TextSecondary = TextSecondaryLight

@Composable
fun CategoryScreen(
    categoryId: Int,
    repository: RecipeRepository,
    onRecipeClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val category = remember(categoryId) { repository.getCategoryById(categoryId) } ?: return
    val recipes = category.recipes
    val context = LocalContext.current
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val listState = rememberLazyListState()

    val accentColor = try {
        Color(android.graphics.Color.parseColor(category.accentColor))
    } catch (e: Exception) {
        Color(0xFFE0E0E0)
    }

    val backgroundResId = context.resources.getIdentifier(
        category.background, "drawable", context.packageName
    )

    val visibleItems = remember { mutableStateListOf<Int>() }

    LaunchedEffect(recipes) {
        visibleItems.clear()
        recipes.forEachIndexed { index, _ ->
            delay(80L + index * 60L)
            visibleItems.add(index)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollOffset = listState.firstVisibleItemScrollOffset
        val backgroundOffset = (scrollOffset * 0.3f).coerceAtMost(200f)
        val backgroundScale = 1f + (scrollOffset * 0.0002f).coerceAtMost(0.08f)

        if (backgroundResId != 0) {
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
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.75f),
                            0.12f to Color.Black.copy(alpha = 0.6f),
                            0.28f to Color(0xFF3E2723).copy(alpha = 0.35f),
                            0.45f to Color(0xFFFFF8F0).copy(alpha = 0.8f),
                            0.6f to Color(0xFFFFF8F0).copy(alpha = 0.95f),
                            1.0f to Color(0xFFFFF8F0)
                        )
                    )
                )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(statusBarPadding.calculateTopPadding()))

                CategoryHeader(
                    categoryName = category.name,
                    recipeCount = recipes.size,
                    categoryIndex = categoryId,
                    accentColor = accentColor,
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            itemsIndexed(recipes, key = { _, recipe -> recipe.id }) { index, recipe ->
                val itemAlpha = remember { Animatable(0f) }
                val itemTranslationY = remember { Animatable(60f) }
                val itemScale = remember { Animatable(0.95f) }

                LaunchedEffect(visibleItems.contains(index)) {
                    if (visibleItems.contains(index)) {
                        coroutineScope {
                            launch { itemAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }
                            launch { itemTranslationY.animateTo(0f, spring(dampingRatio = 0.65f, stiffness = 180f)) }
                            launch { itemScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 200f)) }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = itemAlpha.value
                            translationY = itemTranslationY.value
                            scaleX = itemScale.value
                            scaleY = itemScale.value
                        }
                        .padding(horizontal = 24.dp)
                        .padding(bottom = if (index == recipes.lastIndex) 40.dp else 14.dp)
                ) {
                    RecipeCard(
                        recipe = recipe,
                        accentColor = accentColor,
                        onClick = { onRecipeClick(recipe.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    categoryName: String,
    recipeCount: Int,
    categoryIndex: Int,
    accentColor: Color,
    onBackClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.82f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "back_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.White.copy(alpha = 0.12f)
                        )
                    )
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.material3.ripple(),
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                fontSize = 32.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.2).sp,
                color = Color.White,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$categoryIndex",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$recipeCount recipes",
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            color = TextSecondary.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium),
        label = "recipe_card_scale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 6f,
        animationSpec = tween(200),
        label = "recipe_card_elevation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "card_shimmer_${recipe.id}")
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_sweep"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(88.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
                    .background(accentColor.copy(alpha = 0.7f))
                    .align(Alignment.CenterVertically)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .drawBehind {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                start = Offset(shimmerX, 0f),
                                end = Offset(shimmerX + 250f, size.height)
                            )
                        )
                    }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recipe.title,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetadataPill(
                            text = "${recipe.prepMins}m prep",
                            accentColor = accentColor
                        )
                        MetadataPill(
                            text = "${recipe.cookMins}m cook",
                            accentColor = accentColor
                        )
                        MetadataPill(
                            text = recipe.difficulty,
                            accentColor = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataPill(
    text: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 0.2.sp
        )
    }
}
