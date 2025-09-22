package com.zerotrace.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.zerotrace.R

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val fadeAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(50f) }
    val logoScale = remember { Animatable(0.8f) }
    val titleTranslate = remember { Animatable(30f) }
    val contentOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.9f) }

    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(1000))
        logoScale.animateTo(1f, animationSpec = tween(800))
        slideAnim.animateTo(0f, animationSpec = tween(800))
        titleTranslate.animateTo(0f, animationSpec = tween(600))
        contentOpacity.animateTo(1f, animationSpec = tween(700))
        buttonScale.animateTo(1f, animationSpec = tween(500))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
            .alpha(fadeAnim.value)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 40.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .offset(y = slideAnim.value.dp)
                    .size(100.dp)
                    .background(Color.White, shape = CircleShape)
                    .shadow(8.dp, CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "ZeroTrace Logo",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.offset(y = titleTranslate.value.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ZeroTrace",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF0F172A),
                    letterSpacing = 2.sp
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(Color(0xFF3B82F6))
                )
                Text(
                    text = "Enterprise Data Security",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.alpha(contentOpacity.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Professional-grade device sanitization with cryptographic verification. Ensure complete data erasure and generate compliance-ready certificates.",
                    fontSize = 17.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FeatureCard(title = "Secure Erasure", desc = "DoD 5220.22-M standard")
                    FeatureCard(title = "Verification", desc = "Cryptographic proof")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .scale(buttonScale.value)
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onGetStarted,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(vertical = 18.dp, horizontal = 48.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "Initialize Process",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "→", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    text = "This process will permanently erase all data",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun FeatureCard(title: String, desc: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFEEF2FF), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "●", fontSize = 20.sp, color = Color(0xFF3B82F6))
        }
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Text(
            text = desc,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )
    }
}
