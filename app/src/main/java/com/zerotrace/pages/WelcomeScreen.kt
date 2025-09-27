package com.zerotrace.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerotrace.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val fadeAnim = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.5f) }
    val titleOpacity = remember { Animatable(0f) }
    val subtitleOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.8f) }
    val pulseScale = remember { Animatable(1f) }

    // Animations
    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(800))

        launch {
            delay(300)
            logoScale.animateTo(1f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ))
        }

        launch {
            delay(800)
            titleOpacity.animateTo(1f, animationSpec = tween(600))
        }

        launch {
            delay(1200)
            subtitleOpacity.animateTo(1f, animationSpec = tween(500))
        }

        launch {
            delay(1600)
            buttonScale.animateTo(1f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            ))
        }

        // Continuous pulse
        launch {
            delay(2000)
            while (true) {
                pulseScale.animateTo(1.05f, animationSpec = tween(2000))
                pulseScale.animateTo(1f, animationSpec = tween(2000))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E293B),
                        Color(0xFF334155),
                        Color(0xFF475569)
                    )
                )
            )
            .alpha(fadeAnim.value)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .scale(logoScale.value * pulseScale.value)
                    .size(140.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFF8FAFC)
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "ZeroTrace Logo",
                    modifier = Modifier.size(84.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Column(
                modifier = Modifier.alpha(titleOpacity.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to",
                    fontSize = 18.sp,
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ZeroTrace",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraLight,
                    color = Color.White,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(3.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF06B6D4),
                                    Color(0xFF10B981)
                                )
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subtitle
            Column(
                modifier = Modifier.alpha(subtitleOpacity.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ENTERPRISE DATA SECURITY",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Secure • Compliant • Verified",
                    fontSize = 16.sp,
                    color = Color(0xFFE2E8F0),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .scale(buttonScale.value)
                    .fillMaxWidth(0.75f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Get Started",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "→",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "Professional-grade device sanitization\nwith cryptographic verification",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp,
                modifier = Modifier.alpha(subtitleOpacity.value)
            )
        }

        // Version info
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .alpha(subtitleOpacity.value)
        ) {
            Text(
                text = "v1.0 • Enterprise Edition",
                fontSize = 12.sp,
                color = Color(0xFF475569),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
