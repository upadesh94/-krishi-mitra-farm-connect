# Krishi Mitra AI — Firebase Integration Guide

This application is built with **Jetpack Compose (Material 3)** and is pre-configured to use **Firebase Authentication** and **Cloud Firestore**.

---

## 1. Connecting your Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project or select an existing project named **KrishiMitra**.
3. Add an **Android app** to your project:
   - **Android package name:** `com.example.krishimitra`
   - **App nickname:** `Krishi Mitra`
4. Download the `google-services.json` file.
5. Place `google-services.json` into your `app/` folder:
   ```
   KrishiMitra/
   └── app/
       └── google-services.json  <-- Place here (git-ignored for security)
   ```
> [!IMPORTANT]
> `google-services.json` is git-ignored by default to prevent leaking project API keys and secrets to public version control. See `app/google-services.json.example` for reference.

6. Rebuild the project in Android Studio (or run `./gradlew assembleDebug`). The build script automatically detects `google-services.json` when present and activates the Google Services plugin! If absent, the app builds and runs in offline demo mode seamlessly without errors.

---

## 2. Firebase Features Enabled

- **Firebase Authentication:**
  - Phone Auth & Email/Password for Farmers and Nursery Owners.
  - Role-based profile management (Farmer vs. Nursery Owner).

- **Cloud Firestore Collections:**
  - `pre_orders`: Real-time seedling reservation orders submitted by farmers.
  - `nurseries`: Nursery owner profile, location, rating, contact, and catalog.
  - `seedlings`: Variety listings, price per plant, minimum quantity, ready date.
  - `disease_reports`: Community crop health observations with location cluster alerts.

- **Offline / Graceful Fallback Mode:**
  - If `google-services.json` is not yet attached, Krishi Mitra runs in offline mode with authentic Maharashtra agricultural seed data (Pune, Haveli, Tomato Hybrid F1, Chilli, Brinjal, Cabbage, and real nurseries: Green Valley Nursery, Shree Sai Agro, MahaPlant).
  - Pre-orders and status changes update locally in memory so you can immediately test and demo the entire user journey.
