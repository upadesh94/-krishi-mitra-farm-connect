# ==============================================================================
# KrishiMitra Security & R8 / ProGuard Optimization Rules
# ==============================================================================

# 1. Strip debug and verbose logs from release builds (prevents sensitive data leaking to logcat)
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# 2. Preserve Data Models and Firestore DTOs (prevents reflection-based serialization failures)
-keepclassmembers class com.example.krishimitra.data.model.** {
    <fields>;
    <methods>;
}
-keep class com.example.krishimitra.data.model.** { *; }

# 3. Kotlin Coroutines & StateFlow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# 4. Coil Image Loader
-keepclassmembers class io.coilkt.** { *; }

# 5. Firebase Authentication and Firestore SDKs
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
