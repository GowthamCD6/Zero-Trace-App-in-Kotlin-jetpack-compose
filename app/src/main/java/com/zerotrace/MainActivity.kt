package com.zerotrace

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zerotrace.data.DeviceDataManager
import com.zerotrace.data.models.WipeData
import com.zerotrace.security.CryptoManager
import com.zerotrace.storage.ExternalStorageManager
import com.zerotrace.utils.DeviceInfoCollector

class MainActivity : ComponentActivity() {
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, DeviceAdminReceiver::class.java)

        setContent {
            var showWelcome by remember { mutableStateOf(true) }
            var showLogin by remember { mutableStateOf(false) }
            var showHome by remember { mutableStateOf(false) }
            var showInstruction by remember { mutableStateOf(false) }
            var userName by remember { mutableStateOf("") }
            if (showWelcome) {
                com.zerotrace.pages.WelcomeScreen(
                    onGetStarted = {
                        showWelcome = false
                        showLogin = true
                    }
                )
            } else if (showLogin) {
                com.zerotrace.pages.LoginScreen(
                    onNavigateToHome = { name ->
                        userName = name
                        showLogin = false
                        showHome = true
                    }
                )
            } else if (showHome) {
                com.zerotrace.pages.HomeScreen(
                    onNavigateToInstruction = {
                        showHome = false
                        showInstruction = true
                    },
                    userName = userName
                )
            } else if (showInstruction) {
                com.zerotrace.pages.InstructionScreen(
                    onNavigateToStart = { showInstruction = false }
                )
            } else {
                ZeroTraceApp(
                    onPreWipe = { wipeId ->
                        val deviceInfo = DeviceDataManager.collectDeviceInfo(this)
                        val wipeData = WipeData(wipeId, deviceInfo)
                        val signedJson = CryptoManager.signJson(wipeData, this)
                        val saved = ExternalStorageManager.saveJsonToUsb(this, signedJson, wipeId)
                        if (saved) {
                            Toast.makeText(this, "Pre-wipe data saved to USB.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to save to USB.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onWipe = {
                        if (devicePolicyManager.isAdminActive(adminComponent)) {
                            devicePolicyManager.wipeData(0)
                        } else {
                            Toast.makeText(this, "Device Admin not active.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "ZeroTrace requires device admin to securely wipe data.")
                            startActivity(intent)
                        }
                    },
                    onPostWipe = { wipeId ->
                        val verified = ExternalStorageManager.verifyJsonSignature(this, wipeId)
                        if (verified) {
                            val pdfCert = CryptoManager.generatePdfCertificate(this, wipeId)
                            val saved = ExternalStorageManager.savePdfToUsb(this, pdfCert, wipeId)
                            if (saved) {
                                Toast.makeText(this, "Certificate saved to USB.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "Failed to save certificate.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Verification failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // Handle storage access result for device info collection
        DeviceInfoCollector.handleStorageAccessResult(this, requestCode, resultCode, data)
    }
}

@Composable
fun ZeroTraceApp(
    onPreWipe: (String) -> Unit,
    onWipe: () -> Unit,
    onPostWipe: (String) -> Unit
) {
    var wipeId by remember { mutableStateOf("") }
    var phase by remember { mutableStateOf(1) }
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ZeroTrace Secure Wipe", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = wipeId,
                onValueChange = { wipeId = it },
                label = { Text("Wipe ID") },
                enabled = phase == 1 || phase == 2
            )
            Spacer(Modifier.height(16.dp))
            if (phase == 1) {
                Button(onClick = {
                    if (wipeId.isNotBlank()) {
                        onPreWipe(wipeId)
                        phase = 2
                    }
                }) {
                    Text("Pre-Wipe: Save Data to USB")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onWipe() }) {
                    Text("Wipe Device Now")
                }
            } else if (phase == 2) {
                Button(onClick = {
                    if (wipeId.isNotBlank()) {
                        onPostWipe(wipeId)
                        phase = 3
                    }
                }) {
                    Text("Post-Wipe: Generate Certificate")
                }
            } else {
                Text("Wipe and certificate complete.", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
