package com.zerotrace.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
fun InstructionScreen(onNavigateToStart: () -> Unit) {
    val fadeAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(30f) }
    val stepsOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.95f) }

    var completedSteps by remember { mutableStateOf(setOf<Int>()) }

    val tutorialSteps = listOf(
        TutorialStep(1, "Prepare Your Storage Device", "Insert a clean USB drive or SD card into your device", "Use a USB drive with at least 8GB capacity. Ensure it's empty and properly formatted (FAT32 recommended).", "💾", "Do not use corrupted or damaged storage devices"),
        TutorialStep(2, "Verify Storage Integrity", "Check that your USB/SD card is functioning properly", "Run a quick scan to ensure the storage device has no errors or corruption. This prevents data loss during the process.", "🔍", "Corrupted storage may cause process failure"),
        TutorialStep(3, "Backup Important Data", "Save any critical files you want to keep", "Create backups of photos, documents, contacts, and any other important data. Once the wipe begins, recovery is impossible.", "💼", "This step is irreversible - backup everything important"),
        TutorialStep(4, "Close All Applications", "Exit all running apps and save any open work", "Ensure no critical applications are running. Log out of all accounts and close any unsaved documents.", "📱", "Unsaved work will be permanently lost"),
        TutorialStep(5, "Enable Developer Options", "Activate USB debugging and OEM unlocking if required", "Some devices need developer options enabled for complete data wiping. Check your device settings.", "⚙️", "Skip if not required for your device"),
        TutorialStep(6, "Connect to Power Source", "Ensure device has sufficient battery or is plugged in", "The wiping process can take time. Maintain at least 50% battery or connect to a charger to prevent interruption.", "🔋", "Low battery may interrupt the process"),
        TutorialStep(7, "Review Security Settings", "Verify encryption and security configurations", "Check if device encryption is enabled. This enhances the security of the wiping process.", "🔐", "Ensure you know your security credentials"),
        TutorialStep(8, "Prepare for Device Restart", "Understand that your device will restart multiple times", "The secure wipe process involves several restarts. Do not interrupt or power off during this process.", "🔄", "Do not power off during restart cycles")
    )

    val allStepsCompleted = completedSteps.size == tutorialSteps.size

    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(800))
        slideAnim.animateTo(0f, animationSpec = tween(600))
        stepsOpacity.animateTo(1f, animationSpec = tween(700))
        buttonScale.animateTo(1f, animationSpec = tween(400))
    }

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
                    .align(Alignment.CenterHorizontally)
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
                    text = "PREPARATION GUIDE",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How to Use ZeroTrace Safely",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Follow this step-by-step guide before proceeding",
                            fontSize = 16.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(3.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(completedSteps.size / tutorialSteps.size.toFloat())
                                    .background(Color(0xFF10B981), shape = RoundedCornerShape(3.dp))
                            )
                        }
                        Text(
                            text = "${completedSteps.size} of ${tutorialSteps.size} steps completed",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Tutorial Steps Section
            Column(
                modifier = Modifier.alpha(stepsOpacity.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Preparation Tutorial",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Complete all steps below to ensure safe data wiping",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                tutorialSteps.forEach { step ->
                    TutorialStepCard(
                        step = step,
                        completed = completedSteps.contains(step.id),
                        onClick = {
                            completedSteps = if (completedSteps.contains(step.id)) {
                                completedSteps - step.id
                            } else {
                                completedSteps + step.id
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Action Section
            Column(
                modifier = Modifier.scale(buttonScale.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!allStepsCompleted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF9CA3AF), shape = RoundedCornerShape(16.dp))
                                .padding(vertical = 16.dp, horizontal = 48.dp)
                                .alpha(0.7f),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Complete All Steps",
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
                                    Text(text = "🔒", fontSize = 12.sp)
                                }
                            }
                        }
                        Text(
                            text = "Please complete all preparation steps above to proceed",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = onNavigateToStart,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .padding(vertical = 16.dp, horizontal = 48.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = "Proceed to Data Wipe",
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
                            text = "✅ All preparation steps completed - Ready to proceed",
                            fontSize = 13.sp,
                            color = Color(0xFF10B981),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "⚠️ This process will permanently erase all device data",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

data class TutorialStep(
    val id: Int,
    val title: String,
    val description: String,
    val details: String,
    val icon: String,
    val warning: String?
)

@Composable
fun TutorialStepCard(step: TutorialStep, completed: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(if (completed) Color(0xFFF0FDF4) else Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .border(2.dp, if (completed) Color(0xFF10B981) else Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF3F4F6), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = step.icon, fontSize = 20.sp)
            }
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp)) {
                Text(
                    text = step.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = step.description,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Normal
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (completed) Color(0xFF10B981) else Color(0xFFE5E7EB), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (completed) "✓" else step.id.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (completed) Color.White else Color(0xFF6B7280)
                )
            }
        }
        Text(
            text = step.details,
            fontSize = 13.sp,
            color = Color(0xFF374151),
            lineHeight = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        if (step.warning != null) {
            Row(
                modifier = Modifier
                    .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .border(4.dp, Color(0xFFF59E0B), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "⚠️", fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                Text(
                    text = step.warning,
                    fontSize = 12.sp,
                    color = Color(0xFF92400E),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
