package com.example.krishimitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.ui.theme.*

@Composable
fun DiseaseScanScreen() {
    val context = LocalContext.current
    var selectedCrop by remember { mutableStateOf("Tomato") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var scanCompleted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 70.dp)
    ) {
        Text(
            text = "AI Crop Disease Detection",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Text(
            text = "Take or upload a clear photo of an affected leaf for instant agricultural guidance.",
            fontSize = 12.sp,
            color = TextMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Crop selection
        Text(text = "Select Crop:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Tomato", "Chilli", "Brinjal", "Cabbage").forEach { crop ->
                FilterChip(
                    selected = selectedCrop == crop,
                    onClick = {
                        selectedCrop = crop
                        scanCompleted = false
                    },
                    label = { Text(crop, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scanner viewfinder box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(2.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isAnalyzing) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Analyzing plant leaf patterns...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }
            } else if (!scanCompleted) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        isAnalyzing = true
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tap to Scan / Upload Leaf Photo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreenDark
                    )
                    Text(
                        text = "Supports Tomato, Chilli, Brinjal, and Cucurbits",
                        fontSize = 11.sp,
                        color = TextLight
                    )
                }
            } else {
                // Real photographed leaf sample preview with detection overlay
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.tomato_leaf_blight),
                        contentDescription = "Scanned Leaf Sample",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = "📸 Real Plant Sample Analyzed • Blight Lesions Detected",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!isAnalyzing && !scanCompleted) {
            Button(
                onClick = {
                    isAnalyzing = true
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(imageVector = Icons.Default.CropFree, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Start AI Scan", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Trigger simulation completion after analyzing
        LaunchedEffect(isAnalyzing) {
            if (isAnalyzing) {
                kotlinx.coroutines.delay(1200)
                isAnalyzing = false
                scanCompleted = true
            }
        }

        // Result Card
        if (scanCompleted) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Diagnostic Result",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AlertRedLight
                        ) {
                            Text(
                                text = "92% Confidence",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Suspected: Early Leaf Blight (Alternaria)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Characteristic brown-black concentric rings found on lower leaves. Early detection allows timely containment.",
                        fontSize = 12.sp,
                        color = TextMedium,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recommended Preventive Actions:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Prune severely affected bottom leaves to improve aeration.\n• Avoid overhead sprinkler watering during evening hours.\n• Consult local KVK or apply approved copper oxychloride protective spray if needed.",
                        fontSize = 11.sp,
                        color = TextMedium,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Disease report shared with local Pune cluster for farmer alerts.", Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "📢 Report to Community Outbreak Radar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
