package com.salazar.babytraker.core.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BabyPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("baby_prefs", Context.MODE_PRIVATE)
    
    private val _activeBabyIdFlow = MutableStateFlow(prefs.getLong("active_baby_id", -1L))
    val activeBabyIdFlow: StateFlow<Long> = _activeBabyIdFlow.asStateFlow()

    var activeBabyId: Long
        get() = prefs.getLong("active_baby_id", -1L)
        set(value) {
            prefs.edit().putLong("active_baby_id", value).apply()
            _activeBabyIdFlow.value = value
        }

    var hasSeenWelcomeMessage: Boolean
        get() = prefs.getBoolean("has_seen_welcome", false)
        set(value) = prefs.edit().putBoolean("has_seen_welcome", value).apply()
}
