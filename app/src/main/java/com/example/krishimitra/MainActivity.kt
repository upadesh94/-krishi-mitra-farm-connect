package com.example.krishimitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.krishimitra.data.repository.FirebaseManager
import com.example.krishimitra.data.repository.KrishiRepository
import com.example.krishimitra.ui.components.KrishiBottomBar
import com.example.krishimitra.ui.components.NavigationTab
import com.example.krishimitra.ui.screens.*
import com.example.krishimitra.ui.theme.KrishiMitraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Safely initialize Firebase Auth & Firestore
        FirebaseManager.initialize(applicationContext)

        setContent {
            KrishiMitraTheme {
                var isAuthenticated by remember { mutableStateOf(false) }
                var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

                LaunchedEffect(Unit) {
                    FirebaseManager.currentUserProfile()
                        .onSuccess { profile ->
                            if (profile != null) {
                                KrishiRepository.setAuthenticatedUser(profile)
                                KrishiRepository.connectRealtimeData(profile)
                                isAuthenticated = true
                            }
                        }
                }

                if (!isAuthenticated) {
                    LoginScreen(onLogin = {
                        KrishiRepository.connectRealtimeData(KrishiRepository.currentUser.value)
                        isAuthenticated = true
                    })
                } else if (KrishiRepository.currentUser.collectAsState().value.role == com.example.krishimitra.data.model.UserRole.NURSERY_OWNER) {
                    NurseryOwnerDashboard(
                        onSignOut = {
                            FirebaseManager.signOut()
                            KrishiRepository.clearSession()
                            isAuthenticated = false
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            KrishiBottomBar(
                                currentTab = currentTab,
                                onTabSelected = { tab -> currentTab = tab }
                            )
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (currentTab) {
                                NavigationTab.HOME -> HomeScreen(
                                    onNavigateToMarketplace = { currentTab = NavigationTab.MARKETPLACE },
                                    onNavigateToScan = { currentTab = NavigationTab.SCAN_PLANT },
                                    onNavigateToCommunity = { currentTab = NavigationTab.COMMUNITY },
                                    onNavigateToProfile = { currentTab = NavigationTab.PROFILE }
                                )
                                NavigationTab.MARKETPLACE -> MarketplaceScreen(onBack = { currentTab = NavigationTab.HOME })
                                NavigationTab.SCAN_PLANT -> DiseaseScanScreen()
                                NavigationTab.COMMUNITY -> CommunityScreen()
                                NavigationTab.PROFILE -> ProfileScreen(
                                    onSignOut = {
                                        FirebaseManager.signOut()
                                        KrishiRepository.clearSession()
                                        isAuthenticated = false
                                        currentTab = NavigationTab.HOME
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}