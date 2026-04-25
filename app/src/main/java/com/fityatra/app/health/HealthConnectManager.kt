package com.fityatra.app.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.fityatra.app.data.model.WellnessData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit

class HealthConnectManager(private val context: Context) {

    val requiredPermissions = setOf(
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(Vo2MaxRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    fun isAvailable(): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        } catch (e: Exception) {
            false
        }
    }

    private fun getClient(): HealthConnectClient? {
        return try {
            if (isAvailable()) HealthConnectClient.getOrCreate(context) else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun hasPermissions(): Boolean {
        val client = getClient() ?: return false
        return try {
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(requiredPermissions)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun readWellnessData(): WellnessData = withContext(Dispatchers.IO) {
        val client = getClient()
        if (client == null || !hasPermissions()) {
            return@withContext WellnessData(isAvailable = false)
        }

        val now = Instant.now()
        val last24h = now.minus(24, ChronoUnit.HOURS)
        val last48h = now.minus(48, ChronoUnit.HOURS)
        val last30d = now.minus(30, ChronoUnit.DAYS)

        var hrv: Float? = null
        var restingHr: Float? = null
        var vo2Max: Float? = null
        var sleepEfficiency: Float? = null

        try {
            val records = client.readRecords(
                ReadRecordsRequest(
                    HeartRateVariabilityRmssdRecord::class,
                    TimeRangeFilter.between(last24h, now)
                )
            ).records
            hrv = records.lastOrNull()?.heartRateVariabilityMillis?.toFloat()
        } catch (e: Exception) { /* no HRV data */ }

        try {
            val records = client.readRecords(
                ReadRecordsRequest(
                    RestingHeartRateRecord::class,
                    TimeRangeFilter.between(last24h, now)
                )
            ).records
            restingHr = records.lastOrNull()?.beatsPerMinute?.toFloat()
        } catch (e: Exception) { /* no resting HR data */ }

        try {
            val records = client.readRecords(
                ReadRecordsRequest(
                    Vo2MaxRecord::class,
                    TimeRangeFilter.between(last30d, now)
                )
            ).records
            vo2Max = records.lastOrNull()?.vo2MillilitersPerMinuteKilogram?.toFloat()
        } catch (e: Exception) { /* no VO2 max data */ }

        try {
            val records = client.readRecords(
                ReadRecordsRequest(
                    SleepSessionRecord::class,
                    TimeRangeFilter.between(last48h, now)
                )
            ).records
            val lastSleep = records.lastOrNull()
            if (lastSleep != null) {
                val totalMinutes = ChronoUnit.MINUTES.between(
                    lastSleep.startTime, lastSleep.endTime
                ).toFloat()
                // STAGE_TYPE_SLEEPING=2, STAGE_TYPE_LIGHT=4, STAGE_TYPE_DEEP=5, STAGE_TYPE_REM=6
                val asleepStageTypes = setOf(2, 4, 5, 6)
                val asleepMinutes = if (lastSleep.stages.isNotEmpty()) {
                    lastSleep.stages
                        .filter { stage -> stage.stage in asleepStageTypes }
                        .sumOf { stage ->
                            ChronoUnit.MINUTES.between(stage.startTime, stage.endTime)
                        }.toFloat()
                } else {
                    totalMinutes // no stage detail — treat full session as sleep
                }
                if (totalMinutes > 0) {
                    sleepEfficiency = (asleepMinutes / totalMinutes) * 100f
                }
            }
        } catch (e: Exception) { /* no sleep data */ }

        WellnessData(
            hrv = hrv,
            restingHeartRate = restingHr,
            vo2Max = vo2Max,
            sleepEfficiency = sleepEfficiency,
            isAvailable = (hrv != null || restingHr != null || vo2Max != null || sleepEfficiency != null)
        )
    }
}
