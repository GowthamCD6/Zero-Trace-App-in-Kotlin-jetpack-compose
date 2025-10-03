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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.zerotrace.utils.FactoryResetManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Modern Color Palette
private val DarkBg = Color(0xFF0A0E1A)
private val CardBg = Color(0xFF141B2E)
private val PrimaryAccent = Color(0xFF6366F1)
private val DangerAccent = Color(0xFFEF4444)
private val WarningAccent = Color(0xFFF59E0B)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF94A3B8)
private val TextTertiary = Color(0xFF64748B)

@Composable
fun WipeScreen(onNavigateBack: () -> Unit) {
    var rippleAnimations by remember { mutableStateOf(listOf<RippleAnimation>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val fadeAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "fade"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBg, Color(0xFF0F1628))
                )
            )
            .alpha(fadeAnim)
    ) {
        BackgroundElements()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderSection()
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusCard()
                
                ActionSection(
                    pulseAnim = pulseAnim,
                    rippleAnimations = rippleAnimations,
                    onButtonPress = {
                        scope.launch {
                            val newRipple = RippleAnimation()
                            rippleAnimations = rippleAnimations + newRipple
                            
                            delay(1500)
                            rippleAnimations = rippleAnimations.filter { it.id != newRipple.id }
                            
                            val resetResult = FactoryResetManager.performFactoryReset(context)
                            if (resetResult) {
                                Toast.makeText(context, "Factory reset initiated...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Factory reset failed. Enable device admin.", Toast.LENGTH_LONG).show()
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
private fun BackgroundElements() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x266366F1),
                    Color(0x00000000)
                )
            ),
            radius = 300.dp.toPx(),
            center = Offset(size.width * 0.85f, size.height * 0.15f)
        )
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x1AEF4444),
                    Color(0x00000000)
                )
            ),
            radius = 250.dp.toPx(),
            center = Offset(size.width * 0.1f, size.height * 0.7f)
        )
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x1A8B5CF6),
                    Color(0x00000000)
                )
            ),
            radius = 200.dp.toPx(),
            center = Offset(size.width * 0.5f, size.height * 0.4f)
        )
    }
}

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 20.dp, bottom = 32.dp)
    ) {
        Text(
            text = "ZEROTRACE",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 6.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(PrimaryAccent, DangerAccent)
                    ),
                    RoundedCornerShape(2.dp)
                )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "SECURE WIPE PROTOCOL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 3.sp
        )
    }
}

@Composable
private fun StatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(WarningAccent, CircleShape)
                        .border(2.dp, Color(0x33F59E0B), CircleShape)
                )
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Text(
                    text = "SYSTEM READY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WarningAccent,
                    letterSpacing = 2.sp
                )
            }
            
            Text(
                text = "⚠️",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "CRITICAL WARNING",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DangerAccent,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "This action will permanently erase ALL data on your device. The operation is irreversible and cannot be undone. Please ensure all important information has been securely backed up.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Divider(
                color = Color(0x1AFFFFFF),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Target Device",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Current Device",
                        fontSize = 15.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x1AEF4444)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerAccent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionSection(
    pulseAnim: Float,
    rippleAnimations: List<RippleAnimation>,
    onButtonPress: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Initialize Data Wipe",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap the button to begin the secure erasure process",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            rippleAnimations.forEach { ripple ->
                RippleEffect(ripple)
            }
            
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulseAnim)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0x33EF4444),
                                Color(0x00EF4444)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .clickable(
                            onClick = onButtonPress,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    shape = CircleShape,
                    color = DangerAccent,
                    shadowElevation = 20.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "WIPE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 3.sp
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Surface(
                            shape = CircleShape,
                            color = Color(0x33FFFFFF),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "⚡",
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RippleEffect(ripple: RippleAnimation) {
    val scale by animateFloatAsState(
        targetValue = 3.5f,
        animationSpec = tween(1500, easing = LinearOutSlowInEasing),
        label = "ripple_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "ripple_alpha"
    )
    
    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale)
            .alpha(alpha)
            .border(3.dp, DangerAccent, CircleShape)
    )
}

@Composable
private fun FooterSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(PrimaryAccent, CircleShape)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Device will restart automatically",
                fontSize = 13.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class RippleAnimation(
    val id: Long = System.currentTimeMillis()
)