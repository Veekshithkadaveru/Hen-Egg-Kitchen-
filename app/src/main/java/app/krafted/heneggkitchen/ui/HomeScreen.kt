package app.krafted.heneggkitchen.ui

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.heneggkitchen.R
import app.krafted.heneggkitchen.data.models.RecipeCategory
import app.krafted.heneggkitchen.ui.navigation.Screen
import app.krafted.heneggkitchen.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

private val SurfaceColor = Color(0xFFFFFDF7)
private val TextPrimary = Color(0xFF1E2238)
private val TextSecondary = Color(0xFFE2E8F0)
private val SearchHint = Color(0xFF9CA3AF)
private val CategoryIconBackgroundExceptions = setOf("Baking with Eggs", "Quick & Easy")
private const val CategoryIconWhiteThreshold = 238
private const val TrimmedCategoryIconScale = 1.25f

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val gridState = rememberLazyStaggeredGridState()

    var isHeaderVisible by remember { mutableStateOf(false) }
    var isGridVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isHeaderVisible = true
        delay(200)
        isGridVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollOffset = gridState.firstVisibleItemScrollOffset
        val backgroundOffset = (scrollOffset * 0.3f).coerceAtMost(200f)
        val backgroundScale = 1f + (scrollOffset * 0.0002f).coerceAtMost(0.08f)

        Image(
            painter = painterResource(id = R.drawable.chick19_back_1),
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
                            0.0f to Color.Black.copy(alpha = 0.7f),
                            0.15f to Color.Black.copy(alpha = 0.55f),
                            0.35f to Color(0xFF3E2723).copy(alpha = 0.3f),
                            0.55f to Color(0xFFFFF8F0).copy(alpha = 0.85f),
                            0.7f to Color(0xFFFFF8F0).copy(alpha = 0.97f),
                            1.0f to Color(0xFFFFF8F0)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(statusBarPadding.calculateTopPadding()))

            AnimatedVisibility(
                visible = isHeaderVisible,
                enter = slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialOffsetY = { -100 }
                ) + fadeIn(tween(500, easing = FastOutSlowInEasing))
            ) {
                Column {
                    HomeHeader(
                        onBookmarksClick = { onNavigate(Screen.Bookmarks.route) }
                    )

                    SearchBar(
                        onClick = { onNavigate(Screen.Search.route) }
                    )
                }
            }

            AnimatedVisibility(
                visible = isGridVisible,
                enter = fadeIn(tween(400)) + slideInVertically(
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                    initialOffsetY = { 40 }
                )
            ) {
                Column {
                    Text(
                        text = "Discover",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    CategoryGrid(
                        categories = state.categories,
                        gridState = gridState,
                        onCategoryClick = { categoryId ->
                            onNavigate(Screen.Category.createRoute(categoryId))
                        }
                    )
                }
            }
        }
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

@Composable
private fun rememberCategoryIconBitmap(
    iconResId: Int,
    shouldTrimWhiteBackground: Boolean
): ImageBitmap? {
    val context = LocalContext.current

    return remember(context, iconResId, shouldTrimWhiteBackground) {
        if (iconResId == 0 || !shouldTrimWhiteBackground) {
            return@remember null
        }

        val sourceBitmap = BitmapFactory.decodeResource(context.resources, iconResId)
            ?: return@remember null
        val workingBitmap = sourceBitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true)
            ?: return@remember null

        if (workingBitmap != sourceBitmap) {
            sourceBitmap.recycle()
        }

        workingBitmap.setHasAlpha(true)
        trimEdgeWhitePixels(bitmap = workingBitmap, threshold = CategoryIconWhiteThreshold)
        workingBitmap.asImageBitmap()
    }
}

private fun trimEdgeWhitePixels(
    bitmap: android.graphics.Bitmap,
    threshold: Int
) {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= 0 || height <= 0) return

    val pixels = IntArray(width * height)
    val visited = BooleanArray(width * height)
    val queue = IntArray(width * height)
    var head = 0
    var tail = 0

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    fun enqueue(index: Int) {
        if (visited[index] || !isEdgeWhitePixel(pixels[index], threshold)) return
        visited[index] = true
        queue[tail++] = index
    }

    for (x in 0 until width) {
        enqueue(x)
        enqueue((height - 1) * width + x)
    }
    for (y in 0 until height) {
        enqueue(y * width)
        enqueue(y * width + (width - 1))
    }

    while (head < tail) {
        val index = queue[head++]
        val x = index % width
        val y = index / width
        val pixel = pixels[index]

        pixels[index] = android.graphics.Color.argb(
            0,
            android.graphics.Color.red(pixel),
            android.graphics.Color.green(pixel),
            android.graphics.Color.blue(pixel)
        )

        if (x > 0) enqueue(index - 1)
        if (x < width - 1) enqueue(index + 1)
        if (y > 0) enqueue(index - width)
        if (y < height - 1) enqueue(index + width)
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
}

private fun isEdgeWhitePixel(pixel: Int, threshold: Int): Boolean {
    if (android.graphics.Color.alpha(pixel) == 0) return true

    return android.graphics.Color.red(pixel) >= threshold &&
        android.graphics.Color.green(pixel) >= threshold &&
        android.graphics.Color.blue(pixel) >= threshold
}

@Composable
private fun HomeHeader(
    onBookmarksClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.82f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "bookmark_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "header_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${getGreeting()}, Chef!",
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                color = TextSecondary.copy(alpha = 0.85f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "What's cooking?",
                fontSize = 32.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.2).sp,
                color = Color.White,
                lineHeight = 36.sp
            )
        }

        Box(
            modifier = Modifier
                .size(52.dp)
                .scale(scale)
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = glowAlpha),
                        radius = size.minDimension / 1.6f
                    )
                }
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
                    onClick = onBookmarksClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "Bookmarks",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "search_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "search_shimmer")
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.88f))
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerX, 0f),
                        end = Offset(shimmerX + 200f, size.height)
                    )
                )
            }
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = SearchHint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Find delicious recipes\u2026",
                color = SearchHint,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<RecipeCategory>,
    gridState: androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState,
    onCategoryClick: (Int) -> Unit
) {
    val visibleItems = remember { mutableStateListOf<Int>() }

    LaunchedEffect(categories) {
        visibleItems.clear()
        categories.forEachIndexed { index, _ ->
            delay(80L + index * 50L)
            visibleItems.add(index)
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalItemSpacing = 14.dp
    ) {
        itemsIndexed(categories, key = { _, cat -> cat.id }) { index, category ->
            val itemAlpha = remember { Animatable(0f) }
            val itemTranslationY = remember { Animatable(60f) }

            LaunchedEffect(visibleItems.contains(index)) {
                if (visibleItems.contains(index)) {
                    kotlinx.coroutines.coroutineScope {
                        launch { itemAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }
                        launch { itemTranslationY.animateTo(0f, spring(dampingRatio = 0.65f, stiffness = 180f)) }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = itemAlpha.value
                        translationY = itemTranslationY.value
                    }
            ) {
                CategoryStaggeredCard(
                    category = category,
                    height = 230.dp,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryStaggeredCard(
    category: RecipeCategory,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val accentColor = try {
        Color(android.graphics.Color.parseColor(category.accentColor))
    } catch (e: Exception) {
        Color(0xFFE0E0E0)
    }

    val iconResId = context.resources.getIdentifier(
        category.icon, "drawable", context.packageName
    )
    val shouldTrimWhiteBackground = category.name !in CategoryIconBackgroundExceptions
    val iconScale = if (shouldTrimWhiteBackground) TrimmedCategoryIconScale else 1f
    val processedIconBitmap = rememberCategoryIconBitmap(
        iconResId = iconResId,
        shouldTrimWhiteBackground = shouldTrimWhiteBackground
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium),
        label = "card_scale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 8f,
        animationSpec = tween(200),
        label = "card_elevation"
    )

    // Floating animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "float_${ category.id }")
    val iconOffsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200 + (category.id * 100) % 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_offset"
    )
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800 + (category.id * 130) % 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.3f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.22f),
                                accentColor.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(0.5f, 0.4f),
                            radius = 400f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .offset(y = iconOffsetY.dp)
                        .drawBehind {
                            drawCircle(
                                color = accentColor.copy(alpha = 0.12f),
                                radius = size.minDimension * 0.9f
                            )
                        }
                )

                if (iconResId != 0) {
                    val iconModifier = Modifier
                        .size(76.dp)
                        .offset(y = iconOffsetY.dp)
                        .graphicsLayer {
                            rotationZ = iconRotation
                            scaleX = iconScale
                            scaleY = iconScale
                        }

                    if (processedIconBitmap != null) {
                        Image(
                            bitmap = processedIconBitmap,
                            contentDescription = category.name,
                            modifier = iconModifier,
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Image(
                            painter = painterResource(id = iconResId),
                            contentDescription = category.name,
                            modifier = iconModifier,
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .background(SurfaceColor)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${category.recipes.size} recipes",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            letterSpacing = 0.2.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "→",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }
        }
    }
}
