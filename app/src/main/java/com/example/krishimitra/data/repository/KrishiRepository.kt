package com.example.krishimitra.data.repository

import com.example.krishimitra.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object KrishiRepository {

    // Current logged-in user profile
    private val _currentUser = MutableStateFlow(
        UserProfile(
            uid = "user_demo_101",
            name = "Suresh Patil",
            phone = "+91 98765 43210",
            email = "suresh.patil@krishimitra.in",
            role = UserRole.FARMER,
            location = "Pune, Maharashtra",
            taluka = "Haveli",
            district = "Pune",
            farmSizeAcres = 3.5,
            soilType = "Medium Black Soil",
            waterSource = "Borewell & Drip Irrigation",
            preferredLanguage = "English"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Nearby nurseries list (matching mockup)
    private val _nurseries = MutableStateFlow(
        listOf(
            Nursery(
                id = "nursery_1",
                name = "Green Valley Nursery",
                ownerName = "Ramesh Gaikwad",
                locationName = "Pune, Maharashtra",
                distanceKm = 2.1,
                rating = 4.5,
                reviewCount = 120,
                cropsAvailable = listOf("Tomato", "Chilli", "Brinjal"),
                phone = "+91 98220 11223",
                address = "Pune-Bangalore Highway, Khed Shivapur, Pune",
                imageUrl = "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?auto=format&fit=crop&w=400&q=80",
                latitude = 18.4088,
                longitude = 73.7915,
                ownerEmail = "pragatiseed@gmail.com"
            ),
            Nursery(
                id = "nursery_2",
                name = "Shree Sai Agro Nursery",
                ownerName = "Anil Shinde",
                locationName = "Pune, Maharashtra",
                distanceKm = 3.4,
                rating = 4.3,
                reviewCount = 95,
                cropsAvailable = listOf("Cabbage", "Cauliflower", "Chilli"),
                phone = "+91 98220 44556",
                address = "Saswad Road, Hadapsar, Pune",
                imageUrl = "https://images.unsplash.com/photo-1592417817098-8f3d6910985b?auto=format&fit=crop&w=400&q=80",
                latitude = 18.5089,
                longitude = 73.9260,
                ownerEmail = "pragatiseed@gmail.com"
            ),
            Nursery(
                id = "nursery_3",
                name = "MahaPlant Nursery",
                ownerName = "Mahesh Deshmukh",
                locationName = "Pune, Maharashtra",
                distanceKm = 5.0,
                rating = 4.6,
                reviewCount = 210,
                cropsAvailable = listOf("Tomato", "Capsicum", "Brinjal"),
                phone = "+91 98220 77889",
                address = "Narayangaon, Junnar Taluka, Pune",
                imageUrl = "https://images.unsplash.com/photo-1530595467537-0b5996c41f2d?auto=format&fit=crop&w=400&q=80",
                latitude = 19.1022,
                longitude = 73.9798,
                ownerEmail = "pragatiseed@gmail.com"
            )
        )
    )
    val nurseries: StateFlow<List<Nursery>> = _nurseries.asStateFlow()

    // Seedling catalog with prices and pre-order dates
    private val _seedlings = MutableStateFlow(
        listOf(
            SeedlingListing(
                id = "seed_1",
                nurseryId = "nursery_1",
                nurseryName = "Green Valley Nursery",
                cropName = "Tomato",
                variety = "Abhinav (Hybrid F1)",
                pricePerPlant = 1.25,
                minOrderQuantity = 500,
                availableQuantity = 15000,
                expectedReadyDate = "15 Nov 2026",
                growthStage = "21-Day Plug Tray",
                imageUrl = "https://images.unsplash.com/photo-1592841200221-a6898f307baa?auto=format&fit=crop&w=400&q=80",
                isPreOrderOpen = true,
                careNotes = "Resistant to Tomato Yellow Leaf Curl Virus (TYLCV). High yield potential."
            ),
            SeedlingListing(
                id = "seed_2",
                nurseryId = "nursery_1",
                nurseryName = "Green Valley Nursery",
                cropName = "Chilli",
                variety = "Teja 4 (G4 Hybrid)",
                pricePerPlant = 1.10,
                minOrderQuantity = 500,
                availableQuantity = 12000,
                expectedReadyDate = "20 Nov 2026",
                growthStage = "25-Day Plug Tray",
                imageUrl = "https://images.unsplash.com/photo-1588252303782-cb80119abd6d?auto=format&fit=crop&w=400&q=80",
                isPreOrderOpen = true,
                careNotes = "Dark green pungent fruits, suitable for fresh market and drying."
            ),
            SeedlingListing(
                id = "seed_3",
                nurseryId = "nursery_3",
                nurseryName = "MahaPlant Nursery",
                cropName = "Brinjal",
                variety = "Manjari Gota (Special)",
                pricePerPlant = 1.30,
                minOrderQuantity = 400,
                availableQuantity = 8000,
                expectedReadyDate = "18 Nov 2026",
                growthStage = "22-Day Plug Tray",
                imageUrl = "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?auto=format&fit=crop&w=400&q=80",
                isPreOrderOpen = true,
                careNotes = "Classic round variegated purple-white brinjal favored in Pune and Mumbai markets."
            ),
            SeedlingListing(
                id = "seed_4",
                nurseryId = "nursery_2",
                nurseryName = "Shree Sai Agro Nursery",
                cropName = "Cabbage",
                variety = "Green Express (F1)",
                pricePerPlant = 0.95,
                minOrderQuantity = 1000,
                availableQuantity = 25000,
                expectedReadyDate = "25 Nov 2026",
                growthStage = "20-Day Plug Tray",
                imageUrl = "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=400&q=80",
                isPreOrderOpen = true,
                careNotes = "Uniform round solid heads, excellent field holding capacity."
            ),
            SeedlingListing(
                id = "seed_5",
                nurseryId = "nursery_2",
                nurseryName = "Shree Sai Agro Nursery",
                cropName = "Cauliflower",
                variety = "Snow White 108",
                pricePerPlant = 1.15,
                minOrderQuantity = 500,
                availableQuantity = 10000,
                expectedReadyDate = "28 Nov 2026",
                growthStage = "22-Day Plug Tray",
                imageUrl = "https://images.unsplash.com/photo-1568584711075-3d021a7c3ca3?auto=format&fit=crop&w=400&q=80",
                isPreOrderOpen = true,
                careNotes = "Dome-shaped dense white curds, excellent tolerance to early winter fluctuations."
            ),
            SeedlingListing(
                id = "seed_6",
                nurseryId = "nursery_3",
                nurseryName = "MahaPlant Nursery",
                cropName = "Capsicum",
                variety = "Indra Yellow & Green (Hybrid)",
                pricePerPlant = 2.40,
                minOrderQuantity = 300,
                availableQuantity = 6000,
                expectedReadyDate = "10 Dec 2026",
                growthStage = "28-Day Plug Tray",
                imageUrl = "https://images.unsplash.com/photo-1563565375-f3fdfdbefa83?auto=format&fit=crop&w=400&q=80",
                isPreOrderOpen = true,
                careNotes = "Polyhouse and shadenet suitable 4-lobed blocky capsicum."
            )
        )
    )
    val seedlings: StateFlow<List<SeedlingListing>> = _seedlings.asStateFlow()

    // Pre-Orders state
    private val _preOrders = MutableStateFlow(
        listOf(
            PreOrder(
                id = "ORD-7021",
                farmerId = "user_demo_101",
                farmerName = "Suresh Patil",
                farmerPhone = "+91 98765 43210",
                nurseryId = "nursery_1",
                nurseryName = "Green Valley Nursery",
                cropName = "Tomato",
                variety = "Abhinav (Hybrid F1)",
                quantity = 1500,
                requiredDate = "15 Nov 2026",
                pricePerPlant = 1.25,
                totalAmount = 1875.0,
                status = OrderStatus.ACCEPTED,
                pickupPreference = "Self Pickup at Nursery",
                notes = "Need healthy pro-tray plants for 1-acre drip plot."
            )
        )
    )
    val preOrders: StateFlow<List<PreOrder>> = _preOrders.asStateFlow()

    // Nursery-owner workspace. These flows are also the offline-first cache used
    // when Firebase is unavailable; each mutation is mirrored to Firestore.
    private val _ownerNurseries = MutableStateFlow(
        listOf(Nursery(
            id = "nursery_1", name = "Green Valley Nursery", ownerName = "Ramesh Gaikwad",
            locationName = "Pune, Maharashtra", phone = "+91 98220 11223",
            address = "Pune-Bangalore Highway, Khed Shivapur, Pune", isVerified = true,
            ownerEmail = "pragatiseed@gmail.com"
        ))
    )
    val ownerNurseries: StateFlow<List<Nursery>> = _ownerNurseries.asStateFlow()
    private val _ownerProducts = MutableStateFlow(listOf(
        NurseryProduct("prod_1", "nursery_1", "Tomato", "Vegetable", "Abhinav Hybrid F1",
            "Disease-hardened plug tray seedlings", pricePerSeedling = 1.25, pricePerTray = 125.0,
            seedlingsPerTray = 100, minimumOrder = 500, bulkPrice = 1.10, availableQuantity = 15000,
            expectedAvailability = "15 Nov 2026", availability = ProductAvailability.AVAILABLE),
        NurseryProduct("prod_2", "nursery_1", "Chilli", "Vegetable", "Teja 4",
            pricePerSeedling = 1.10, pricePerTray = 110.0, seedlingsPerTray = 100,
            minimumOrder = 500, bulkPrice = 0.95, availableQuantity = 12000,
            expectedAvailability = "20 Nov 2026", availability = ProductAvailability.PRE_ORDER)
    ))
    val ownerProducts: StateFlow<List<NurseryProduct>> = _ownerProducts.asStateFlow()
    private val _inventory = MutableStateFlow(listOf(
        InventoryItem("prod_1", "nursery_1", "Tomato", "Abhinav Hybrid F1", 15000, 1500, minimumStock = 1000),
        InventoryItem("prod_2", "nursery_1", "Chilli", "Teja 4", 12000, 0, minimumStock = 1000)
    ))
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()
    private val _batches = MutableStateFlow(listOf(
        ProductionBatch("batch_1", "nursery_1", "Tomato", "Abhinav Hybrid F1", 4500,
            "15 Nov 2026", status = ProductionStatus.SEEDLING_GROWTH, seedSource = "Certified seed")
    ))
    val productionBatches: StateFlow<List<ProductionBatch>> = _batches.asStateFlow()
    private val _payments = MutableStateFlow(emptyList<NurseryPayment>())
    val payments: StateFlow<List<NurseryPayment>> = _payments.asStateFlow()
    private val _offers = MutableStateFlow(emptyList<NurseryOffer>())
    val offers: StateFlow<List<NurseryOffer>> = _offers.asStateFlow()
    private val _reviews = MutableStateFlow(listOf(
        NurseryReview("review_1", "nursery_1", "Suresh Patil", 5, "Healthy, uniform seedlings and timely pickup.")
    ))
    val reviews: StateFlow<List<NurseryReview>> = _reviews.asStateFlow()
    private val _notifications = MutableStateFlow(listOf(
        NurseryNotification("note_1", "nursery_1", "New pre-order received", "Tomato · 1,500 seedlings requested by Suresh Patil")
    ))
    val notifications: StateFlow<List<NurseryNotification>> = _notifications.asStateFlow()

    private var firebaseOrderListener: com.google.firebase.firestore.ListenerRegistration? = null
    private var firebaseNotificationListener: com.google.firebase.firestore.ListenerRegistration? = null

    fun connectRealtimeData(user: UserProfile) {
        firebaseOrderListener?.remove()
        firebaseNotificationListener?.remove()
        if (!FirebaseManager.isConfigured()) return

        firebaseOrderListener = FirebaseManager.observeOrders(user) { remoteOrders ->
            _preOrders.value = remoteOrders
        }
        firebaseNotificationListener = FirebaseManager.observeNotifications(user) { remoteNotifications ->
            _notifications.value = remoteNotifications
        }
    }

    fun addNursery(nursery: Nursery) {
        val ownedNursery = nursery.copy(
            ownerEmail = nursery.ownerEmail.ifBlank { _currentUser.value.email }
        )
        _ownerNurseries.value = listOf(ownedNursery) + _ownerNurseries.value
        persist("nurseries", ownedNursery.id, ownedNursery)
    }
    fun addProduct(product: NurseryProduct) {
        _ownerProducts.value = listOf(product) + _ownerProducts.value
        _inventory.value = listOf(InventoryItem(product.id, product.nurseryId, product.name, product.variety, product.availableQuantity)) + _inventory.value
        persist("products", product.id, product)
    }
    fun updateProduct(product: NurseryProduct) {
        _ownerProducts.value = _ownerProducts.value.map { if (it.id == product.id) product else it }
        _inventory.value = _inventory.value.map { if (it.productId == product.id) it.copy(currentStock = product.availableQuantity) else it }
        persist("products", product.id, product)
    }
    fun updateOwnerOrder(orderId: String, status: OrderStatus): Boolean {
        val order = _preOrders.value.firstOrNull { it.id == orderId } ?: return false
        val allowed = when (order.status) {
            OrderStatus.REQUESTED -> setOf(OrderStatus.ACCEPTED, OrderStatus.CANCELLED)
            OrderStatus.ACCEPTED -> setOf(OrderStatus.PRODUCTION_PLANNED, OrderStatus.CANCELLED)
            OrderStatus.PRODUCTION_PLANNED -> setOf(OrderStatus.GROWING, OrderStatus.CANCELLED)
            OrderStatus.GROWING -> setOf(OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED)
            OrderStatus.READY_FOR_PICKUP -> setOf(OrderStatus.COMPLETED, OrderStatus.CANCELLED)
            else -> emptySet()
        }
        if (status !in allowed) return false
        if (status == OrderStatus.COMPLETED && order.status == OrderStatus.COMPLETED) return false
        _preOrders.value = _preOrders.value.map { if (it.id == orderId) it.copy(status = status) else it }
        if (status == OrderStatus.ACCEPTED) reserveStock(order)
        if (status == OrderStatus.COMPLETED) {
            val item = _inventory.value.firstOrNull { it.productName == order.cropName && it.nurseryId == order.nurseryId }
            if (item != null) adjustStock(item.productId, -order.quantity, "FULFILLED", orderId)
        }
        val updated = _preOrders.value.firstOrNull { it.id == orderId } ?: order.copy(status = status)
        persist("pre_orders", orderId, updated)
        createNotification(
            targetUserId = order.farmerId,
            title = "Order ${status.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}",
            message = "${order.cropName} order ${order.id} is now ${status.name.replace('_', ' ')}."
        )
        return true
    }
    fun reserveStock(order: PreOrder) {
        _inventory.value = _inventory.value.map { item ->
            if (item.productName == order.cropName && item.nurseryId == order.nurseryId)
                item.copy(reservedStock = (item.reservedStock + order.quantity).coerceAtMost(item.currentStock))
            else item
        }
    }
    fun adjustStock(productId: String, delta: Int, type: String = "ADJUSTMENT", note: String = ""): Boolean {
        val item = _inventory.value.firstOrNull { it.productId == productId } ?: return false
        if (item.currentStock + delta < item.reservedStock || item.currentStock + delta < 0) return false
        _inventory.value = _inventory.value.map { if (it.productId == productId) it.copy(currentStock = it.currentStock + delta, updatedAt = System.currentTimeMillis()) else it }
        persist("inventory_movements", UUID.randomUUID().toString(), InventoryMovement(UUID.randomUUID().toString(), productId, item.nurseryId, delta, type, note))
        return true
    }
    fun addBatch(batch: ProductionBatch) { _batches.value = listOf(batch) + _batches.value; persist("production_batches", batch.id, batch) }
    fun updateBatch(batch: ProductionBatch) { _batches.value = _batches.value.map { if (it.id == batch.id) batch else it }; persist("production_batches", batch.id, batch) }
    fun addPayment(payment: NurseryPayment) { _payments.value = listOf(payment) + _payments.value; persist("payments", payment.id, payment) }
    fun addOffer(offer: NurseryOffer) { _offers.value = listOf(offer) + _offers.value; persist("offers", offer.id, offer) }
    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(read = true) else it }
        FirebaseManager.getFirestore()?.collection("notifications")?.document(id)?.update("read", true)
    }
    fun replyToReview(id: String, reply: String) { _reviews.value = _reviews.value.map { if (it.id == id) it.copy(reply = reply) else it } }

    private fun persist(collection: String, id: String, value: Any) {
        FirebaseManager.getFirestore()?.collection(collection)?.document(id)?.set(value)
    }

    private fun createNotification(targetUserId: String, title: String, message: String) {
        val id = UUID.randomUUID().toString()
        val notification = NurseryNotification(
            id = id,
            title = title,
            message = message,
            targetUserId = targetUserId
        )
        _notifications.value = listOf(notification) + _notifications.value
        persist("notifications", id, notification)
    }

    // Real-time weather info
    val weather = MutableStateFlow(
        WeatherInfo(
            location = "Pune, Maharashtra",
            temperatureC = 28,
            condition = "Partly Cloudy",
            humidityPercent = 68,
            windKmh = 12,
            rainChancePercent = 20,
            warning = "Favorable transplanting conditions for next 3 days."
        )
    )

    // Disease outbreak alert in vicinity
    val diseaseAlert = MutableStateFlow(
        DiseaseAlert(
            id = "alert_101",
            cropName = "Tomato",
            diseaseName = "Tomato Leaf Blight",
            riskLevel = "High Risk",
            distanceKm = 4.8,
            detectionRadius = "within 5 km",
            dateReported = "Today, 08:30 AM",
            description = "Leaf blight spots identified in 3 neighboring farms in Haveli cluster.",
            safeMeasures = "Apply preventive Mancozeb / Copper Oxychloride spray in early morning."
        )
    )

    // Community posts
    val communityPosts = MutableStateFlow(
        listOf(
            CommunityPost(
                id = "p1",
                authorName = "Pandurang Jadhav",
                cropName = "Chilli",
                question = "Leaf curling noticed after recent drizzle in Shirur taluka. Are thrips or mites the main cause?",
                timeAgo = "2 hours ago",
                repliesCount = 6,
                helpfulCount = 14
            ),
            CommunityPost(
                id = "p2",
                authorName = "Dr. Aniket More (Agri Consultant)",
                cropName = "Tomato",
                question = "Guide for pro-tray seedling hardening before main field transplanting in November.",
                timeAgo = "5 hours ago",
                repliesCount = 12,
                helpfulCount = 38
            )
        )
    )

    fun submitPreOrder(
        cropName: String,
        variety: String,
        nurseryId: String,
        nurseryName: String,
        quantity: Int,
        requiredDate: String,
        pricePerPlant: Double,
        pickupPreference: String,
        notes: String
    ): PreOrder {
        val newOrder = PreOrder(
            id = "ORD-" + UUID.randomUUID().toString().take(6).uppercase(),
            farmerId = _currentUser.value.uid,
            farmerName = _currentUser.value.name,
            farmerPhone = _currentUser.value.phone,
            nurseryId = nurseryId,
            nurseryName = nurseryName,
            nurseryOwnerEmail = _nurseries.value.firstOrNull { it.id == nurseryId }?.ownerEmail.orEmpty(),
            cropName = cropName,
            variety = variety,
            quantity = quantity,
            requiredDate = requiredDate,
            pricePerPlant = pricePerPlant,
            totalAmount = quantity * pricePerPlant,
            status = OrderStatus.REQUESTED,
            pickupPreference = pickupPreference,
            notes = notes,
            createdAt = System.currentTimeMillis()
        )

        val updated = ArrayList(_preOrders.value)
        updated.add(0, newOrder)
        _preOrders.value = updated

        persist("pre_orders", newOrder.id, newOrder)
        if (newOrder.nurseryOwnerEmail.isNotBlank()) {
            createNotification(
                targetUserId = "owner:${newOrder.nurseryOwnerEmail}",
                title = "New order received",
                message = "${newOrder.farmerName} requested ${newOrder.quantity} ${newOrder.cropName} seedlings."
            )
        }
        return newOrder
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val updated = _preOrders.value.map {
            if (it.id == orderId) it.copy(status = newStatus) else it
        }
        _preOrders.value = updated

        val db = FirebaseManager.getFirestore()
        if (db != null) {
            try {
                db.collection("pre_orders").document(orderId).update("status", newStatus.name)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun login(emailOrPhone: String, role: UserRole, name: String) {
        _currentUser.value = _currentUser.value.copy(
            email = emailOrPhone,
            role = role,
            name = if (name.isNotBlank()) name else _currentUser.value.name
        )
    }

    fun setAuthenticatedUser(profile: UserProfile) {
        _currentUser.value = profile
    }

    fun updateLanguage(lang: String) {
        _currentUser.value = _currentUser.value.copy(preferredLanguage = lang)
    }

    fun updateFarmProfile(farmSize: Double, soilType: String, waterSource: String) {
        _currentUser.value = _currentUser.value.copy(
            farmSizeAcres = farmSize,
            soilType = soilType,
            waterSource = waterSource
        )
    }
}
