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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerotrace.utils.BatteryUtils
import com.zerotrace.utils.BackgroundAppChecker
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InstructionScreen(onNavigateToStart: () -> Unit) {
    val fadeAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(100f) }
    val stepsOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.8f) }
    val progressPulse = remember { Animatable(1f) }

    var completedSteps by remember { mutableStateOf(setOf<Int>()) }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tutorialSteps = listOf(
        TutorialStep(
            id = 1,
            title = "Connect USB OTG Drive",
            description = "Insert a USB OTG drive or microSD card",
            details = "Connect a clean USB OTG drive or microSD card (minimum 1GB recommended). This drive will securely store your device information before the wipe. Make sure it's properly formatted and accessible.",
            icon = Icons.Filled.Storage,
            color = Color(0xFF2196F3),
            warning = "Keep this drive safe - you'll need it after the reset"
        ),
        TutorialStep(
            id = 2,
            title = "Verify Storage Access",
            description = "Ensure the app can read and write to the drive",
            details = "Grant ZeroTrace permission to access the connected storage. The app will verify that it can save your device identifiers (model, serial number, IMEI) to the drive for later verification.",
            icon = Icons.Filled.Storage,
            color = Color(0xFF9C27B0),
            warning = "Storage access is required for the certificate process"
        ),
        TutorialStep(
            id = 3,
            title = "Enable Developer Options",
            description = "Activate USB debugging for full device access",
            details = "Go to Settings → About Phone → Tap 'Build Number' 7 times to enable Developer Options. Then enable 'USB Debugging' and 'OEM Unlocking' if available. This ensures complete data sanitization.",
            icon = Icons.Filled.Build,
            color = Color(0xFFFF9800),
            warning = "Required for NIST-standard cryptographic erasure"
        ),
        TutorialStep(
            id = 4,
            title = "Backup Important Data",
            description = "Save photos, contacts, and files elsewhere",
            details = "This process is completely irreversible. Export all photos, contacts, documents, and app data to cloud storage or another device. Once the wipe begins, all data will be permanently destroyed using cryptographic erasure.",
            icon = Icons.Filled.CloudUpload,
            color = Color(0xFF00BCD4),
            warning = "All data will be permanently and irrecoverably erased"
        ),
        TutorialStep(
            id = 5,
            title = "Close All Applications",
            description = "Log out and save any unsaved work",
            details = "Sign out of all accounts (Google, social media, banking apps). Close all open apps and save any work in progress. Ensure no critical processes are running that could interfere with the reset.",
            icon = Icons.Filled.Delete,
            color = Color(0xFF4CAF50),
            warning = "Unsaved data will be lost permanently"
        ),
        TutorialStep(
            id = 6,
            title = "Charge Your Device",
            description = "Connect to power and ensure >50% battery",
            details = "Plug in your charger and verify battery level is above 50%. The factory reset and verification process takes 20-40 minutes. Power interruption during this process could cause issues.",
            icon = Icons.Filled.BatteryFull,
            color = Color(0xFFF44336),
            warning = "Do not disconnect power during the process"
        ),
        TutorialStep(
            id = 7,
            title = "Disable Security Locks",
            description = "Remove screen locks, PIN, password, or pattern",
            details = "Temporarily disable screen lock, fingerprint, face unlock, and any device encryption passwords. You'll need to remove Google account lock as well. This prevents verification issues after the reset.",
            icon = Icons.Filled.VerifiedUser,
            color = Color(0xFFE91E63),
            warning = "Factory Reset Protection must be disabled"
        ),
        TutorialStep(
            id = 8,
            title = "Keep USB Drive Ready",
            description = "You'll physically remove and reconnect the drive",
            details = "After device information is saved, you'll be instructed to physically remove the USB drive before starting the wipe. After your device resets to factory state, you'll reconnect the same drive for verification and certificate generation.",
            icon = Icons.Filled.Storage,
            color = Color(0xFF8BC34A),
            warning = "Use the SAME USB drive after reset - don't lose it!"
        )
    )

    val allStepsCompleted = completedSteps.size == tutorialSteps.size
    val progressPercentage = completedSteps.size / tutorialSteps.size.toFloat()

    LaunchedEffect(Unit) {
        launch {
            fadeAnim.animateTo(1f, animationSpec = tween(800, easing = EaseOutCubic))
        }
        launch {
            delay(100)
            slideAnim.animateTo(0f, animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f))
        }
        launch {
            delay(300)
            stepsOpacity.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        }
        launch {
            delay(500)
            buttonScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.7f, stiffness = 180f))
        }
    }

    LaunchedEffect(progressPercentage) {
        if (progressPercentage > 0f) {
            launch {
                progressPulse.animateTo(1.1f, animationSpec = tween(300))
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
                        Color(0xFFF5F7FA),
                        Color(0xFFE8EDF3),
                        Color(0xFFDBE4ED)
                    )
                )
            )
            .alpha(fadeAnim.value)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                modifier = Modifier
                    .offset(y = slideAnim.value.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            Color.White,
                            shape = CircleShape
                        )
                        .border(3.dp, Color(0xFF2196F3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ZeroTrace",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Certified Data Sanitization",
                    fontSize = 16.sp,
                    color = Color(0xFF546E7A),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Tutorial Steps
            Column(
                modifier = Modifier.alpha(stepsOpacity.value),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Preparation Steps",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                tutorialSteps.forEachIndexed { index, step ->
                    EnhancedTutorialStepCard(
                        step = step,
                        completed = completedSteps.contains(step.id),
                        stepNumber = index + 1,
                        totalSteps = tutorialSteps.size,
                        onClick = {
                            currentStepIndex = index
                            if (step.id == 5) {
                                coroutineScope.launch {
                                    BackgroundAppChecker.checkBackgroundApps(context)
                                    // Always allow user to manually mark as complete after dialog
                                    completedSteps = if (completedSteps.contains(step.id)) {
                                        completedSteps - step.id
                                    } else {
                                        completedSteps + step.id
                                    }
                                }
                            } else if (step.id == 6) {
                                coroutineScope.launch {
                                    val batteryOk = BatteryUtils.isBatterySufficient(context, 50)
                                    if (batteryOk) {
                                        completedSteps = completedSteps + step.id
                                    } else {
                                        completedSteps = completedSteps - step.id
                                    }
                                }
                            } else {
                                completedSteps = if (completedSteps.contains(step.id)) {
                                    completedSteps - step.id
                                } else {
                                    completedSteps + step.id
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Section
            Column(
                modifier = Modifier.scale(buttonScale.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!allStepsCompleted) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Complete All Steps First",
                                color = Color(0xFF424242),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Review and complete each preparation step before starting the data wipe process",
                                fontSize = 14.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Success Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE8F5E9)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(40.dp)
                                )
                                Column(modifier = Modifier.padding(start = 16.dp)) {
                                    Text(
                                        text = "Ready to Begin!",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = "All preparation steps completed",
                                        fontSize = 14.sp,
                                        color = Color(0xFF558B2F)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Process Info Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "What Happens Next:",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                ProcessStepItem(
                                    number = "1",
                                    text = "Device info saved to USB drive"
                                )
                                ProcessStepItem(
                                    number = "2",
                                    text = "You'll remove the USB drive"
                                )
                                ProcessStepItem(
                                    number = "3",
                                    text = "Factory reset begins (irreversible)"
                                )
                                ProcessStepItem(
                                    number = "4",
                                    text = "Reinstall ZeroTrace after reset"
                                )
                                ProcessStepItem(
                                    number = "5",
                                    text = "Reconnect USB for verification"
                                )
                                ProcessStepItem(
                                    number = "6",
                                    text = "Receive tamper-proof certificate"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Professional Proceed Button
                        Button(
                            onClick = onNavigateToStart,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            ),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Start Data Wipe Process",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Floating Progress Indicator
        if (progressPercentage > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .scale(progressPulse.value)
                        .width(140.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = progressPercentage,
                                modifier = Modifier.size(70.dp),
                                strokeWidth = 6.dp,
                                color = Color(0xFF4CAF50),
                                trackColor = Color(0xFFE0E0E0)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${completedSteps.size}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    text = "of ${tutorialSteps.size}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78909C)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Steps Done",
                            fontSize = 12.sp,
                            color = Color(0xFF546E7A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessStepItem(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFFE3F2FD), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF424242),
            modifier = Modifier.padding(start = 12.dp)
        )
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
        targetValue = if (completed) Color(0xFFF1F8F4) else Color.White,
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = if (completed) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
        animationSpec = tween(300)
    )

    LaunchedEffect(completed) {
        if (completed) {
            cardScale.animateTo(1.02f, tween(150))
            cardScale.animateTo(1f, tween(150))
            checkmarkRotation.animateTo(360f, tween(400))
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (completed) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale.value)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                step.color.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                            .border(2.dp, step.color, CircleShape),
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = step.description,
                            fontSize = 13.sp,
                            color = Color(0xFF546E7A),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (completed) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
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
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = step.details,
                fontSize = 13.sp,
                color = Color(0xFF546E7A),
                lineHeight = 18.sp
            )

            if (step.warning != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = step.warning,
                            fontSize = 12.sp,
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}