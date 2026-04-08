package app.krafted.heneggkitchen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.heneggkitchen.ui.theme.TextPrimary
import app.krafted.heneggkitchen.ui.theme.TextSecondary
import app.krafted.heneggkitchen.ui.theme.WarmAmber
import app.krafted.heneggkitchen.ui.theme.WarmCream
import app.krafted.heneggkitchen.ui.theme.WarmOffWhite
import app.krafted.heneggkitchen.viewmodel.CookingViewModel
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.rememberPageCurlState
import kotlinx.coroutines.delay


@OptIn(ExperimentalPageCurlApi::class)
@Composable
fun CookingModeScreen(
    viewModel: CookingViewModel,
    recipeId: Int,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    val pageCurlState = rememberPageCurlState(initialCurrent = 0)

    val recipe = state.recipe ?: return

    val ingredientNames = remember(recipe) { recipe.ingredients.map { it.name } }
    val context = LocalContext.current
    val backgroundResId = state.categoryBackground?.let {
        context.resources.getIdentifier(it, "drawable", context.packageName)
    } ?: 0

    var hasInteracted by remember { mutableStateOf(false) }
    val hintAlpha = remember { Animatable(1f) }

    LaunchedEffect(pageCurlState.current) {
        if (pageCurlState.current > 0 && !hasInteracted) {
            hasInteracted = true
            hintAlpha.animateTo(0f, tween(500))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (backgroundResId != 0) {
            Image(
                painter = painterResource(id = backgroundResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Black.copy(alpha = 0.65f),
                                0.1f to Color.Black.copy(alpha = 0.5f),
                                0.2f to WarmCream.copy(alpha = 0.7f),
                                0.35f to WarmCream.copy(alpha = 0.92f),
                                0.5f to WarmCream.copy(alpha = 0.98f),
                                1.0f to WarmCream
                            )
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                WarmAmber.copy(alpha = 0.03f),
                                WarmCream
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Spacer(modifier = Modifier.height(statusBarPadding.calculateTopPadding()))

            CookingHeader(
                recipeTitle = recipe.title,
                currentStep = pageCurlState.current,
                totalSteps = recipe.steps.size,
                onBackClick = onBackClick,
                hasBackground = backgroundResId != 0
            )

            StepProgressBar(
                currentStep = pageCurlState.current,
                totalSteps = recipe.steps.size
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                PageCurl(
                    count = recipe.steps.size,
                    state = pageCurlState,
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .border(
                                width = 0.5.dp,
                                color = Color.Black.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmOffWhite)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.TopStart)
                                    .clip(CircleShape)
                                    .background(WarmAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WarmAmber
                                )
                            }

                            Text(
                                text = "\u201C",
                                fontSize = 48.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Light,
                                color = WarmAmber.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(y = (-8).dp)
                            )

                            Text(
                                text = recipe.steps[index],
                                fontSize = 22.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary.copy(alpha = 0.85f),
                                lineHeight = 32.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }

                if (!hasInteracted) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .graphicsLayer { alpha = hintAlpha.value }
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .graphicsLayer { alpha = hintAlpha.value }
                    )
                }

                Text(
                    text = "curl page to turn",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary.copy(alpha = 0.4f),
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = TextPrimary.copy(alpha = 0.06f)
            )

            IngredientsReminder(
                ingredients = ingredientNames
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CookingHeader(
    recipeTitle: String,
    currentStep: Int,
    totalSteps: Int,
    onBackClick: () -> Unit,
    hasBackground: Boolean = false
) {
    val backInteraction = remember { MutableInteractionSource() }
    val backPressed by backInteraction.collectIsPressedAsState()
    val backScale by animateFloatAsState(
        targetValue = if (backPressed) 0.82f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
        label = "back_scale"
    )

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

            Text(
                text = recipeTitle,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = if (hasBackground) Color.White else TextPrimary,
                letterSpacing = (-0.3).sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WarmAmber
                )) {
                    append("Step ${currentStep + 1}")
                }
                withStyle(SpanStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )) {
                    append(" of $totalSteps")
                }
            },
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
private fun StepProgressBar(
    currentStep: Int,
    totalSteps: Int
) {
    val progress by animateFloatAsState(
        targetValue = if (totalSteps > 0) (currentStep + 1).toFloat() / totalSteps else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(TextPrimary.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(WarmAmber)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .drawBehind {
                    if (totalSteps > 1) {
                        for (step in 0 until totalSteps) {
                            val stepFraction = (step + 1).toFloat() / totalSteps
                            val x = size.width * stepFraction
                            val isCurrent = step == currentStep
                            val isCompleted = step < currentStep
                            val dotRadius = if (isCurrent) 5f else 3f
                            val dotColor = if (isCompleted || isCurrent)
                                Color.White
                            else
                                Color.White.copy(alpha = 0.4f)

                            drawCircle(
                                color = dotColor,
                                radius = dotRadius,
                                center = Offset(x.coerceIn(dotRadius, size.width - dotRadius), size.height / 2f)
                            )
                        }
                    }
                }
        )
    }
}

@Composable
private fun IngredientsReminder(
    ingredients: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "ingredients_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = WarmOffWhite)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(),
                        onClick = { expanded = !expanded }
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ingredients",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WarmAmber.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${ingredients.size}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber.copy(alpha = 0.85f)
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp
                    else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)
                ) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        color = TextPrimary.copy(alpha = 0.06f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    IngredientsStaggeredList(ingredients = ingredients)
                }
            }
        }
    }
}

@Composable
private fun IngredientsStaggeredList(ingredients: List<String>) {
    val itemAlphas = remember(ingredients) {
        ingredients.map { Animatable(0f) }
    }

    LaunchedEffect(ingredients) {
        ingredients.forEachIndexed { index, _ ->
            delay(40L * index)
            itemAlphas[index].animateTo(
                1f,
                tween(250, easing = FastOutSlowInEasing)
            )
        }
    }

    Column {
        ingredients.forEachIndexed { index, name ->
            Text(
                text = if (index < ingredients.lastIndex) "$name  \u00B7  " else name,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                lineHeight = 22.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = itemAlphas[index].value
                }
            )
        }
    }
}
