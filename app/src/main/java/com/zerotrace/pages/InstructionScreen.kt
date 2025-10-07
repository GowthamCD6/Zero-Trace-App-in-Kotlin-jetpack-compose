package com.zerotrace.pages

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerotrace.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
// Data class for tutorial steps
data class TutorialStep(
    val id: Int,
    val title: String,
    val description: String,
    val details: String,
    val icon: ImageVector,
    val color: Color,
    val warning: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionScreen(
    onNavigateToStart: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    // Animation states
    val fadeAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(100f) }
    val stepsOpacity = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.8f) }
    val progressPulse = remember { Animatable(1f) }

    // Step tracking states
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }
    var nextRequiredStep by remember { mutableIntStateOf(1) }
    var showDevOptionsModal by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Tutorial steps configuration
    val tutorialSteps = remember {
        listOf(
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
                details = "Grant ZeroTrace permission to access the connected storage. The app will verify that it can save your device identifiers (model, serial number, IMEI) to the drive for later verification.\n\nClick here to auto collect data.",
                icon = Icons.Filled.VerifiedUser,
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
                icon = Icons.Filled.AppShortcut,
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
                title = "Keep USB Drive Ready",
                description = "You'll physically remove and reconnect the drive",
                details = "After device information is saved, you'll be instructed to physically remove the USB drive before starting the wipe. After your device resets to factory state, you'll reconnect the same drive for verification and certificate generation.",
                icon = Icons.Filled.UsbOff,
                color = Color(0xFF8BC34A),
                warning = "Use the SAME USB drive after reset - don't lose it!"
            )
        )
    }

    // Calculated values
    val allStepsCompleted = completedSteps.size == tutorialSteps.size
    val progressPercentage = completedSteps.size / tutorialSteps.size.toFloat()

    // Developer Options modal
    if (showDevOptionsModal) {
        AlertDialog(
            onDismissRequest = { showDevOptionsModal = false },
            confirmButton = {
                Button(onClick = { showDevOptionsModal = false }) {
                    Text("OK")
                }
            },
            title = { Text("Developer Options Disabled") },
            text = { Text("Your mobile phone's Developer Options are off. Please enable them to continue.") }
        )
    }

    // Animation effects
    LaunchedEffect(Unit) {
        launch { fadeAnim.animateTo(1f, animationSpec = tween(800, easing = EaseOutCubic)) }
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

    // Step completion handler
    fun handleStepCompletion(step: TutorialStep) {
        if (step.id != nextRequiredStep) {
            Toast.makeText(
                context,
                "❌ Please complete Step $nextRequiredStep first before proceeding to Step ${step.id}",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        when (step.id) {
            1 -> {
                val detected = ExternalIn.checkExternalStorage(context, showToast = true)
                if (detected) {
                    completedSteps = completedSteps + step.id
                    nextRequiredStep = 2
                }
            }
            2 -> {
                if (!completedSteps.contains(step.id)) {
                    val activity = context as? Activity
                    activity?.let {
                        DeviceInfoCollector.requestStorageAccess(it, object : DeviceInfoCollector.StorageAccessCallback {
                            override fun onStorageAccessGranted() {
                                completedSteps = completedSteps + step.id
                                nextRequiredStep = 3
                            }
                            override fun onStorageAccessDenied() {}
                        })
                    }
                }
            }
            3 -> {
                if (DeveloperOptionsUtils.isDeveloperOptionsEnabled(context)) {
                    completedSteps = completedSteps + step.id
                    nextRequiredStep = 4
                    Toast.makeText(context, "✅ Developer Options Enabled Successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    showDevOptionsModal = true
                }
            }
            4 -> {
                completedSteps = completedSteps + step.id
                nextRequiredStep = 5
            }
            5 -> {
                if (!completedSteps.contains(step.id)) {
                    coroutineScope.launch {
                        BackgroundAppChecker.checkBackgroundApps(context)
                        completedSteps = completedSteps + step.id
                        nextRequiredStep = 6
                    }
                }
            }
            6 -> {
                coroutineScope.launch {
                    val batteryOk = BatteryUtils.isBatterySufficient(context, 50)
                    if (batteryOk) {
                        completedSteps = completedSteps + step.id
                        nextRequiredStep = 7
                    }
                }
            }
            7 -> {
                coroutineScope.launch {
                    val isExternalRemoved = ExternalOut.isExternalStorageRemoved(context)
                    if (isExternalRemoved) {
                        completedSteps = completedSteps + step.id
                    }
                }
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
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // Sequential flow indicator
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Complete steps in order. Step $nextRequiredStep is now available.",
                            fontSize = 13.sp,
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                tutorialSteps.forEach { step ->
                    EnhancedTutorialStepCard(
                        step = step,
                        completed = completedSteps.contains(step.id),
                        stepNumber = step.id,
                        totalSteps = tutorialSteps.size,
                        clickable = true,
                        isNextStep = step.id == nextRequiredStep,
                        onClick = { handleStepCompletion(step) }
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

        // Unique Floating Back Button
        UniqueFloatingBackButton(
            onNavigateBack = onNavigateBack,
            fadeAnim = fadeAnim.value
        )

        // Floating Progress Modal
        if (progressPercentage > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                FloatingProgressCard(
                    completedSteps = completedSteps.size,
                    totalSteps = tutorialSteps.size,
                    progressPercentage = progressPercentage,
                    isComplete = allStepsCompleted,
                    pulseScale = progressPulse.value
                )
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

@Composable
fun FloatingProgressCard(
    completedSteps: Int,
    totalSteps: Int,
    progressPercentage: Float,
    isComplete: Boolean,
    pulseScale: Float
) {
    Card(
        modifier = Modifier
            .scale(pulseScale)
            .width(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Progress Circle Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(76.dp)
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 6.dp,
                    color = Color(0xFFE8F5E8),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                // Active Progress
                CircularProgressIndicator(
                    progress = progressPercentage,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 6.dp,
                    color = Color(0xFF4CAF50),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                // Center Content - Properly Centered
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = completedSteps.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "of $totalSteps",
                            fontSize = 10.sp,
                            color = Color(0xFF81C784),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Title
            Text(
                text = "Steps Done",
                fontSize = 12.sp,
                color = Color(0xFF388E3C),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Status Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isComplete) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Complete",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Complete",
                            fontSize = 10.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Text(
                        text = "${(progressPercentage * 100).toInt()}%",
                        fontSize = 10.sp,
                        color = Color(0xFF66BB6A),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedTutorialStepCard(
    step: TutorialStep,
    completed: Boolean,
    stepNumber: Int,
    totalSteps: Int,
    clickable: Boolean = true,
    isNextStep: Boolean = false, // New parameter to indicate if this is the next available step
    onClick: () -> Unit
) {
    val cardScale = remember { Animatable(1f) }
    val checkmarkRotation = remember { Animatable(0f) }
    
    val cardColor by animateColorAsState(
        targetValue = when {
            completed -> Color(0xFFF1F8F4) // Green for completed
            isNextStep -> Color(0xFFF3F7FF) // Light blue for next available step
            else -> Color(0xFFF5F5F5) // Gray for locked steps
        },
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            completed -> Color(0xFF4CAF50) // Green for completed
            isNextStep -> Color(0xFF2196F3) // Blue for next available step
            else -> Color(0xFFBDBDBD) // Gray for locked steps
        },
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
            .then(if (clickable) Modifier.clickable { onClick() } else Modifier)
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
                            when {
                                completed -> Color(0xFF4CAF50) // Green for completed
                                isNextStep -> Color(0xFF2196F3) // Blue for next available step
                                else -> Color(0xFFBDBDBD) // Gray for locked steps
                            },
                            CircleShape
                        )
                        .rotate(if (completed) checkmarkRotation.value else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        completed -> {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        isNextStep -> {
                            Text(
                                text = stepNumber.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
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

@Composable
fun UniqueFloatingBackButton(
    onNavigateBack: () -> Unit,
    fadeAnim: Float
) {
    val buttonScale = remember { Animatable(0.9f) }
    val iconRotation = remember { Animatable(0f) }
    val glowPulse = remember { Animatable(1f) }
    val shimmerOffset = remember { Animatable(0f) }
    
    // Clean entrance animation
    LaunchedEffect(Unit) {
        launch {
            delay(400) // Smooth delay after main content starts
            buttonScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f))
        }
        launch {
            delay(600)
            iconRotation.animateTo(360f, animationSpec = tween(1000, easing = EaseOutCubic))
        }
    }
    
    // Gentle continuous glow effect
    LaunchedEffect(Unit) {
        while (true) {
            glowPulse.animateTo(1.15f, animationSpec = tween(1500, easing = EaseInOutSine))
            glowPulse.animateTo(1f, animationSpec = tween(1500, easing = EaseInOutSine))
        }
    }
    
    // Subtle shimmer effect
    LaunchedEffect(Unit) {
        while (true) {
            shimmerOffset.animateTo(1f, animationSpec = tween(3000, easing = LinearEasing))
            shimmerOffset.snapTo(0f)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp)
            .alpha(fadeAnim),
        contentAlignment = Alignment.TopStart
    ) {
        Card(
            onClick = onNavigateBack,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp,
                pressedElevation = 16.dp
            ),
            modifier = Modifier
                .size(52.dp)
                .scale(buttonScale.value)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF388E3C),
                                Color(0xFF2E7D32)
                            ),
                            radius = 60f
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            CircleShape
                        )
                        .scale(glowPulse.value)
                )
                
                // Inner glow ring
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .scale(glowPulse.value * 0.8f)
                )
                
                // Home icon with rotation
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Back to Home",
                    tint = Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .rotate(iconRotation.value * 0.1f) // Subtle rotation
                )
                
                // Shimmer overlay effect
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f * (1f - shimmerOffset.value.coerceIn(0f, 1f))),
                            CircleShape
                        )
                        .offset(
                            x = (shimmerOffset.value * 12).dp,
                            y = (-shimmerOffset.value * 8).dp
                        )
                )
            }
        }
    }
}