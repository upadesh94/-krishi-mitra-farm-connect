package com.example.krishimitra.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Defensive utility for safely launching platform Intents (telephony dialer, map navigation)
 * with input sanitization and ActivityNotFoundException protection.
 */
object SafeIntentLauncher {

    /**
     * Safely dials a phone number after stripping malicious or invalid characters.
     */
    fun dialPhoneNumber(context: Context, rawPhone: String) {
        val sanitized = rawPhone.filter { it.isDigit() || it == '+' }
        if (sanitized.isBlank()) {
            Toast.makeText(context, "Phone number is not available.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$sanitized"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No telephony or phone dialer app found on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Safely opens a geographic coordinate with label in native Maps,
     * with graceful web browser fallback if no dedicated maps app is installed.
     */
    fun openMapLocation(context: Context, latitude: Double, longitude: Double, label: String) {
        val encodedLabel = Uri.encode(label.trim())
        val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($encodedLabel)")
        val webFallbackUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")

        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            // Fallback to web browser
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, webFallbackUri)
                context.startActivity(webIntent)
            } catch (fallbackError: Exception) {
                Toast.makeText(context, "Unable to open maps. No map or browser application available.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
