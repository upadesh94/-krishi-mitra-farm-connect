package com.example.krishimitra.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.krishimitra.data.model.Nursery
import com.example.krishimitra.data.model.OrderStatus
import com.example.krishimitra.data.model.PreOrder
import com.example.krishimitra.data.model.SeedlingListing
import com.example.krishimitra.data.model.imageResId
import com.example.krishimitra.data.repository.KrishiRepository
import com.example.krishimitra.ui.theme.*

@Composable
fun MarketplaceScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val seedlings by KrishiRepository.seedlings.collectAsState()
    val preOrders by KrishiRepository.preOrders.collectAsState()
    val nurseries by KrishiRepository.nurseries.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Browse Seedlings, 1: Nearby Nurseries, 2: My Pre-Orders
    var selectedCropFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var requiredQuantity by remember { mutableStateOf("") }
    var selectedSeedlingForOrder by remember { mutableStateOf<SeedlingListing?>(null) }
    var selectedNurseryForDetail by remember { mutableStateOf<Nursery?>(null) }
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    DisposableEffect(locationPermissionGranted) {
        if (!locationPermissionGranted) {
            onDispose { }
        } else {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10_000L)
                .setMinUpdateIntervalMillis(5_000L)
                .build()
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { userLocation = it.latitude to it.longitude }
                }
            }
            client.requestLocationUpdates(request, callback, context.mainLooper)
            onDispose { client.removeLocationUpdates(callback) }
        }
    }

    val cropFilters = listOf("All", "Tomato", "Chilli", "Brinjal", "Cabbage", "Cauliflower", "Capsicum")

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
                    "Pre-Order ${order.id} reserved successfully!",
                    Toast.LENGTH_LONG
                ).show()
                selectedSeedlingForOrder = null
                selectedTab = 1 // Switch to pre-orders view
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
    ) {
        // Top Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "Seedling Marketplace & Pre-Orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search plants or varieties") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = requiredQuantity,
                        onValueChange = { requiredQuantity = it.filter(Char::isDigit) },
                        singleLine = true,
                        label = { Text("Required plants") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (!locationPermissionGranted) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                openNearestNurseryMap(context, userLocation, KrishiRepository.nurseries.value)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (userLocation == null) "Use location" else "Nearest map",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Tab Row: Seedlings vs Nurseries vs My Pre-Orders
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = PrimaryGreen
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Seedlings (${seedlings.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Nurseries (${nurseries.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Pre-Orders (${preOrders.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Filter Pills
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cropFilters) { filter ->
                        FilterChip(
                            selected = selectedCropFilter == filter,
                            onClick = { selectedCropFilter = filter },
                            label = { Text(text = filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Seedlings list
                val filteredSeedlings = seedlings.filter {
                    (selectedCropFilter == "All" || it.cropName.equals(selectedCropFilter, ignoreCase = true)) &&
                    (searchQuery.isBlank() || it.variety.contains(searchQuery, ignoreCase = true) ||
                        it.cropName.contains(searchQuery, ignoreCase = true) ||
                        it.nurseryName.contains(searchQuery, ignoreCase = true)) &&
                    (requiredQuantity.toIntOrNull()?.let { required -> it.availableQuantity >= required } ?: true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSeedlings) { item ->
                        SeedlingCatalogCard(
                            seedling = item,
                            onPreOrderClick = { selectedSeedlingForOrder = item },
                            onNurseryClick = {
                                val found = nurseries.find { it.name.equals(item.nurseryName, ignoreCase = true) || it.id == item.nurseryId }
                                if (found != null) selectedNurseryForDetail = found
                            }
                        )
                    }
                }
            }
            1 -> {
                // Nurseries Tab
                val filteredNurseries = if (searchQuery.isBlank()) nurseries else {
                    nurseries.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.address.contains(searchQuery, ignoreCase = true) ||
                        it.cropsAvailable.any { c -> c.contains(searchQuery, ignoreCase = true) }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredNurseries) { nursery ->
                        NurseryCatalogCard(
                            nursery = nursery,
                            onExploreClick = { selectedNurseryForDetail = nursery }
                        )
                    }
                }
            }
            else -> {
                // My Pre-Orders list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(preOrders) { order ->
                        PreOrderItemCard(order = order)
                    }
                }
            }
        }
    }
}

private fun openNearestNurseryMap(
    context: android.content.Context,
    userLocation: Pair<Double, Double>?,
    nurseries: List<Nursery>
) {
    if (userLocation == null) {
        Toast.makeText(context, "Waiting for your live location. Try again in a moment.", Toast.LENGTH_SHORT).show()
        return
    }
    val nearest = nurseries.minByOrNull {
        distanceKm(userLocation.first, userLocation.second, it.latitude, it.longitude)
    } ?: return
    val uri = Uri.parse(
        "geo:${nearest.latitude},${nearest.longitude}?q=${nearest.latitude},${nearest.longitude}(${Uri.encode(nearest.name)})"
    )
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (intent.resolveActivity(context.packageManager) == null) intent.setPackage(null)
    context.startActivity(intent)
}

private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
        kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
        kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
    return earthRadiusKm * 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
}

@Composable
fun SeedlingCatalogCard(
    seedling: SeedlingListing,
    onPreOrderClick: () -> Unit,
    onNurseryClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Real photo of small plant / pro-tray seedling
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = seedling.imageResId),
                        contentDescription = "${seedling.cropName} seedling",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = seedling.growthStage,
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 1.5.dp, horizontal = 2.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${seedling.cropName} — ${seedling.variety}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = if (onNurseryClick != null) Modifier.clickable(onClick = onNurseryClick) else Modifier
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = seedling.nurseryName,
                            fontSize = 11.sp,
                            color = PrimaryGreenDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GreenSurface
                    ) {
                        Text(
                            text = "₹${String.format("%.2f", seedling.pricePerPlant)} / plant",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryGreenDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = seedling.careNotes,
                fontSize = 11.sp,
                color = TextMedium,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ready: ${seedling.expectedReadyDate}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    }
                    Text(
                        text = "Min order: ${seedling.minOrderQuantity} plants",
                        fontSize = 10.sp,
                        color = TextLight
                    )
                }

                Button(
                    onClick = onPreOrderClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(text = "Pre-Order Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PreOrderItemCard(order: PreOrder) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val statusColor = when (order.status) {
            OrderStatus.ACCEPTED -> PrimaryGreen
            OrderStatus.REQUESTED -> HarvestGold
            OrderStatus.GROWING -> SkyBlue
            OrderStatus.READY_FOR_PICKUP -> PrimaryGreenDark
            OrderStatus.COMPLETED -> PrimaryGreen
            OrderStatus.CANCELLED -> AlertRed
            else -> TextMedium
        }

        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Real small plant thumbnail
                Image(
                    painter = painterResource(id = order.imageResId),
                    contentDescription = order.cropName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = order.id,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = order.status.name.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${order.quantity} × ${order.cropName} (${order.variety})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Nursery: ${order.nurseryName}",
                        fontSize = 11.sp,
                        color = TextMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Ready Date: ${order.requiredDate}", fontSize = 11.sp, color = TextMedium)
                Text(text = "Total: ₹${String.format("%.2f", order.totalAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreenDark)
            }
        }
    }
}

@Composable
fun NurseryCatalogCard(
    nursery: Nursery,
    onExploreClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExploreClick)
    ) {
        Column {
            // Cover Image with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = nursery.imageResId),
                    contentDescription = nursery.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Verified polyhouse badge
                Text(
                    text = "🌱 Verified Polyhouse Cultivator",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )

                // Distance pill badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${nursery.distanceKm} km",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }

                // Rating pill badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${nursery.rating} (${nursery.reviewCount})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }
            }

            // Nursery Details
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = nursery.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${nursery.address} • Managed by ${nursery.ownerName}",
                    fontSize = 11.sp,
                    color = TextMedium
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Available crops tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    nursery.cropsAvailable.forEach { crop ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GreenSurface
                        ) {
                            Text(
                                text = "🌱 $crop",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryGreenDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${nursery.phone}"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                    }

                    Button(
                        onClick = onExploreClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("View Seedlings", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
