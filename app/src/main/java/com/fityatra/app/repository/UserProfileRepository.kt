package com.fityatra.app.repository

import com.fityatra.app.data.AppPreferences
import com.fityatra.app.data.dao.UserProfileDao
import com.fityatra.app.data.entities.UserProfile
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(
    private val userProfileDao: UserProfileDao,
    private val appPreferences: AppPreferences
) {
    suspend fun getUserProfile(): UserProfile? = userProfileDao.getUserProfile()

    fun getUserProfileFlow(): Flow<UserProfile?> = userProfileDao.getUserProfileFlow()

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile)
        appPreferences.isOnboarded = true
    }

    fun isOnboarded(): Boolean = appPreferences.isOnboarded
}
