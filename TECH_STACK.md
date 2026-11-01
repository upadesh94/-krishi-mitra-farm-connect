# KrishiMitra (कृषिमित्र) — Technology Stack & Architecture

> **KrishiMitra** is a modern, offline-first Android application designed to bridge Indian farmers with nearby plant nurseries. It enables pre-ordering disease-hardened seedlings in pro-trays, AI-driven crop disease diagnostics, regional weather forecasts, and full nursery production management.

---

## 1. High-Level Architecture Overview

KrishiMitra follows a modern **Single-Activity**, **100% Jetpack Compose** architecture powered by the **Repository Pattern** and reactive **Kotlin Coroutines / StateFlow**.

```
┌──────────────────────────────────────────────────────────────┐
│                     Jetpack Compose UI                       │
│  ┌─────────────────────────────┐ ┌────────────────────────┐  │
│  │     Farmer Interface        │ │  Nursery Owner Portal  │  │
│  │ (Home, Market, Scan, Forum) │ │(Overview, Orders, Stock│  │
│  └─────────────────────────────┘ └────────────────────────┘  │
└───────────────────────────────┬──────────────────────────────┘
                                │ State / Intent
                                ▼
┌──────────────────────────────────────────────────────────────┐
│            Data Layer (Reactive Repository Pattern)           │
│   • KrishiRepository (StateFlow streams, In-memory cache)     │
│   • Data Models (SeedlingListing, Nursery, PreOrder, etc.)   │
└───────────────────────────────┬──────────────────────────────┘
                                │ Sync / Auth
                                ▼
┌──────────────────────────────────────────────────────────────┐
│             Backend & System Device Services                 │
│   • Firebase Cloud Firestore (Real-time NoSQL database)       │
│   • Firebase Authentication (Email/Password & Role-based)    │
│   • Google Play Services Location (FusedLocationProvider)    │
│   • Android System Intents (Phone dialer, Google Maps geo)   │
└──────────────────────────────────────────────────────────────┘
```

---

## 2. Core Platform & Build Environment

| Technology | Specification / Version | Description |
|---|---|---|
| **Platform** | Native Android | Built natively for Android devices |
| **Language** | Kotlin `2.0.21` | Modern, concise, null-safe language |
| **Kotlin Compiler Plugin** | `org.jetbrains.kotlin.plugin.compose` | Official Kotlin 2.0 Compose compiler |
| **Android Gradle Plugin (AGP)** | `9.3.0-alpha11` | Build system and packaging pipeline |
| **Compile SDK** | `35` (Android 15) | Targets latest Android platform APIs |
| **Target SDK** | `35` (Android 15) | Optimized for modern runtime behaviors |
| **Min SDK** | `24` (Android 7.0 Nougat) | Supports ~95%+ of active Android devices |
| **Java Virtual Machine (JVM)** | JDK 17 | Language level and bytecode target |
| **Dependency Catalog** | Gradle Version Catalog (`libs.versions.toml`) | Centralized version and dependency management |

---

## 3. UI Framework & Design System

KrishiMitra features a custom agricultural design language built entirely with declarative UI:

- **Jetpack Compose (Compose BOM `2024.10.01`)**:
  - `androidx.compose.ui:ui`: Declarative rendering engine.
  - `androidx.compose.ui:ui-graphics`: Gradients, canvas, and vector styling.
  - `androidx.compose.foundation:foundation`: Gestures, scrolls, lazy layouts (`LazyColumn`, `LazyRow`).
  - `androidx.compose.animation:animation`: `AnimatedVisibility`, `fadeIn`, `slideInVertically` animations.
- **Material Design 3 (`androidx.compose.material3:material3`)**:
  - Material 3 theme with dynamic color tokens.
  - Component library: `Card`, `FilterChip`, `TabRow`, `NavigationBar`, `TopAppBar`, `OutlinedTextField`, `Button`, `Dialog`.
- **Iconography**:
  - `androidx.compose.material:material-icons-extended`: Extended Google Material Icons (Agriculture, Spa, Storefront, Location, Camera, etc.).
- **Theme Palette**:
  - **Primary Greens**: Agricultural Deep Green (`#15803D`), Forest Green (`#166534`), Fresh Leaf Accent (`#22C55E`), Mint Surface (`#F0FDF4`).
  - **Secondary Accents**: Harvest Gold (`#EAB308`), Sun Orange (`#F97316`), Sky Blue (`#38BDF8`), Alert Red (`#EF4444`).
  - **Neutrals**: Crisp Slate Heading (`#0F172A`), Off-white Background (`#F8FAFC`), Light Gray Dividers (`#E2E8F0`).

---

## 4. State Management & Concurrency

- **Kotlin Coroutines**: Non-blocking asynchronous operations for network calls, Firebase queries, and scanner simulations.
- **Kotlin StateFlow & SharedFlow**: Reactive state emission consumed by Compose via `collectAsState()`.
- **Unidirectional Data Flow (UDF)**:
  - UI emits user intents (`submitPreOrder`, `updateOwnerOrder`, `adjustStock`).
  - `KrishiRepository` updates the central `MutableStateFlow` streams.
  - UI automatically re-composes to reflect state changes.
- **Compose Lifecycle**:
  - `androidx.lifecycle:lifecycle-runtime-compose:2.8.7`
  - `androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7`
  - `androidx.activity:activity-compose:1.9.3`

---

## 5. Backend & Cloud Infrastructure

The app is engineered with an **offline-first hybrid architecture**: it runs smoothly in offline/demo mode out of the box, and seamlessly synchronizes live cloud data when connected to Firebase.

- **Google Firebase BOM (`33.7.0`)**:
  - **Firebase Authentication (`firebase-auth-ktx`)**:
    - Secure Email & Password authentication.
    - Role-based profile provisioning (`UserRole.FARMER` vs `UserRole.NURSERY_OWNER`).
    - Offline fallback with instant demo mode.
  - **Cloud Firestore (`firebase-firestore-ktx`)**:
    - Real-time NoSQL document database.
    - Collections: `pre_orders`, `nurseries`, `products`, `production_batches`, `inventory_movements`, `notifications`, `reviews`.
    - Live snapshot listeners (`SnapshotListener`) for real-time synchronization between farmers and nurseries.
  - **Firebase Storage (`firebase-storage-ktx`)**:
    - Cloud bucket storage for uploaded leaf disease photos.
- **Google Services Gradle Plugin (`4.4.2`)**:
  - Integrates `google-services.json` configuration into the build.

---

## 6. Device Hardware & Geolocation Integration

- **Google Play Services Location (`21.3.0`)**:
  - `FusedLocationProviderClient`: Battery-efficient balanced-accuracy live geolocation.
  - Haversine distance algorithm calculating exact kilometers between farmer location and nurseries.
- **Android Platform System Intents**:
  - **Telephony Intent (`Intent.ACTION_DIAL`)**: One-tap calling to nursery owners and farmers (`tel:+91...`).
  - **Geo Navigation Intent (`Intent.ACTION_VIEW`)**: Native launching of Google Maps with precise coordinates (`geo:lat,lng?q=...`) for farm visits.

---

## 7. Image Loading & Asset Pipeline

- **Coil Compose (`io.coil-kt:coil-compose:2.7.0`)**:
  - Lightweight, coroutine-based image loading library for Android.
  - Asynchronous remote image downloading, caching, and placeholder fallback.
- **Photorealistic Agricultural Image Assets**:
  - Dedicated pro-tray seedling photography in `app/src/main/res/drawable/`:
    - `seedling_tomato.jpg` (Tomato Abhinav F1 seedling in plug cell)
    - `seedling_chilli.jpg` (Chilli Teja 4 seedling in plug cell)
    - `seedling_brinjal.jpg` (Eggplant / Brinjal seedling with broad leaves)
    - `seedling_cabbage.jpg` (Cabbage plug tray seedling)
    - `seedling_cauliflower.jpg` (Cauliflower plug tray seedling)
    - `seedling_capsicum.jpg` (Bell pepper seedling in pro-tray cell)
    - `nursery_seedlings.jpg` (Commercial greenhouse polyhouse rows)
    - `seedling_preorder.jpg` (Pro-tray seedling nursery cultivation)
    - `tomato_leaf_blight.jpg` (Real leaf sample with early blight lesions)
    - `farmer_field_sunrise.jpg` (Full-bleed agricultural login hero)

---

## 8. Directory & Package Structure

```
KrishiMitra/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/krishimitra/
│   │   │   │   ├── MainActivity.kt                     # Application entry & role routing
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── Models.kt                   # Domain entities & extension properties
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── FirebaseManager.kt          # Cloud Firestore & Auth connector
│   │   │   │   │       └── KrishiRepository.kt         # Reactive data streams & business logic
│   │   │   │   └── ui/
│   │   │   │       ├── components/                     # Reusable Compose components
│   │   │   │       │   ├── ActionGrid.kt               # 6-Tile quick action grid
│   │   │   │       │   ├── BottomNavigation.kt         # Farmer 5-tab navigation bar
│   │   │   │       │   ├── GreetingSection.kt          # Farmer greeting & search bar
│   │   │   │       │   ├── NearbyNurseriesRow.kt       # Horizontal nursery carousel
│   │   │   │       │   ├── PromoBanners.kt             # Pre-order banner & AI crop card
│   │   │   │       │   ├── TopBar.kt                   # Location header & notifications
│   │   │   │       │   └── WeatherAndDiseaseRow.kt     # Live weather card & disease alert
│   │   │   │       ├── screens/                        # Full-page screens & modal dialogs
│   │   │   │       │   ├── CommunityScreen.kt          # Farmer discussion forum
│   │   │   │       │   ├── DiseaseScanScreen.kt        # AI plant leaf disease detector
│   │   │   │       │   ├── HomeScreen.kt               # Farmer primary dashboard
│   │   │   │       │   ├── LoginScreen.kt              # Styled authentication with role toggle
│   │   │   │       │   ├── MarketplaceScreen.kt        # Seedlings & Nearby Nurseries catalog
│   │   │   │       │   ├── NurseryDetailDialog.kt      # Detailed nursery profile & plant list
│   │   │   │       │   ├── NurseryOwnerDashboard.kt    # Nursery Owner management portal
│   │   │   │       │   ├── PreOrderDialog.kt           # Farmer seedling pre-order dialog
│   │   │   │       │   └── ProfileScreen.kt            # Land details, language & Firebase status
│   │   │   │       └── theme/                          # Design tokens & styling
│   │   │   │           ├── Color.kt                    # Brand color definitions
│   │   │   │           ├── Theme.kt                    # Light / Dark Material 3 theme
│   │   │   │           └── Type.kt                     # Typography definitions
│   │   │   └── res/
│   │   │       └── drawable/                           # High-res photorealistic plant images
│   │   └── build.gradle.kts                            # App module build configuration
├── gradle/
│   └── libs.versions.toml                              # Version catalog
├── build.gradle.kts                                    # Root build configuration
└── TECH_STACK.md                                       # Tech stack documentation
```

---

## 9. Testing & Quality Assurance

- **Unit Testing**: JUnit 4 (`junit:junit:4.13.2`)
- **Android Instrumentation Testing**:
  - `androidx.test.ext:junit:1.2.1`
  - `androidx.test.espresso:espresso-core:3.6.1`
- **Compose Tooling**:
  - `androidx.compose.ui:ui-tooling` (Debug inspector)
  - `androidx.compose.ui:ui-tooling-preview` (Android Studio composable previews)

---

## 10. Summary Table of Key Dependencies

| Dependency | Group / Artifact | Version | Purpose |
|---|---|---|---|
| **Compose BOM** | `androidx.compose:compose-bom` | `2024.10.01` | Bill of Materials for Compose libraries |
| **Material 3** | `androidx.compose.material3:material3` | BOM | Modern Material Design components |
| **Compose Navigation** | `androidx.navigation:navigation-compose` | `2.8.5` | In-app screen navigation |
| **Coil Image Loader** | `io.coil-kt:coil-compose` | `2.7.0` | Asynchronous image loading & caching |
| **Firebase BOM** | `com.google.firebase:firebase-bom` | `33.7.0` | Cloud backend services |
| **Firebase Auth** | `com.google.firebase:firebase-auth-ktx` | BOM | User authentication & role management |
| **Firebase Firestore**| `com.google.firebase:firebase-firestore-ktx` | BOM | Real-time cloud database |
| **Google Play Location**| `com.google.android.gms:play-services-location` | `21.3.0` | Fused location tracking & distance |
| **Kotlin Coroutines** | Built-in with Kotlin / AndroidX | `2.0.21` | Reactive streams & concurrency |

