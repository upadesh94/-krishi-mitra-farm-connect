package com.example.krishimitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.data.model.UserRole
import com.example.krishimitra.data.repository.FirebaseManager
import com.example.krishimitra.data.repository.KrishiRepository
import com.example.krishimitra.ui.theme.*

@Composable
fun ProfileScreen(
    onOpenAuth: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser by KrishiRepository.currentUser.collectAsState()

    var selectedLang by remember { mutableStateOf(currentUser.preferredLanguage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 70.dp)
    ) {
        // User Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = PrimaryGreenDark,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = currentUser.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = currentUser.phone,
                        fontSize = 12.sp,
                        color = TextMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GreenSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = if (currentUser.role == UserRole.FARMER) "Farmer Account" else "Nursery Owner",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreenDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Farm Profile Details
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Farm Details & Land Profile",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileInfoRow(label = "Location", value = "${currentUser.taluka}, ${currentUser.district}")
                ProfileInfoRow(label = "Farm Size", value = "${currentUser.farmSizeAcres} Acres")
                ProfileInfoRow(label = "Soil Type", value = currentUser.soilType)
                ProfileInfoRow(label = "Water Availability", value = currentUser.waterSource)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Preferences
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Preferred Language (भाषा)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("English", "मराठी", "हिंदी").forEach { lang ->
                        FilterChip(
                            selected = selectedLang == lang,
                            onClick = {
                                selectedLang = lang
                                KrishiRepository.updateLanguage(lang)
                                Toast.makeText(context, "Language updated to $lang", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text(lang) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign out")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // About Krishi Mitra AI Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.krishi_mitra_app_icon),
                    contentDescription = "Krishi Mitra AI Logo",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Krishi Mitra AI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreenDark
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GreenSurface
                        ) {
                            Text(
                                text = "v1.0.0",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Farm Together • Grow Better",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMedium
                    )
                    Text(
                        text = "Empowering farmers with pro-tray nursery pre-orders & AI diagnostics.",
                        fontSize = 10.sp,
                        color = TextLight,
                        lineHeight = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Firebase Sync Status Info Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (FirebaseManager.isConfigured()) Icons.Default.CheckCircle else Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = if (FirebaseManager.isConfigured()) PrimaryGreen else HarvestGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Firebase Database & Auth Status",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (FirebaseManager.isConfigured()) {
                        "🟢 Connected to Firebase Cloud Firestore and Authentication."
                    } else {
                        "🟡 Running with high-speed local repository & state synchronization. Place your 'google-services.json' in 'app/' to connect your live Firebase project."
                    },
                    fontSize = 11.sp,
                    color = TextMedium,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMedium)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
    }
}
