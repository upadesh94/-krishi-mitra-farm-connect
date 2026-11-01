package com.example.krishimitra.data.repository

import android.content.Context
import android.util.Log
import com.example.krishimitra.data.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    const val DEMO_PASSWORD = "112233"
    private var isFirebaseAvailable = false

    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                auth = FirebaseAuth.getInstance()
                firestore = FirebaseFirestore.getInstance()
                isFirebaseAvailable = true
                Log.d(TAG, "Firebase initialized successfully.")
            } else {
                Log.w(TAG, "FirebaseApp is not configured. Running in offline/mock demo mode.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase initialization skipped or failed: ${e.message}")
            isFirebaseAvailable = false
        }
    }

    fun isConfigured(): Boolean = isFirebaseAvailable

    fun getAuth(): FirebaseAuth? = auth
    fun getFirestore(): FirebaseFirestore? = firestore

    fun observeOrders(
        user: UserProfile,
        onChange: (List<PreOrder>) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration? {
        val database = firestore ?: return null
        val query = if (user.role == UserRole.NURSERY_OWNER) {
            database.collection("pre_orders").whereEqualTo("nurseryOwnerEmail", user.email)
        } else {
            database.collection("pre_orders").whereEqualTo("farmerId", user.uid)
        }
        return query.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                Log.e(TAG, "Realtime order sync failed", error)
                return@addSnapshotListener
            }
            onChange(snapshot.documents.mapNotNull(::readPreOrder))
        }
    }

    fun observeNotifications(
        user: UserProfile,
        onChange: (List<NurseryNotification>) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration? {
        val database = firestore ?: return null
        val target = if (user.role == UserRole.NURSERY_OWNER) "owner:${user.email}" else user.uid
        return database.collection("notifications")
            .whereEqualTo("targetUserId", target)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e(TAG, "Realtime notification sync failed", error)
                    return@addSnapshotListener
                }
                onChange(
                    snapshot.documents
                        .mapNotNull(::readNotification)
                        .sortedByDescending { it.createdAt }
                )
            }
    }

    private fun readPreOrder(document: com.google.firebase.firestore.DocumentSnapshot): PreOrder? {
        val cropName = document.getString("cropName") ?: return null
        val status = document.getString("status")
            ?.let { runCatching { OrderStatus.valueOf(it) }.getOrNull() }
            ?: OrderStatus.REQUESTED
        return PreOrder(
            id = document.id,
            farmerId = document.getString("farmerId").orEmpty(),
            farmerName = document.getString("farmerName").orEmpty(),
            farmerPhone = document.getString("farmerPhone").orEmpty(),
            nurseryId = document.getString("nurseryId").orEmpty(),
            nurseryName = document.getString("nurseryName").orEmpty(),
            nurseryOwnerEmail = document.getString("nurseryOwnerEmail").orEmpty(),
            cropName = cropName,
            variety = document.getString("variety").orEmpty(),
            quantity = (document.getLong("quantity") ?: 0L).toInt(),
            requiredDate = document.getString("requiredDate").orEmpty(),
            pricePerPlant = document.getDouble("pricePerPlant") ?: 0.0,
            totalAmount = document.getDouble("totalAmount") ?: 0.0,
            status = status,
            pickupPreference = document.getString("pickupPreference").orEmpty(),
            notes = document.getString("notes").orEmpty(),
            createdAt = document.getLong("createdAt") ?: 0L
        )
    }

    private fun readNotification(document: com.google.firebase.firestore.DocumentSnapshot): NurseryNotification? {
        return NurseryNotification(
            id = document.id,
            nurseryId = document.getString("nurseryId").orEmpty(),
            title = document.getString("title").orEmpty(),
            message = document.getString("message").orEmpty(),
            read = document.getBoolean("read") ?: false,
            createdAt = document.getLong("createdAt") ?: 0L,
            targetUserId = document.getString("targetUserId").orEmpty()
        )
    }

    suspend fun currentUserProfile(): Result<UserProfile?> {
        val firebaseUser = auth?.currentUser ?: return Result.success(null)
        val database = firestore
            ?: return Result.failure(IllegalStateException("Firebase Firestore is not configured."))
        return try {
            val snapshot = database.collection("users").document(firebaseUser.uid).get().await()
            val role = snapshot.getString("role")
                ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
                ?: return Result.failure(IllegalStateException("Your account profile is incomplete."))
            Result.success(
                UserProfile(
                    uid = firebaseUser.uid,
                    name = snapshot.getString("name").orEmpty().ifBlank { firebaseUser.email.orEmpty() },
                    email = firebaseUser.email.orEmpty(),
                    role = role,
                    phone = snapshot.getString("phone").orEmpty(),
                    location = snapshot.getString("location").orEmpty().ifBlank { "Pune, Maharashtra" },
                    taluka = snapshot.getString("taluka").orEmpty().ifBlank { "Haveli" },
                    district = snapshot.getString("district").orEmpty().ifBlank { "Pune" },
                    farmSizeAcres = snapshot.getDouble("farmSizeAcres") ?: 0.0,
                    soilType = snapshot.getString("soilType").orEmpty().ifBlank { "Not set" },
                    waterSource = snapshot.getString("waterSource").orEmpty().ifBlank { "Not set" },
                    preferredLanguage = snapshot.getString("preferredLanguage").orEmpty().ifBlank { "English" }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
    }

    suspend fun signIn(
        email: String,
        password: String,
        selectedRole: UserRole
    ): Result<UserProfile> {
        val firebaseAuth = auth
            ?: return Result.failure(IllegalStateException("Firebase Authentication is not configured."))
        val database = firestore
            ?: return Result.failure(IllegalStateException("Firebase Firestore is not configured."))

        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("Firebase did not return a user."))
            val snapshot = database.collection("users").document(uid).get().await()
            val storedRole = snapshot.getString("role")
                ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
                ?: return Result.failure(IllegalStateException("Your account profile is incomplete."))

            if (storedRole != selectedRole) {
                firebaseAuth.signOut()
                return Result.failure(IllegalArgumentException("This account is registered as ${storedRole.displayName}."))
            }

            Result.success(
                UserProfile(
                    uid = uid,
                    name = snapshot.getString("name").orEmpty().ifBlank { email.substringBefore("@") },
                    email = authResult.user?.email.orEmpty(),
                    role = storedRole,
                    phone = snapshot.getString("phone").orEmpty(),
                    location = snapshot.getString("location").orEmpty().ifBlank { "Pune, Maharashtra" },
                    taluka = snapshot.getString("taluka").orEmpty().ifBlank { "Haveli" },
                    district = snapshot.getString("district").orEmpty().ifBlank { "Pune" },
                    farmSizeAcres = snapshot.getDouble("farmSizeAcres") ?: 0.0,
                    soilType = snapshot.getString("soilType").orEmpty().ifBlank { "Not set" },
                    waterSource = snapshot.getString("waterSource").orEmpty().ifBlank { "Not set" },
                    preferredLanguage = snapshot.getString("preferredLanguage").orEmpty().ifBlank { "English" }
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Firebase sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun createAccount(
        email: String,
        password: String,
        name: String,
        role: UserRole
    ): Result<UserProfile> {
        val firebaseAuth = auth
            ?: return Result.failure(IllegalStateException("Firebase Authentication is not configured."))
        val database = firestore
            ?: return Result.failure(IllegalStateException("Firebase Firestore is not configured."))

        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("Firebase did not return a user."))
            val profile = UserProfile(
                uid = uid,
                name = name.trim(),
                email = email.trim(),
                role = role
            )
            database.collection("users").document(uid).set(profile.toFirestoreMap()).await()
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Firebase account creation failed", e)
            Result.failure(e)
        }
    }

    suspend fun createDemoAccounts(): Result<Int> {
        val demoAccounts = listOf(
            Triple("ram@gmail.com", "Ram", UserRole.FARMER),
            Triple("yogesh@gmail.com", "Yogesh", UserRole.FARMER),
            Triple("pragatiseed@gmail.com", "Pragati Seed", UserRole.NURSERY_OWNER)
        )
        var created = 0
        demoAccounts.forEach { (email, name, role) ->
            val result = createAccount(email, DEMO_PASSWORD, name, role)
            if (result.isSuccess) created++
            if (firebaseAuthOrNull()?.currentUser != null) firebaseAuthOrNull()?.signOut()
        }
        return if (created > 0) Result.success(created)
        else Result.failure(IllegalStateException("No demo accounts were created. They may already exist."))
    }

    private fun firebaseAuthOrNull(): FirebaseAuth? = auth

    private fun UserProfile.toFirestoreMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "role" to role.name,
        "location" to location,
        "taluka" to taluka,
        "district" to district,
        "farmSizeAcres" to farmSizeAcres,
        "soilType" to soilType,
        "waterSource" to waterSource,
        "preferredLanguage" to preferredLanguage,
        "verificationStatus" to if (role == UserRole.NURSERY_OWNER) NurseryApprovalStatus.PENDING.name else "APPROVED",
        "nurseryIds" to emptyList<String>(),
        "createdAt" to System.currentTimeMillis()
    )

}
