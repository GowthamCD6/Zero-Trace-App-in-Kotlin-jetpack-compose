package com.zerotrace.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onNavigateToInstruction: () -> Unit) {
    val fadeAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(30f) }
    val contentOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.95f) }

    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(800))
        slideAnim.animateTo(0f, animationSpec = tween(600))
        contentOpacity.animateTo(1f, animationSpec = tween(700))
        buttonScale.animateTo(1f, animationSpec = tween(400))
    }

    val features = listOf(
        Feature("🔒", "Guaranteed Data Destruction", "Uses NIST-standard cryptographic erasure, the method recommended for modern mobile devices.", Color(0xFFEF4444)),
        Feature("🛡️", "Tamper-Proof Verification", "Generates a digitally-signed certificate proving the wipe was successful and authentic.", Color(0xFF3B82F6)),
        Feature("🔐", "Offline & Secure", "The entire process works without sending your data to the cloud. Your secrets stay with you.", Color(0xFF10B981))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
            .alpha(fadeAnim.value)
    ) {
        // Background pattern
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 60.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                modifier = Modifier
    
                    .offset(y = slideAnim.value.dp),
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
                    text = "DATA WIPING SOLUTION",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x1A3B82F6), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ZeroTrace",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        Text(
                            text = "ZeroTrace ensures your personal data is permanently and verifiably destroyed before you recycle or resell your Android device. We leverage the device's built-in cryptographic erasure to render data irrecoverable, generating a digitally-signed certificate of sanitization for your records and for compliance purposes.",
                            fontSize = 16.sp,
                            color = Color(0xFF374151),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            // Features Section
            Column(
                modifier = Modifier.alpha(contentOpacity.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Key Features",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                features.forEach { feature ->
                    FeatureCard(feature)
                }
                // Security Badge
                Row(
                    modifier = Modifier
                        .background(Color(0x0D10B981), shape = RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x3310B981), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0x1A10B981), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = "NIST Certified",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Meets highest security standards for data sanitization",
                            fontSize = 14.sp,
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Action Section
            Column(
                modifier = Modifier.scale(buttonScale.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNavigateToInstruction,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(vertical = 18.dp, horizontal = 48.dp)
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "Get Started",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "→", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    text = "📋 Review preparation guide before proceeding",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class Feature(val icon: String, val title: String, val description: String, val color: Color)

@Composable
fun FeatureCard(feature: Feature) {
    Row(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(24.dp)
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(feature.color.copy(alpha = 0.08f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = feature.icon, fontSize = 24.sp)
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = feature.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = feature.description,
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
