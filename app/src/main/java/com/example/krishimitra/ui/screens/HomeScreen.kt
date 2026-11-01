package com.example.krishimitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.krishimitra.data.model.Nursery
import com.example.krishimitra.data.model.SeedlingListing
import com.example.krishimitra.data.repository.KrishiRepository
import com.example.krishimitra.ui.components.*
import com.example.krishimitra.ui.theme.BackgroundLight

@Composable
fun HomeScreen(
    onNavigateToMarketplace: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by KrishiRepository.currentUser.collectAsState()
    val nurseries by KrishiRepository.nurseries.collectAsState()
    val seedlings by KrishiRepository.seedlings.collectAsState()
    val weather by KrishiRepository.weather.collectAsState()
    val diseaseAlert by KrishiRepository.diseaseAlert.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSeedlingForOrder by remember { mutableStateOf<SeedlingListing?>(null) }
    var selectedNurseryForDetail by remember { mutableStateOf<Nursery?>(null) }

    // Nursery Detail Dialog when a nursery card is tapped
    if (selectedNurseryForDetail != null) {
        NurseryDetailDialog(
            nursery = selectedNurseryForDetail!!,
            seedlings = seedlings,
            onDismiss = { selectedNurseryForDetail = null },
            onPreOrderSeedling = { seedling ->
                selectedSeedlingForOrder = seedling
            }
        )
    }

    // Dialog when a nursery card is tapped: allow quick pre-order of that nursery's top seedling
    if (selectedSeedlingForOrder != null) {
        PreOrderDialog(
            seedling = selectedSeedlingForOrder!!,
            onDismiss = { selectedSeedlingForOrder = null },
            onConfirmOrder = { qty, date, pickup, notes ->
                val order = KrishiRepository.submitPreOrder(
                    cropName = selectedSeedlingForOrder!!.cropName,
                    variety = selectedSeedlingForOrder!!.variety,
                    nurseryId = selectedSeedlingForOrder!!.nurseryId,
                    nurseryName = selectedSeedlingForOrder!!.nurseryName,
                    quantity = qty,
                    requiredDate = date,
                    pricePerPlant = selectedSeedlingForOrder!!.pricePerPlant,
                    pickupPreference = pickup,
                    notes = notes
                )
                Toast.makeText(
                    context,
                    "Pre-order ${order.id} submitted for ${order.quantity} ${order.cropName} plants!",
                    Toast.LENGTH_LONG
                ).show()
                selectedSeedlingForOrder = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Sticky Top Header
        TopBar(
            currentLocation = currentUser.location,
            unreadNotifications = 3,
            onLocationClick = {
                Toast.makeText(context, "Location set to: Pune, Maharashtra", Toast.LENGTH_SHORT).show()
            },
            onNotificationClick = {
                Toast.makeText(context, "3 New Notifications: 1 Pre-order accepted, 1 Weather alert, 1 Disease warning", Toast.LENGTH_LONG).show()
            }
        )

        // Scrollable Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // Space for bottom bar
        ) {
            // 1. Hero greeting & search
            GreetingSection(
                farmerName = currentUser.name.split(" ").firstOrNull() ?: "Farmer",
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onScanClick = onNavigateToScan
            )

            // 2. Quick action grid (6 buttons)
            ActionGrid(
                onActionClick = { action ->
                    when (action) {
                        "BUY_PLANTS", "PRE_ORDER" -> onNavigateToMarketplace()
                        "DISEASE_DETECTION" -> onNavigateToScan()
                        "COMMUNITY" -> onNavigateToCommunity()
                        "WEATHER" -> {
                            Toast.makeText(context, "Weather Forecast: 28°C, Partly Cloudy, Rain chance 20%", Toast.LENGTH_SHORT).show()
                        }
                        "CROP_RECOMMENDATION" -> {
                            Toast.makeText(context, "Soil: ${currentUser.soilType} — Recommended: Tomato Hybrid F1 (92% Match)", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )

            // 3. Pre-Order Promotional Banner
            PreOrderPromoBanner(
                onExploreClick = onNavigateToMarketplace
            )

            // 4. AI Crop Recommendation Card
            AiCropRecommendationCard(
                onGetRecommendationClick = {
                    Toast.makeText(
                        context,
                        "Based on your location in Haveli, Pune & ${currentUser.soilType}, Tomato (Hybrid) and Chilli (Teja 4) are optimal for planting.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )

            // 5. Today's Weather & Disease Outbreak Alert
            WeatherAndDiseaseRow(
                weather = weather,
                diseaseAlert = diseaseAlert,
                onWeatherClick = {
                    Toast.makeText(context, "Weather details: Humidity ${weather.humidityPercent}%, Wind ${weather.windKmh} km/h", Toast.LENGTH_SHORT).show()
                },
                onDiseaseAlertClick = {
                    Toast.makeText(context, "${diseaseAlert.diseaseName}: ${diseaseAlert.description}", Toast.LENGTH_LONG).show()
                }
            )

            // 6. Nearby Nurseries
            val filteredNurseries = if (searchQuery.isBlank()) {
                nurseries
            } else {
                nurseries.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.cropsAvailable.any { c -> c.contains(searchQuery, ignoreCase = true) }
                }
            }

            NearbyNurseriesRow(
                nurseries = filteredNurseries,
                onViewAllClick = onNavigateToMarketplace,
                onNurseryClick = { nursery ->
                    selectedNurseryForDetail = nursery
                }
            )
        }
    }
}
