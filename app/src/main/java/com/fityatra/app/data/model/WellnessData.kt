package com.fityatra.app.data.model

data class WellnessData(
    val hrv: Float? = null,              // Heart rate variability in ms
    val restingHeartRate: Float? = null, // Resting heart rate in bpm
    val vo2Max: Float? = null,           // VO2 max in ml/kg/min
    val sleepEfficiency: Float? = null,  // Sleep efficiency 0–100 %
    // true only when at least one metric was successfully read
    val isAvailable: Boolean = false
)
