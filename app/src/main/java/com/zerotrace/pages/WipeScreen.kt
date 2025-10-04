package com.zerotrace.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.zerotrace.utils.FactoryResetManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// App-consistent Color Palette (aligned with HomeScreen)
private val BackgroundPrimary = Color(0xFFFAFBFC) // very light
private val BackgroundSecondary = Color(0xFFF8FAFC) // subtle gradient
private val CardBackground = Color(0xFFFFFFFF) // white cards
private val AccentBlue = Color(0xFF3B82F6)
private val AccentRed = Color(0xFFEF4444)
private val AccentAmber = Color(0xFFFBBF24)
private val TextPrimary = Color(0xFF0F172A) // dark text
private val TextSecondary = Color(0xFF64748B) // muted dark
private val TextMuted = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

@Composable
fun WipeScreen(onNavigateBack: () -> Unit) {
    var particleAnimations by remember { mutableStateOf(listOf<ParticleAnimation>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    
    // Gentle breathing animation for the button
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    
    // Rotating orbit animation
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundPrimary, BackgroundSecondary)
                )
            )
    ) {
        SubtleBackgroundPattern()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderSection()
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                InformationCard()
                
                WipeButtonSection(
                    breatheScale = breatheScale,
                    orbitRotation = orbitRotation,
                    particleAnimations = particleAnimations,
                    onButtonPress = {
                        scope.launch {
                            // Create particle burst effect
                            val particles = (0..8).map { 
                                ParticleAnimation(angle = it * 40f)
                            }
                            particleAnimations = particles
                            
                            delay(1000)
                            particleAnimations = emptyList()
                            
                            val resetResult = FactoryResetManager.performFactoryReset(context)
                            if (resetResult) {
                                Toast.makeText(
                                    context, 
                                    "Factory reset initiated...", 
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context, 
                                    "Factory reset failed. Please enable device admin.", 
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )
            }
            
            FooterSection()
        }
    }
}

@Composable
private fun SubtleBackgroundPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 60.dp.toPx()
        
        // Subtle grid pattern
        for (x in 0 until (size.width / gridSize).toInt()) {
            for (y in 0 until (size.height / gridSize).toInt()) {
                drawCircle(
                    color = Color(0x08FFFFFF),
                    radius = 2.dp.toPx(),
                    center = Offset(x * gridSize, y * gridSize)
                )
            }
        }
        
        // Accent glow - top right
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x1A3B82F6),
                    Color(0x00000000)
                )
            ),
            radius = 280.dp.toPx(),
            center = Offset(size.width * 0.9f, size.height * 0.1f)
        )
        
        // Accent glow - bottom left
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x12DC2626),
                    Color(0x00000000)
                )
            ),
            radius = 240.dp.toPx(),
            center = Offset(size.width * 0.15f, size.height * 0.85f)
        )
    }
}

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "ZeroTrace",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(2.dp)
                .background(AccentBlue, RoundedCornerShape(1.dp))
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Secure Data Erasure",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun InformationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentAmber, CircleShape)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "System Ready",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentAmber,
                    letterSpacing = 0.5.sp
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "⚠",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Text(
                    text = "Important Warning",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            Text(
                text = "This action will permanently erase all data on your device. This operation cannot be reversed or undone.",
                fontSize = 15.sp,
                color = TextSecondary,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Please ensure you have:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            
            Column(
                modifier = Modifier.padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoBullet("Backed up all important files")
                InfoBullet("Saved your photos and documents")
                InfoBullet("Logged out of all accounts")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(
                color = BorderColor,
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Target Device",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This Device",
                        fontSize = 16.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentRed.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentRed,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .background(AccentBlue, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WipeButtonSection(
    breatheScale: Float,
    orbitRotation: Float,
    particleAnimations: List<ParticleAnimation>,
    onButtonPress: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Initialize Data Wipe",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "Tap the button below to begin the secure erasure process",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        
    Spacer(modifier = Modifier.height(12.dp))
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(180.dp)
        ) {
            // Orbiting particles
            OrbitingElements(rotation = orbitRotation)
            
            // Burst particles
            particleAnimations.forEach { particle ->
                BurstParticle(particle)
            }
            
            // Outer glow ring
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(breatheScale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AccentRed.copy(alpha = 0.14f),
                                AccentRed.copy(alpha = 0.00f)
                            )
                        ),
                        CircleShape
                    )
            )
            
            // Main circular button
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clickable(
                        onClick = onButtonPress,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                shape = CircleShape,
                color = AccentRed,
                shadowElevation = 16.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Power/Delete icon using Canvas
                    Canvas(modifier = Modifier.size(48.dp)) {
                        val strokeWidth = 4.dp.toPx()
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val radius = size.width / 3
                        
                        // Draw circle with gap at top
                        drawArc(
                            color = Color.White,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        
                        // Draw vertical line (power symbol)
                        drawLine(
                            color = Color.White,
                            start = Offset(centerX, centerY - radius),
                            end = Offset(centerX, centerY),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
            
            // Inner rotating ring
            Canvas(
                modifier = Modifier
                    .size(135.dp)
                    .rotate(orbitRotation)
            ) {
                drawArc(
                    color = AccentRed.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                
                drawArc(
                    color = AccentRed.copy(alpha = 0.2f),
                    startAngle = 180f,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
private fun OrbitingElements(rotation: Float) {
    Canvas(modifier = Modifier.size(160.dp)) {
        val radius = 80.dp.toPx()
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        for (i in 0..2) {
            val angle = Math.toRadians((rotation + i * 120).toDouble())
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()
            
            drawCircle(
                color = AccentRed.copy(alpha = 0.3f),
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun BurstParticle(particle: ParticleAnimation) {
    val distance by animateFloatAsState(
        targetValue = 80f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "particle_distance"
    )
    
    val alpha by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "particle_alpha"
    )
    
    val angle = Math.toRadians(particle.angle.toDouble())
    val offsetX = (distance * cos(angle)).dp
    val offsetY = (distance * sin(angle)).dp
    
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(8.dp)
            .alpha(alpha)
            .background(AccentRed, CircleShape)
    )
}

@Composable
private fun FooterSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(AccentBlue, CircleShape)
            )
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Text(
                text = "Device will restart automatically after completion",
                fontSize = 13.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class ParticleAnimation(
    val angle: Float,
    val id: Long = System.currentTimeMillis() + angle.toLong()
)