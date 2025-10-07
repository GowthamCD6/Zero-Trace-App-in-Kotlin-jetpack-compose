package com.zerotrace

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.zerotrace.utils.DeviceInfoCollector

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var showWelcome by remember { mutableStateOf(true) }
            var showLogin by remember { mutableStateOf(false) }
            var showHome by remember { mutableStateOf(false) }
            var showInstruction by remember { mutableStateOf(false) }
            var showWipe by remember { mutableStateOf(false) }
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
                    onNavigateToStart = { 
                        showInstruction = false
                        showWipe = true
                    },
                    onNavigateBack = {
                        showInstruction = false
                        showHome = true
                    }
                )
            } else if (showWipe) {
                com.zerotrace.pages.WipeScreen(
                    onNavigateBack = {
                        showWipe = false
                        showInstruction = true
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
