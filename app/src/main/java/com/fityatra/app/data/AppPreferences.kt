package com.fityatra.app.data

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fityatra_prefs", Context.MODE_PRIVATE)

    var claudeApiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) { prefs.edit().putString(KEY_API_KEY, value).apply() }

    var isOnboarded: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDED, false)
        set(value) { prefs.edit().putBoolean(KEY_ONBOARDED, value).apply() }

    companion object {
        private const val KEY_API_KEY = "claude_api_key"
        private const val KEY_ONBOARDED = "onboarding_complete"
    }
}
