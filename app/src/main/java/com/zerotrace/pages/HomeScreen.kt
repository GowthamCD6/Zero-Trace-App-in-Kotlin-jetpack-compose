package com.zerotrace.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HomeScreen(
    onNavigateToInstruction: () -> Unit,
    onNavigateToVerification: () -> Unit = {},
    userName: String = "User"
) {
    val coroutineScope = rememberCoroutineScope()

    // Enhanced animations
    val fadeAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(30f) }
    val headerScale = remember { Animatable(0.95f) }
    val contentOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.9f) }
    val buttonTranslation = remember { Animatable(40f) }

    // Tooltip visibility state
    var showTooltip by remember { mutableStateOf(true) }
    val tooltipAlpha = remember { Animatable(0f) }

    // Wave animation for emoji
    val waveRotation = remember { Animatable(0f) }

    // Professional blinking effect for verify button
    val blinkAlpha = remember { Animatable(1f) }

    // Card swipe animation state
    var selectedCardIndex by remember { mutableStateOf(0) }
    val cardOffsetX = remember { Animatable(0f) }
    val cardRotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(1000, easing = EaseOutCubic))
        slideAnim.animateTo(0f, animationSpec = tween(700, easing = EaseOutCubic))
        headerScale.animateTo(1f, animationSpec = tween(600, easing = EaseOutBack))

        delay(200)
        contentOpacity.animateTo(1f, animationSpec = tween(1000))

        delay(300)
        buttonScale.animateTo(1f, animationSpec = tween(500, easing = EaseOutBack))
        buttonTranslation.animateTo(0f, animationSpec = tween(500, easing = EaseOutCubic))

        // Show tooltip after animations
        delay(500)
        tooltipAlpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))

        // Auto-hide tooltip after 3 seconds
        delay(3000)
        if (showTooltip) {
            tooltipAlpha.animateTo(0f, animationSpec = tween(300))
            showTooltip = false
        }
    }

    // Wave animation effect for emoji
    LaunchedEffect(Unit) {
        while (true) {
            waveRotation.animateTo(
                targetValue = 20f,
                animationSpec = tween(150, easing = LinearEasing)
            )
            waveRotation.animateTo(
                targetValue = -20f,
                animationSpec = tween(150, easing = LinearEasing)
            )
            waveRotation.animateTo(
                targetValue = 15f,
                animationSpec = tween(150, easing = LinearEasing)
            )
            waveRotation.animateTo(
                targetValue = -15f,
                animationSpec = tween(150, easing = LinearEasing)
            )
            waveRotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(150, easing = LinearEasing)
            )
            delay(2000)
        }
    }

    // Professional blinking effect for verify button
    LaunchedEffect(Unit) {
        while (true) {
            blinkAlpha.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(1200, easing = EaseInOutCubic)
            )
            blinkAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, easing = EaseInOutCubic)
            )
            delay(300)
        }
    }

    val features = listOf(
        Feature(
            icon = Icons.Filled.Lock,
            title = "Guaranteed Data Destruction",
            description = "Military-grade cryptographic erasure following NIST standards for complete data sanitization.",
            color = Color(0xFFEF4444)
        ),
        Feature(
            icon = Icons.Filled.VerifiedUser,
            title = "Tamper-Proof Verification",
            description = "Generates digitally-signed certificates proving successful wipe completion for compliance.",
            color = Color(0xFF3B82F6)
        ),
        Feature(
            icon = Icons.Filled.PrivacyTip,
            title = "100% Offline & Secure",
            description = "Complete privacy protection - no cloud connectivity, all processes remain on your device.",
            color = Color(0xFF10B981)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFFFFFFF),
                        Color(0xFFF1F5F9)
                    )
                )
            )
            .alpha(fadeAnim.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (showTooltip) {
                            showTooltip = false
                            coroutineScope.launch {
                                tooltipAlpha.animateTo(0f, animationSpec = tween(300))
                            }
                        }
                    }
                )
            }
    ) {
        // Top background decoration
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDCE9FF),
                        Color(0xFFEFF6FF),
                        Color(0xFFF8FAFC)
                    )
                ),
                size = size
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 50.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with User Greeting and Verify Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = slideAnim.value.dp)
                        .alpha(headerScale.value),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // User Greeting with animated waving emoji
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👋",
                            fontSize =28.sp,
                            modifier = Modifier.graphicsLayer {
                                rotationZ = waveRotation.value
                                transformOrigin = TransformOrigin(0.7f, 0.7f)
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Hello, $userName!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    // Verify Button with professional blinking effect
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = blinkAlpha.value
                            }
                    ) {
                        Button(
                            onClick = {
                                if (showTooltip) {
                                    showTooltip = false
                                    coroutineScope.launch {
                                        tooltipAlpha.animateTo(0f, animationSpec = tween(300))
                                    }
                                }
                                onNavigateToVerification()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 2.dp
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .border(
                                    width = 1.5.dp,
                                    color = Color(0xFF3B82F6).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    spotColor = Color(0xFF3B82F6).copy(alpha = 0.3f)
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Verify",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Verify",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3B82F6),
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                    }
                }

                // Floating Tooltip with increased z-index
                if (showTooltip) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 0.dp, y = 56.dp)
                            .width(260.dp)
                            .zIndex(1000f)
                            .graphicsLayer {
                                alpha = tooltipAlpha.value
                                translationY = (1f - tooltipAlpha.value) * -20f
                            }
                    ) {
                        // Tooltip Arrow
                        Canvas(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-50).dp, y = (-8).dp)
                                .zIndex(1001f)
                        ) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width / 2, 0f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF1F2937)
                            )
                        }

                        // Tooltip Content
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1F2937)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                            modifier = Modifier.zIndex(1001f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Verify that all data is successfully erased after secure erase",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // App Title Header
            Column(
                modifier = Modifier
                    .scale(headerScale.value)
                    .offset(y = slideAnim.value.dp)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ZeroTrace",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF8B5CF6),
                                    Color(0xFF3B82F6)
                                )
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SECURE DATA ERASURE SOLUTION",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Main Description Card
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(contentOpacity.value)
                    .graphicsLayer {
                        translationY = (1f - contentOpacity.value) * 50f
                    }
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF3B82F6).copy(alpha = 0.2f),
                                        Color(0xFF3B82F6).copy(alpha = 0.05f)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Professional Data Sanitization",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ZeroTrace ensures complete and verifiable destruction of personal data before device disposal. Using advanced cryptographic erasure techniques, we render all information permanently irrecoverable while generating compliance-ready certification.",
                        fontSize = 15.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Features Section
            Column(
                modifier = Modifier.alpha(contentOpacity.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF3B82F6))
                                )
                            )
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Key Features",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF3B82F6), Color.Transparent)
                                )
                            )
                    )
                }

                // Swipeable Feature Cards
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    features.forEachIndexed { index, feature ->
                        val offset = (index - selectedCardIndex) * 1000f
                        val scale = if (index == selectedCardIndex) 1f else 0.85f
                        val alpha = if (abs(index - selectedCardIndex) <= 1) 1f else 0f

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationX = offset + cardOffsetX.value
                                    scaleX = scale
                                    scaleY = scale
                                    rotationZ =
                                        if (index == selectedCardIndex) cardRotation.value else 0f
                                    this.alpha = alpha
                                }
                                .zIndex(if (index == selectedCardIndex) 1f else 0f)
                                .pointerInput(selectedCardIndex) {
                                    detectHorizontalDragGestures(
                                        onDragEnd = {
                                            coroutineScope.launch {
                                                when {
                                                    cardOffsetX.value > 150 && selectedCardIndex > 0 -> {
                                                        selectedCardIndex--
                                                    }

                                                    cardOffsetX.value < -150 && selectedCardIndex < features.size - 1 -> {
                                                        selectedCardIndex++
                                                    }
                                                }
                                                cardOffsetX.animateTo(
                                                    0f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    )
                                                )
                                                cardRotation.animateTo(
                                                    0f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    )
                                                )
                                            }
                                        },
                                        onHorizontalDrag = { change, dragAmount ->
                                            if (index == selectedCardIndex) {
                                                change.consume()
                                                coroutineScope.launch {
                                                    cardOffsetX.snapTo(cardOffsetX.value + dragAmount)
                                                    cardRotation.snapTo(cardOffsetX.value * 0.02f)
                                                }
                                            }
                                        }
                                    )
                                }
                        ) {
                            ModernFeatureCard(feature = feature)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Card Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    features.forEachIndexed { index, _ ->
                        val isSelected = index == selectedCardIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(if (isSelected) 32.dp else 10.dp)
                                .height(10.dp)
                                .background(
                                    brush = if (isSelected) {
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFD1D5DB), Color(0xFFD1D5DB))
                                        )
                                    },
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .animateContentSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Security Badge
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0FDF4)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = contentOpacity.value
                            translationY = (1f - contentOpacity.value) * 30f
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF10B981),
                                            Color(0xFF059669)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(3.dp, Color.White, CircleShape)
                                .shadow(4.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Certified",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NIST SP 800-88 Certified",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Meets federal standards for media sanitization and data destruction",
                                fontSize = 13.sp,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Fixed Bottom Button Section
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.8f),
                            Color.White
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = buttonScale.value
                        scaleY = buttonScale.value
                        translationY = buttonTranslation.value
                    }
            ) {
                // Main Action Button
                Button(
                    onClick = onNavigateToInstruction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A),
                                        Color(0xFF1E293B),
                                        Color(0xFF334155),
                                        Color(0xFF1E293B),
                                        Color(0xFF0F172A)
                                    ),
                                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF3B82F6).copy(alpha = 0.6f),
                                        Color(0xFF8B5CF6).copy(alpha = 0.6f),
                                        Color(0xFF3B82F6).copy(alpha = 0.6f)
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Start",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Begin Secure Erasure",
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Arrow",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class Feature(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

@Composable
fun ModernFeatureCard(
    feature: Feature,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        feature.color.copy(alpha = 0.3f),
                        feature.color.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with gradient background
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                feature.color.copy(alpha = 0.2f),
                                feature.color.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                feature.color.copy(alpha = 0.4f),
                                feature.color.copy(alpha = 0.2f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = feature.color,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = feature.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = feature.description,
                fontSize = 15.sp,
                color = Color(0xFF475569),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp
            )
        }
    }
}
