package com.example.krishimitra.data.model

import com.example.krishimitra.R

enum class UserRole {
    FARMER,
    NURSERY_OWNER,
    ADMIN
}

val UserRole.displayName: String
    get() = when (this) {
        UserRole.FARMER -> "Farmer"
        UserRole.NURSERY_OWNER -> "Nursery Owner"
        UserRole.ADMIN -> "Admin"
    }

data class UserProfile(
    val uid: String = "",
    val name: String = "Suresh Patil",
    val phone: String = "+91 98765 43210",
    val email: String = "suresh.patil@krishimitra.in",
    val role: UserRole = UserRole.FARMER,
    val location: String = "Pune, Maharashtra",
    val taluka: String = "Haveli",
    val district: String = "Pune",
    val farmSizeAcres: Double = 3.5,
    val soilType: String = "Medium Black Soil",
    val waterSource: String = "Borewell & Drip Irrigation",
    val preferredLanguage: String = "English" // English, मराठी, हिंदी
)

data class Nursery(
    val id: String = "",
    val name: String = "",
    val ownerName: String = "",
    val locationName: String = "Pune, Maharashtra",
    val distanceKm: Double = 2.0,
    val rating: Double = 4.5,
    val reviewCount: Int = 120,
    val cropsAvailable: List<String> = emptyList(),
    val imageUrl: String = "",
    val phone: String = "+91 98220 12345",
    val address: String = "Near Highway, Pune District",
    val isVerified: Boolean = true,
    val latitude: Double = 18.5204,
    val longitude: Double = 73.8567,
    val ownerEmail: String = ""
)

val Nursery.imageResId: Int
    get() = when (id) {
        "nursery_1" -> R.drawable.nursery_seedlings
        "nursery_2" -> R.drawable.seedling_preorder
        "nursery_3" -> R.drawable.seedling_tomato
        else -> R.drawable.nursery_seedlings
    }

data class SeedlingListing(
    val id: String = "",
    val nurseryId: String = "",
    val nurseryName: String = "",
    val cropName: String = "",       // e.g. Tomato, Chilli, Brinjal, Cabbage
    val variety: String = "",        // e.g. Abhinav Hybrid, Teja 4, Manjari Gota
    val pricePerPlant: Double = 1.20,
    val minOrderQuantity: Int = 500,
    val availableQuantity: Int = 10000,
    val expectedReadyDate: String = "15 Nov 2026",
    val growthStage: String = "18-Day Plug Tray",
    val imageUrl: String = "",
    val isPreOrderOpen: Boolean = true,
    val careNotes: String = "Disease-hardened seedling raised in pro-trays with cocopeat."
)

fun getSeedlingImageRes(cropName: String): Int {
    return when (cropName.lowercase().trim()) {
        "tomato" -> R.drawable.seedling_tomato
        "chilli" -> R.drawable.seedling_chilli
        "brinjal" -> R.drawable.seedling_brinjal
        "cabbage" -> R.drawable.seedling_cabbage
        "cauliflower" -> R.drawable.seedling_cauliflower
        "capsicum" -> R.drawable.seedling_capsicum
        else -> R.drawable.seedling_tomato
    }
}

val SeedlingListing.imageResId: Int
    get() = getSeedlingImageRes(cropName)

enum class OrderStatus {
    DRAFT,
    REQUESTED,
    ACCEPTED,
    PRODUCTION_PLANNED,
    GROWING,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED
}

data class PreOrder(
    val id: String = "",
    val farmerId: String = "",
    val farmerName: String = "",
    val farmerPhone: String = "",
    val nurseryId: String = "",
    val nurseryName: String = "",
    val nurseryOwnerEmail: String = "",
    val cropName: String = "",
    val variety: String = "",
    val quantity: Int = 1000,
    val requiredDate: String = "20 Nov 2026",
    val pricePerPlant: Double = 1.20,
    val totalAmount: Double = 1200.0,
    val status: OrderStatus = OrderStatus.REQUESTED,
    val pickupPreference: String = "Self Pickup at Nursery",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

val PreOrder.imageResId: Int
    get() = getSeedlingImageRes(cropName)

data class WeatherInfo(
    val location: String = "Pune, Maharashtra",
    val temperatureC: Int = 28,
    val condition: String = "Partly Cloudy",
    val humidityPercent: Int = 68,
    val windKmh: Int = 12,
    val rainChancePercent: Int = 20,
    val warning: String? = "Optimal weather for seedling transplanting in next 3 days."
)

data class DiseaseAlert(
    val id: String = "",
    val cropName: String = "Tomato",
    val diseaseName: String = "Tomato Leaf Blight",
    val riskLevel: String = "High Risk",
    val distanceKm: Double = 4.8,
    val detectionRadius: String = "within 5 km",
    val dateReported: String = "Today",
    val description: String = "Detected on early season hybrids. Check underside of leaves for dark water-soaked concentric lesions.",
    val safeMeasures: String = "Ensure adequate spacing, avoid overhead watering, and spray copper hydroxide if needed."
)

data class CommunityPost(
    val id: String = "",
    val authorName: String = "",
    val cropName: String = "",
    val question: String = "",
    val timeAgo: String = "",
    val repliesCount: Int = 0,
    val helpfulCount: Int = 0
)

enum class NurseryApprovalStatus { PENDING, APPROVED, REJECTED, SUSPENDED }
enum class ProductAvailability { AVAILABLE, PRE_ORDER, IN_PRODUCTION, UNAVAILABLE, DISCONTINUED }
enum class ProductionStatus { PLANNED, SEEDS_PREPARED, SOWING_COMPLETED, GERMINATION, SEEDLING_GROWTH, READY, COMPLETED, CANCELLED }
enum class PaymentStatus { PENDING, ADVANCE_PAID, PARTIALLY_PAID, PAID, REFUNDED, FAILED }

data class NurseryOwnerProfile(
    val ownerId: String = "",
    val mobile: String = "",
    val verification: NurseryApprovalStatus = NurseryApprovalStatus.PENDING,
    val businessRegistration: String = "",
    val description: String = ""
)

data class NurseryProduct(
    val id: String = "",
    val nurseryId: String = "",
    val name: String = "",
    val category: String = "",
    val variety: String = "",
    val description: String = "",
    val scientificName: String = "",
    val season: String = "",
    val seedlingAgeDays: Int = 0,
    val pricePerSeedling: Double = 0.0,
    val pricePerTray: Double = 0.0,
    val seedlingsPerTray: Int = 0,
    val minimumOrder: Int = 1,
    val bulkPrice: Double = 0.0,
    val availableQuantity: Int = 0,
    val expectedAvailability: String = "",
    val availability: ProductAvailability = ProductAvailability.AVAILABLE,
    val active: Boolean = true
)

val NurseryProduct.imageResId: Int
    get() = getSeedlingImageRes(name)

data class InventoryItem(
    val productId: String = "",
    val nurseryId: String = "",
    val productName: String = "",
    val variety: String = "",
    val currentStock: Int = 0,
    val reservedStock: Int = 0,
    val damagedStock: Int = 0,
    val expiredStock: Int = 0,
    val minimumStock: Int = 100,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val availableStock: Int get() = (currentStock - reservedStock).coerceAtLeast(0)
}

data class InventoryMovement(
    val id: String = "",
    val productId: String = "",
    val nurseryId: String = "",
    val quantity: Int = 0,
    val type: String = "ADJUSTMENT",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ProductionBatch(
    val id: String = "",
    val nurseryId: String = "",
    val cropName: String = "",
    val variety: String = "",
    val plannedQuantity: Int = 0,
    val expectedCompletion: String = "",
    val actualQuantity: Int = 0,
    val seedSource: String = "",
    val relatedPreOrders: List<String> = emptyList(),
    val status: ProductionStatus = ProductionStatus.PLANNED,
    val notes: String = ""
)

data class NurseryPayment(
    val id: String = "",
    val nurseryId: String = "",
    val orderId: String = "",
    val farmerName: String = "",
    val amount: Double = 0.0,
    val advance: Double = 0.0,
    val method: String = "Cash",
    val status: PaymentStatus = PaymentStatus.PENDING,
    val reference: String = ""
)

data class NurseryOffer(
    val id: String = "",
    val nurseryId: String = "",
    val title: String = "",
    val crop: String = "",
    val minimumQuantity: Int = 0,
    val discountPercent: Int = 0,
    val validUntil: String = "",
    val active: Boolean = true
)

data class NurseryReview(
    val id: String = "",
    val nurseryId: String = "",
    val farmerName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val reply: String = ""
)

data class NurseryNotification(
    val id: String = "",
    val nurseryId: String = "",
    val title: String = "",
    val message: String = "",
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val targetUserId: String = ""
)
