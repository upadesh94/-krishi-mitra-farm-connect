package com.example.krishimitra.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.krishimitra.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.krishimitra.data.model.*
import com.example.krishimitra.data.repository.KrishiRepository
import com.example.krishimitra.ui.theme.*
import java.util.UUID

private enum class OwnerNavTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Overview", Icons.Default.Dashboard),
    ORDERS("Orders", Icons.Default.ShoppingCart),
    PRODUCTS("Catalog", Icons.Default.Spa),
    PRODUCTION("Batches", Icons.Default.EnergySavingsLeaf),
    BUSINESS("More", Icons.Default.Storefront)
}

private enum class BusinessSubModule(val title: String, val subtitle: String, val icon: ImageVector) {
    INVENTORY("Inventory & Stock", "Pro-tray counts, reservations and adjustments", Icons.Default.Warehouse),
    SALES("Sales & Payments", "Revenue tracking and farmer payment receipts", Icons.Default.Payments),
    OFFERS("Promotions & Offers", "Seasonal discounts and bulk farmer rates", Icons.Default.LocalOffer),
    REVIEWS("Farmer Reviews", "Ratings, community feedback and responses", Icons.Default.Star),
    REPORTS("Analytics & Reports", "Best-selling crops, volume and insights", Icons.Default.Assessment),
    NURSERIES("My Nurseries", "Locations, facilities and polyhouse profiles", Icons.Default.LocationOn),
    NOTIFICATIONS("Alerts & Notifications", "Incoming orders, stock and demand updates", Icons.Default.Notifications),
    SETTINGS("Account Settings", "Owner profile, preferences and logout", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseryOwnerDashboard(onSignOut: () -> Unit) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(OwnerNavTab.DASHBOARD) }
    var activeSubModule by remember { mutableStateOf<BusinessSubModule?>(null) }
    var selectedNurseryId by remember { mutableStateOf("nursery_1") }

    val nurseries by KrishiRepository.ownerNurseries.collectAsState()
    val orders by KrishiRepository.preOrders.collectAsState()
    val products by KrishiRepository.ownerProducts.collectAsState()
    val stock by KrishiRepository.inventory.collectAsState()
    val batches by KrishiRepository.productionBatches.collectAsState()
    val notifications by KrishiRepository.notifications.collectAsState()
    val unreadNotificationsCount by remember { derivedStateOf { notifications.count { !it.read } } }

    var showAddNurseryDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddBatchDialog by remember { mutableStateOf(false) }
    var showCreateOfferDialog by remember { mutableStateOf(false) }

    val activeNursery = nurseries.firstOrNull { it.id == selectedNurseryId } ?: nurseries.firstOrNull()

    // Dialogs
    if (showAddNurseryDialog) {
        OwnerFormDialog(
            title = "Add Polyhouse Nursery",
            subtitle = "Register a new nursery facility location",
            fields = listOf("Nursery Name", "Owner Name", "Contact Phone", "Address", "Taluka / District"),
            onDismiss = { showAddNurseryDialog = false },
            onSave = { values ->
                val newId = "nursery_${UUID.randomUUID().toString().take(6)}"
                KrishiRepository.addNursery(
                    Nursery(
                        id = newId,
                        name = values[0],
                        ownerName = values[1],
                        phone = values[2],
                        address = values[3],
                        locationName = values.getOrNull(4) ?: "Maharashtra",
                        isVerified = true
                    )
                )
                selectedNurseryId = newId
                showAddNurseryDialog = false
                Toast.makeText(context, "Nursery registered successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddProductDialog) {
        OwnerFormDialog(
            title = "Add Seedling Listing",
            subtitle = "Publish a healthy pro-tray crop to your catalog",
            fields = listOf("Crop Name (e.g. Tomato)", "Variety (e.g. Abhinav F1)", "Price per Seedling (₹)", "Tray Cell Size (e.g. 104)", "Minimum Order", "Available Seedlings", "Expected Ready Date"),
            onDismiss = { showAddProductDialog = false },
            onSave = { values ->
                val newProd = NurseryProduct(
                    id = "prod_${UUID.randomUUID().toString().take(6)}",
                    nurseryId = selectedNurseryId,
                    name = values[0],
                    category = "Vegetable",
                    variety = values[1],
                    pricePerSeedling = values.getOrNull(2)?.toDoubleOrNull() ?: 1.20,
                    seedlingsPerTray = values.getOrNull(3)?.toIntOrNull() ?: 100,
                    minimumOrder = values.getOrNull(4)?.toIntOrNull() ?: 500,
                    availableQuantity = values.getOrNull(5)?.toIntOrNull() ?: 10000,
                    expectedAvailability = values.getOrNull(6) ?: "15 Nov 2026",
                    availability = ProductAvailability.AVAILABLE
                )
                KrishiRepository.addProduct(newProd)
                showAddProductDialog = false
                Toast.makeText(context, "Seedling added to catalog!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddBatchDialog) {
        OwnerFormDialog(
            title = "Plan Production Batch",
            subtitle = "Schedule plug tray sowing in cocopeat",
            fields = listOf("Crop Name", "Variety", "Planned Seedling Count", "Expected Ready Date", "Certified Seed Source"),
            onDismiss = { showAddBatchDialog = false },
            onSave = { values ->
                KrishiRepository.addBatch(
                    ProductionBatch(
                        id = "batch_${UUID.randomUUID().toString().take(6)}",
                        nurseryId = selectedNurseryId,
                        cropName = values[0],
                        variety = values[1],
                        plannedQuantity = values.getOrNull(2)?.toIntOrNull() ?: 5000,
                        expectedCompletion = values.getOrNull(3) ?: "25 Nov 2026",
                        seedSource = values.getOrNull(4) ?: "Authorized Breeder F1",
                        status = ProductionStatus.SOWING_COMPLETED
                    )
                )
                showAddBatchDialog = false
                Toast.makeText(context, "Production batch scheduled in pro-trays!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showCreateOfferDialog) {
        OwnerFormDialog(
            title = "Create Farmer Special Offer",
            subtitle = "Encourage early pre-orders with bulk discounts",
            fields = listOf("Offer Title", "Crop Name", "Minimum Order Quantity", "Discount Percent (%)", "Valid Until Date"),
            onDismiss = { showCreateOfferDialog = false },
            onSave = { values ->
                KrishiRepository.addOffer(
                    NurseryOffer(
                        id = "offer_${UUID.randomUUID().toString().take(6)}",
                        nurseryId = selectedNurseryId,
                        title = values[0],
                        crop = values[1],
                        minimumQuantity = values.getOrNull(2)?.toIntOrNull() ?: 1000,
                        discountPercent = values.getOrNull(3)?.toIntOrNull() ?: 10,
                        validUntil = values.getOrNull(4) ?: "30 Nov 2026"
                    )
                )
                showCreateOfferDialog = false
                Toast.makeText(context, "Special offer published!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.krishi_mitra_app_icon),
                                contentDescription = "Krishi Mitra Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeNursery?.name ?: "KrishiMitra Nursery",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = "🌱 Polyhouse Cultivation Partner",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryGreenDark
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Notifications button with badge
                            IconButton(
                                onClick = {
                                    currentTab = OwnerNavTab.BUSINESS
                                    activeSubModule = BusinessSubModule.NOTIFICATIONS
                                }
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotificationsCount > 0) {
                                            Badge(containerColor = AlertRed) {
                                                Text(text = unreadNotificationsCount.toString(), color = Color.White)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Alerts",
                                        tint = TextDark
                                    )
                                }
                            }

                            // Sign out
                            IconButton(onClick = onSignOut) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Sign out",
                                    tint = TextMedium
                                )
                            }
                        }
                    }

                    // Nursery Switcher Row if multiple nurseries exist
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        nurseries.forEach { n ->
                            val isSelected = n.id == selectedNurseryId
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedNurseryId = n.id },
                                label = { Text(text = n.name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryGreen,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }

                        AssistChip(
                            onClick = { showAddNurseryDialog = true },
                            label = { Text("+ Add Nursery", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen) },
                            leadingIcon = {
                                Icon(Icons.Default.Add, null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                OwnerNavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            currentTab = tab
                            activeSubModule = null
                        },
                        icon = {
                            Icon(imageVector = tab.icon, contentDescription = tab.label)
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreenDark,
                            selectedTextColor = PrimaryGreenDark,
                            indicatorColor = GreenSurface,
                            unselectedIconColor = TextMedium,
                            unselectedTextColor = TextMedium
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentTab) {
                OwnerNavTab.DASHBOARD -> {
                    OwnerDashboardTab(
                        nurseryId = selectedNurseryId,
                        onNavigateToOrders = { currentTab = OwnerNavTab.ORDERS },
                        onNavigateToProducts = { currentTab = OwnerNavTab.PRODUCTS },
                        onNavigateToProduction = { currentTab = OwnerNavTab.PRODUCTION },
                        onAddProduct = { showAddProductDialog = true },
                        onPlanBatch = { showAddBatchDialog = true },
                        onCreateOffer = { showCreateOfferDialog = true }
                    )
                }
                OwnerNavTab.ORDERS -> {
                    OwnerOrdersTab(nurseryId = selectedNurseryId)
                }
                OwnerNavTab.PRODUCTS -> {
                    OwnerProductsTab(
                        nurseryId = selectedNurseryId,
                        onAddProduct = { showAddProductDialog = true }
                    )
                }
                OwnerNavTab.PRODUCTION -> {
                    OwnerProductionTab(
                        nurseryId = selectedNurseryId,
                        onPlanBatch = { showAddBatchDialog = true }
                    )
                }
                OwnerNavTab.BUSINESS -> {
                    if (activeSubModule == null) {
                        OwnerBusinessHubTab(
                            onSelectModule = { activeSubModule = it },
                            onSignOut = onSignOut
                        )
                    } else {
                        OwnerSubModuleScreen(
                            subModule = activeSubModule!!,
                            nurseryId = selectedNurseryId,
                            onBack = { activeSubModule = null },
                            onAddNursery = { showAddNurseryDialog = true },
                            onCreateOffer = { showCreateOfferDialog = true },
                            onSignOut = onSignOut
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 1. DASHBOARD TAB
// ─────────────────────────────────────────────────────────────

@Composable
private fun OwnerDashboardTab(
    nurseryId: String,
    onNavigateToOrders: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToProduction: () -> Unit,
    onAddProduct: () -> Unit,
    onPlanBatch: () -> Unit,
    onCreateOffer: () -> Unit
) {
    val orders by KrishiRepository.preOrders.collectAsState()
    val products by KrishiRepository.ownerProducts.collectAsState()
    val stock by KrishiRepository.inventory.collectAsState()
    val currentUser by KrishiRepository.currentUser.collectAsState()

    val nurseryOrders = orders.filter { it.nurseryId == nurseryId }
    val pendingRequests = nurseryOrders.filter { it.status == OrderStatus.REQUESTED }
    val totalRevenue = nurseryOrders.filter { it.status == OrderStatus.COMPLETED }.sumOf { it.totalAmount }
    val usableStock = stock.filter { it.nurseryId == nurseryId }.sumOf { it.availableStock }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F3E22), Color(0xFF166534), Color(0xFF15803D))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Good day, ${currentUser.name.split(" ").firstOrNull() ?: "Owner"} 🌿",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${pendingRequests.size} new farmer pre-orders awaiting your approval",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD1FAE5)
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricTile(
                        label = "Pre-Orders",
                        value = "${nurseryOrders.size}",
                        subtext = "${pendingRequests.size} pending",
                        icon = Icons.Default.ShoppingCart,
                        accentColor = HarvestGold,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                    MetricTile(
                        label = "Catalog Seedlings",
                        value = "${products.filter { it.nurseryId == nurseryId }.size}",
                        subtext = "Active varieties",
                        icon = Icons.Default.Spa,
                        accentColor = PrimaryGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProducts
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricTile(
                        label = "Usable Seedlings",
                        value = "$usableStock",
                        subtext = "In pro-tray cells",
                        icon = Icons.Default.Warehouse,
                        accentColor = SkyBlue,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProduction
                    )
                    MetricTile(
                        label = "Completed Revenue",
                        value = "₹${String.format("%.0f", totalRevenue)}",
                        subtext = "Confirmed dispatch",
                        icon = Icons.Default.Payments,
                        accentColor = PrimaryGreenDark,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Quick Actions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionPillButton(
                            text = "+ Add Product",
                            icon = Icons.Default.Add,
                            modifier = Modifier.weight(1f),
                            onClick = onAddProduct
                        )
                        ActionPillButton(
                            text = "Plan Batch",
                            icon = Icons.Default.EnergySavingsLeaf,
                            modifier = Modifier.weight(1f),
                            onClick = onPlanBatch
                        )
                        ActionPillButton(
                            text = "Create Offer",
                            icon = Icons.Default.LocalOffer,
                            modifier = Modifier.weight(1f),
                            onClick = onCreateOffer
                        )
                    }
                }
            }
        }

        // Recent Orders Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Farmer Orders",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                TextButton(onClick = onNavigateToOrders) {
                    Text(text = "View All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                }
            }
        }

        // Recent Orders List
        if (nurseryOrders.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No orders received yet. Farmer requests will appear here instantly.",
                        fontSize = 12.sp,
                        color = TextMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            items(nurseryOrders.take(5), key = { it.id }) { order ->
                OwnerOrderCard(order = order)
            }
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                }
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextLight, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
            Text(text = subtext, fontSize = 10.sp, color = TextMedium)
        }
    }
}

@Composable
private fun ActionPillButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenSurface,
            contentColor = PrimaryGreenDark
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        modifier = modifier.height(36.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

// ─────────────────────────────────────────────────────────────
// 2. ORDERS TAB
// ─────────────────────────────────────────────────────────────

@Composable
private fun OwnerOrdersTab(nurseryId: String) {
    val orders by KrishiRepository.preOrders.collectAsState()
    val nurseryOrders = orders.filter { it.nurseryId == nurseryId }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Accepted", "Production", "Growing", "Ready", "Completed")

    val filtered = nurseryOrders.filter { order ->
        when (selectedFilter) {
            "All" -> true
            "Pending" -> order.status == OrderStatus.REQUESTED
            "Accepted" -> order.status == OrderStatus.ACCEPTED
            "Production" -> order.status == OrderStatus.PRODUCTION_PLANNED
            "Growing" -> order.status == OrderStatus.GROWING
            "Ready" -> order.status == OrderStatus.READY_FOR_PICKUP
            "Completed" -> order.status == OrderStatus.COMPLETED
            else -> true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { f ->
                FilterChip(
                    selected = selectedFilter == f,
                    onClick = { selectedFilter = f },
                    label = { Text(text = f, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No orders found in '$selectedFilter' status.",
                            fontSize = 12.sp,
                            color = TextMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            } else {
                items(filtered, key = { it.id }) { order ->
                    OwnerOrderCard(order = order)
                }
            }
        }
    }
}

@Composable
private fun OwnerOrderCard(order: PreOrder) {
    val context = LocalContext.current
    val statusColor = when (order.status) {
        OrderStatus.ACCEPTED -> PrimaryGreen
        OrderStatus.REQUESTED -> HarvestGold
        OrderStatus.PRODUCTION_PLANNED -> Color(0xFF7C3AED)
        OrderStatus.GROWING -> SkyBlue
        OrderStatus.READY_FOR_PICKUP -> PrimaryGreenDark
        OrderStatus.COMPLETED -> PrimaryGreen
        OrderStatus.CANCELLED -> AlertRed
        else -> TextMedium
    }

    val nextStatus = when (order.status) {
        OrderStatus.REQUESTED -> OrderStatus.ACCEPTED to "Accept Order"
        OrderStatus.ACCEPTED -> OrderStatus.PRODUCTION_PLANNED to "Plan Production"
        OrderStatus.PRODUCTION_PLANNED -> OrderStatus.GROWING to "Mark Growing"
        OrderStatus.GROWING -> OrderStatus.READY_FOR_PICKUP to "Ready for Pickup"
        OrderStatus.READY_FOR_PICKUP -> OrderStatus.COMPLETED to "Handover Completed"
        else -> null
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: ID and Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${order.pickupPreference}",
                        fontSize = 10.sp,
                        color = TextMedium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
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

            Spacer(modifier = Modifier.height(10.dp))

            // Body: Small Plant Image + Order Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = order.imageResId),
                    contentDescription = order.cropName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${order.cropName} — ${order.variety}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "${order.quantity} seedlings • ₹${order.pricePerPlant}/seedling",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryGreenDark
                    )
                    Text(
                        text = "Farmer: ${order.farmerName}",
                        fontSize = 11.sp,
                        color = TextMedium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${String.format("%.0f", order.totalAmount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                    Text(
                        text = "Due: ${order.requiredDate}",
                        fontSize = 10.sp,
                        color = TextLight
                    )
                }
            }

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 Note: ${order.notes}",
                        fontSize = 10.sp,
                        color = TextMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.farmerPhone}"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Call Farmer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                }

                if (nextStatus != null) {
                    Button(
                        onClick = {
                            val success = KrishiRepository.updateOwnerOrder(order.id, nextStatus.first)
                            if (success) {
                                Toast.makeText(context, "Order ${order.id} updated to ${nextStatus.first.name}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(36.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(text = nextStatus.second, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 3. PRODUCTS TAB
// ─────────────────────────────────────────────────────────────

@Composable
private fun OwnerProductsTab(
    nurseryId: String,
    onAddProduct: () -> Unit
) {
    val products by KrishiRepository.ownerProducts.collectAsState()
    val nurseryProducts = products.filter { it.nurseryId == nurseryId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Seedling Product Catalog",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "${nurseryProducts.size} listings published for farmers",
                        fontSize = 11.sp,
                        color = TextMedium
                    )
                }
                Button(
                    onClick = onAddProduct,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Seedling", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        if (nurseryProducts.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No seedlings in your catalog yet. Tap '+ Add Seedling' to add your pro-tray crops.",
                        fontSize = 12.sp,
                        color = TextMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        } else {
            items(nurseryProducts, key = { it.id }) { product ->
                Card(
                    shape = RoundedCornerShape(18.dp),
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
                            // Real Small Plant Image
                            Image(
                                painter = painterResource(id = product.imageResId),
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${product.name} — ${product.variety}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "₹${String.format("%.2f", product.pricePerSeedling)} / seedling",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tray: ${product.seedlingsPerTray} cells • Min: ${product.minimumOrder} plants",
                                    fontSize = 10.sp,
                                    color = TextMedium
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GreenSurface
                            ) {
                                Text(
                                    text = "${product.availableQuantity} in stock",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreenDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available: ${product.expectedAvailability}",
                                fontSize = 11.sp,
                                color = TextMedium
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = product.availability.name.replace("_", " "),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 4. PRODUCTION & DEMAND TAB
// ─────────────────────────────────────────────────────────────

@Composable
private fun OwnerProductionTab(
    nurseryId: String,
    onPlanBatch: () -> Unit
) {
    val batches by KrishiRepository.productionBatches.collectAsState()
    val orders by KrishiRepository.preOrders.collectAsState()
    val inventory by KrishiRepository.inventory.collectAsState()
    val nurseryBatches = batches.filter { it.nurseryId == nurseryId }

    val confirmedDemandGrouped = orders
        .filter { it.nurseryId == nurseryId && it.status !in setOf(OrderStatus.CANCELLED, OrderStatus.DRAFT) }
        .groupBy { it.cropName to it.variety }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Demand Planning Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GreenSurface,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Smart Sowing Demand Estimation",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "Based on active farmer pre-orders",
                                    fontSize = 11.sp,
                                    color = TextMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (confirmedDemandGrouped.isEmpty()) {
                        Text(
                            text = "No confirmed farmer pre-orders to estimate demand.",
                            fontSize = 11.sp,
                            color = TextLight
                        )
                    } else {
                        confirmedDemandGrouped.forEach { (key, orderList) ->
                            val totalDemand = orderList.sumOf { it.quantity }
                            val usable = inventory.firstOrNull { it.productName == key.first }?.availableStock ?: 0
                            val buffer = (totalDemand * 0.10).toInt()
                            val suggestedBatch = (totalDemand + buffer - usable).coerceAtLeast(0)

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "${key.first} (${key.second})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(text = "Demand: $totalDemand", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Current Usable Stock: $usable plants • 10% Safety Buffer: +$buffer",
                                        fontSize = 11.sp,
                                        color = TextMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "👉 Recommended Sowing Batch: $suggestedBatch seedlings",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreenDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Production Batches Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Active Pro-Tray Batches", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "${nurseryBatches.size} batches in germination & growth", fontSize = 11.sp, color = TextMedium)
                }
                Button(
                    onClick = onPlanBatch,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Plan Batch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Batch Cards
        items(nurseryBatches, key = { it.id }) { b ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = getSeedlingImageRes(b.cropName)),
                            contentDescription = b.cropName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${b.cropName} • ${b.variety}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(text = "${b.plannedQuantity} seedlings planned • ${b.seedSource}", fontSize = 11.sp, color = TextMedium)
                            Text(text = "Target Ready: ${b.expectedCompletion}", fontSize = 11.sp, color = PrimaryGreenDark, fontWeight = FontWeight.SemiBold)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenSurface
                        ) {
                            Text(
                                text = b.status.name.replace("_", " "),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreenDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 5. BUSINESS HUB TAB (MORE)
// ─────────────────────────────────────────────────────────────

@Composable
private fun OwnerBusinessHubTab(
    onSelectModule: (BusinessSubModule) -> Unit,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Nursery Business Hub",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )
            Text(
                text = "Manage inventory, sales, reviews, and nursery profiles",
                fontSize = 12.sp,
                color = TextMedium
            )
        }

        items(BusinessSubModule.values()) { mod ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (mod == BusinessSubModule.SETTINGS) onSelectModule(mod)
                        else onSelectModule(mod)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenSurface,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = mod.icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = mod.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(text = mod.subtitle, fontSize = 11.sp, color = TextMedium)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextLight, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 6. BUSINESS SUB-MODULE SCREEN
// ─────────────────────────────────────────────────────────────

@Composable
private fun OwnerSubModuleScreen(
    subModule: BusinessSubModule,
    nurseryId: String,
    onBack: () -> Unit,
    onAddNursery: () -> Unit,
    onCreateOffer: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Sub-module Top Bar with Back Arrow
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(text = subModule.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = subModule.subtitle, fontSize = 11.sp, color = TextMedium)
                }
            }
        }

        // Sub-module content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (subModule) {
                BusinessSubModule.INVENTORY -> {
                    val items by KrishiRepository.inventory.collectAsState()
                    val nurseryInventory = items.filter { it.nurseryId == nurseryId }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(nurseryInventory) { item ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = getSeedlingImageRes(item.productName)),
                                        contentDescription = item.productName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "${item.productName} • ${item.variety}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                                        Text(
                                            text = "Available: ${item.availableStock} • Reserved: ${item.reservedStock}",
                                            fontSize = 11.sp,
                                            color = PrimaryGreenDark,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(text = "Damaged: ${item.damagedStock}", fontSize = 10.sp, color = TextLight)
                                    }

                                    Row {
                                        IconButton(onClick = {
                                            KrishiRepository.adjustStock(item.productId, 100, "RECEIPT")
                                            Toast.makeText(context, "+100 seedlings added to ${item.productName}", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Default.Add, "Add 100", tint = PrimaryGreen)
                                        }
                                        IconButton(onClick = {
                                            val ok = KrishiRepository.adjustStock(item.productId, -100, "WASTAGE")
                                            if (ok) Toast.makeText(context, "-100 seedlings deducted", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Default.Remove, "Deduct 100", tint = AlertRed)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                BusinessSubModule.SALES -> {
                    val orders by KrishiRepository.preOrders.collectAsState()
                    val completed = orders.filter { it.nurseryId == nurseryId && it.status == OrderStatus.COMPLETED }
                    val pending = orders.filter { it.nurseryId == nurseryId && it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MetricTile(
                                    label = "Received Revenue",
                                    value = "₹${String.format("%.0f", completed.sumOf { it.totalAmount })}",
                                    subtext = "${completed.size} orders paid",
                                    icon = Icons.Default.Payments,
                                    accentColor = PrimaryGreen,
                                    modifier = Modifier.weight(1f),
                                    onClick = {}
                                )
                                MetricTile(
                                    label = "Pending Receivable",
                                    value = "₹${String.format("%.0f", pending.sumOf { it.totalAmount })}",
                                    subtext = "${pending.size} orders in pipeline",
                                    icon = Icons.Default.Schedule,
                                    accentColor = HarvestGold,
                                    modifier = Modifier.weight(1f),
                                    onClick = {}
                                )
                            }
                        }
                    }
                }

                BusinessSubModule.OFFERS -> {
                    val offers by KrishiRepository.offers.collectAsState()
                    val nurseryOffers = offers.filter { it.nurseryId == nurseryId }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Button(
                                onClick = onCreateOffer,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create New Promotional Offer", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (nurseryOffers.isEmpty()) {
                            item {
                                Surface(shape = RoundedCornerShape(14.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                                    Text("No promotional offers created yet.", modifier = Modifier.padding(24.dp), textAlign = TextAlign.Center, color = TextMedium)
                                }
                            }
                        } else {
                            items(nurseryOffers) { o ->
                                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = o.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(text = "${o.crop} • ${o.discountPercent}% off on orders above ${o.minimumQuantity} seedlings", fontSize = 12.sp, color = PrimaryGreenDark)
                                        Text(text = "Valid until: ${o.validUntil}", fontSize = 11.sp, color = TextLight)
                                    }
                                }
                            }
                        }
                    }
                }

                BusinessSubModule.REVIEWS -> {
                    val reviews by KrishiRepository.reviews.collectAsState()
                    val nurseryReviews = reviews.filter { it.nurseryId == nurseryId }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (nurseryReviews.isEmpty()) {
                            item {
                                Surface(shape = RoundedCornerShape(14.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                                    Text("No farmer reviews submitted yet.", modifier = Modifier.padding(24.dp), textAlign = TextAlign.Center, color = TextMedium)
                                }
                            }
                        } else {
                            items(nurseryReviews) { r ->
                                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = r.farmerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                                            Text(text = "★".repeat(r.rating), color = HarvestGold, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = r.comment, fontSize = 12.sp, color = TextMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                BusinessSubModule.REPORTS -> {
                    val orders by KrishiRepository.preOrders.collectAsState()
                    val products by KrishiRepository.ownerProducts.collectAsState()
                    val nurseryOrders = orders.filter { it.nurseryId == nurseryId }

                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(text = "Executive Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                            Spacer(modifier = Modifier.height(10.dp))
                            ReportRow("Total Farmer Pre-Orders", "${nurseryOrders.size}")
                            ReportRow("Fulfilled & Dispatched", "${nurseryOrders.count { it.status == OrderStatus.COMPLETED }}")
                            ReportRow("Active Catalog Crops", "${products.filter { it.nurseryId == nurseryId }.size}")
                            ReportRow("Top In-Demand Crop", nurseryOrders.groupingBy { it.cropName }.eachCount().maxByOrNull { it.value }?.key ?: "Tomato")
                        }
                    }
                }

                BusinessSubModule.NURSERIES -> {
                    val list by KrishiRepository.ownerNurseries.collectAsState()
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Button(onClick = onAddNursery, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Register New Facility Location", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        items(list) { n ->
                            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = n.imageResId),
                                        contentDescription = n.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = n.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                                        Text(text = "${n.address} • ${n.phone}", fontSize = 11.sp, color = TextMedium)
                                        Text(text = if (n.isVerified) "🌱 Approved Polyhouse" else "Pending Verification", fontSize = 10.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                BusinessSubModule.NOTIFICATIONS -> {
                    val notes by KrishiRepository.notifications.collectAsState()
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(notes) { note ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = if (note.read) Color.White else GreenSurface),
                                modifier = Modifier.clickable { KrishiRepository.markNotificationRead(note.id) }
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (note.read) Icons.Default.Drafts else Icons.Default.MarkEmailUnread,
                                        contentDescription = null,
                                        tint = if (note.read) TextLight else PrimaryGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                                        Text(text = note.message, fontSize = 11.sp, color = TextMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                BusinessSubModule.SETTINGS -> {
                    val user by KrishiRepository.currentUser.collectAsState()
                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(text = "Owner Account", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = user.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = user.email, fontSize = 11.sp, color = TextMedium)
                            Text(text = user.phone, fontSize = 11.sp, color = TextMedium)
                            Spacer(modifier = Modifier.height(18.dp))
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log out of Nursery Portal")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMedium)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

// ─────────────────────────────────────────────────────────────
// REUSABLE DIALOG FOR ADDING NURSERY / PRODUCT / BATCH
// ─────────────────────────────────────────────────────────────

@Composable
private fun OwnerFormDialog(
    title: String,
    subtitle: String,
    fields: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    var values by remember { mutableStateOf(List(fields.size) { "" }) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(text = subtitle, fontSize = 11.sp, color = TextMedium)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, null, tint = TextMedium)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                fields.forEachIndexed { index, label ->
                    OutlinedTextField(
                        value = values[index],
                        onValueChange = { newVal ->
                            values = values.toMutableList().also { it[index] = newVal }
                        },
                        label = { Text(label, fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (values.firstOrNull()?.isNotBlank() == true) {
                                onSave(values)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text("Save & Publish", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
