package com.zerotrace.pages

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InstructionScreen(onNavigateToStart: () -> Unit) {
    val fadeAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(100f) }
    val stepsOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.8f) }
    val headerRotation = remember { Animatable(0f) }
    val progressPulse = remember { Animatable(1f) }

    var completedSteps by remember { mutableStateOf(setOf<Int>()) }
    var currentStepIndex by remember { mutableStateOf(0) }

    val tutorialSteps = listOf(
        TutorialStep(1, "Prepare Storage Device", "Insert a clean USB drive or SD card (min 8GB)", "Use a high-quality storage device formatted as FAT32. Ensure it's completely empty and has no corrupted sectors for optimal performance.", Icons.Filled.Storage, Color(0xFF6366F1), "Use only certified, non-corrupted storage devices"),
        TutorialStep(2, "Verify Device Integrity", "Run comprehensive storage health check", "Perform a complete scan to detect bad sectors, corruption, or hardware issues. A healthy storage device is crucial for successful data wiping.", Icons.Filled.VerifiedUser, Color(0xFF8B5CF6), "Damaged storage may cause complete process failure"),
        TutorialStep(3, "Secure Data Backup", "Create encrypted backups of important files", "Export photos, contacts, documents, and app data to secure cloud storage or external drives. Remember: this process is completely irreversible.", Icons.Filled.Backup, Color(0xFF06B6D4), "All data will be permanently destroyed - backup everything"),
        TutorialStep(4, "Close All Applications", "Terminate all running processes and save work", "Log out from all accounts, close documents, stop background apps. Clear memory and ensure no critical processes are running.", Icons.Filled.Apps, Color(0xFF10B981), "Unsaved work will be permanently lost"),
        TutorialStep(5, "Enable Developer Mode", "Activate advanced debugging options if needed", "Turn on USB debugging and OEM unlocking in developer settings. Required for deep system-level wiping on some devices.", Icons.Filled.DeveloperMode, Color(0xFFF59E0B), "Only enable if your device requires it"),
        TutorialStep(6, "Power Management", "Ensure stable power supply during process", "Connect to charger and maintain >70% battery. The wiping process can take 30-90 minutes depending on storage size.", Icons.Filled.Battery6Bar, Color(0xFFEF4444), "Power interruption will corrupt the process"),
        TutorialStep(7, "Security Configuration", "Review encryption and unlock methods", "Verify device encryption status, note down security credentials, and ensure you have admin access to all protected areas.", Icons.Filled.Security, Color(0xFFEC4899), "Remember all passwords and PINs"),
        TutorialStep(8, "System Restart Preparation", "Prepare for multiple automatic restarts", "The secure wipe involves 3-5 automatic restarts. Do not intervene, power off, or disconnect during these cycles.", Icons.Filled.RestartAlt, Color(0xFF84CC16), "Never interrupt restart sequences")
    )

    val allStepsCompleted = completedSteps.size == tutorialSteps.size
    val progressPercentage = completedSteps.size / tutorialSteps.size.toFloat()

    LaunchedEffect(Unit) {
        // All launches must be inside coroutine scope
        launch {
            fadeAnim.animateTo(1f, animationSpec = tween(1200, easing = EaseOutCubic))
        }
        launch {
            delay(200)
            slideAnim.animateTo(0f, animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f))
        }
        launch {
            delay(400)
            stepsOpacity.animateTo(1f, animationSpec = tween(800, easing = EaseOutCubic))
        }
        launch {
            delay(600)
            buttonScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f))
        }
        launch {
            headerRotation.animateTo(360f, animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ))
        }
    }

    LaunchedEffect(progressPercentage) {
        if (progressPercentage > 0f) {
            launch {
                progressPulse.animateTo(1.2f, animationSpec = tween(300))
                progressPulse.animateTo(1f, animationSpec = tween(300))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F23),
                        Color(0xFF1A1A3E),
                        Color(0xFF2D1B69)
                    )
                )
            )
            .alpha(fadeAnim.value)
    ) {
        // Animated background elements
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size((80 + index * 40).dp)
                    .offset(
                        x = (50 + index * 80).dp,
                        y = (100 + index * 150).dp
                    )
                    .rotate(headerRotation.value + index * 45f)
                    .alpha(0.1f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 50.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Futuristic Header
            Column(
                modifier = Modifier
                    .offset(y = slideAnim.value.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo with glow effect
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(2.dp, Color(0xFF6366F1).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Color(0xFF6366F1),
                        modifier = Modifier
                            .size(60.dp)
                            .rotate(headerRotation.value * 0.1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ZeroTrace",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraLight,
                    color = Color.White,
                    letterSpacing = 4.sp
                )

                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(3.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(1.5.dp)
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SECURE PREPARATION PROTOCOL",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Progress Card with glassmorphism effect
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E293B).copy(alpha = 0.7f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF475569).copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Checklist,
                                contentDescription = null,
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "Preparation Checklist",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Complete all security protocols",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Animated Progress Ring
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.scale(progressPulse.value)
                        ) {
                            CircularProgressIndicator(
                                progress = progressPercentage,
                                modifier = Modifier.size(80.dp),
                                strokeWidth = 6.dp,
                                color = Color(0xFF10B981),
                                trackColor = Color(0xFF374151)
                            )
                            Text(
                                text = "${(progressPercentage * 100).toInt()}%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${completedSteps.size} of ${tutorialSteps.size} protocols completed",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Tutorial Steps with enhanced design
            Column(
                modifier = Modifier.alpha(stepsOpacity.value),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Security Protocols",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Follow each step carefully to ensure complete data sanitization",
                    fontSize = 16.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                tutorialSteps.forEachIndexed { index, step ->
                    EnhancedTutorialStepCard(
                        step = step,
                        completed = completedSteps.contains(step.id),
                        stepNumber = index + 1,
                        totalSteps = tutorialSteps.size,
                        onClick = {
                            completedSteps = if (completedSteps.contains(step.id)) {
                                completedSteps - step.id
                            } else {
                                completedSteps + step.id
                            }
                            currentStepIndex = index
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Enhanced Action Section
            Column(
                modifier = Modifier.scale(buttonScale.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!allStepsCompleted) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF374151).copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF6B7280).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Complete All Protocols",
                                color = Color(0xFF9CA3AF),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "All security steps must be verified before proceeding",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Success indicator
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF059669).copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(modifier = Modifier.padding(start = 16.dp)) {
                                    Text(
                                        text = "All Protocols Completed",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = "System ready for secure data erasure",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6EE7B7)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Danger button with enhanced styling
                        Button(
                            onClick = onNavigateToStart,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .border(2.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteForever,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Begin Secure Data Wipe",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Warning text with enhanced styling
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFDC2626).copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.border(1.dp, Color(0xFFDC2626).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "This process permanently destroys all device data",
                                    fontSize = 13.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
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
    val icon: ImageVector,
    val color: Color,
    val warning: String?
)

@Composable
fun EnhancedTutorialStepCard(
    step: TutorialStep,
    completed: Boolean,
    stepNumber: Int,
    totalSteps: Int,
    onClick: () -> Unit
) {
    val cardScale = remember { Animatable(1f) }
    val checkmarkRotation = remember { Animatable(0f) }
    
    val cardColor by animateColorAsState(
        targetValue = if (completed) {
            step.color.copy(alpha = 0.1f)
        } else {
            Color(0xFF1E293B).copy(alpha = 0.7f)
        },
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = if (completed) step.color else Color(0xFF475569).copy(alpha = 0.5f),
        animationSpec = tween(300)
    )

    LaunchedEffect(completed) {
        if (completed) {
            cardScale.animateTo(1.02f, tween(150))
            cardScale.animateTo(1f, tween(150))
            checkmarkRotation.animateTo(360f, tween(500))
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (completed) 16.dp else 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale.value)
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Step icon and info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        step.color.copy(alpha = 0.3f),
                                        step.color.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, step.color.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = step.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = step.description,
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Completion indicator
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (completed) step.color else Color(0xFF475569),
                            CircleShape
                        )
                        .rotate(if (completed) checkmarkRotation.value else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    if (completed) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = stepNumber.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = step.details,
                fontSize = 14.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 20.sp
            )

            if (step.warning != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFBBF24).copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.border(
                        1.dp,
                        Color(0xFFFBBF24).copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = step.warning,
                            fontSize = 12.sp,
                            color = Color(0xFFFEF3C7),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}