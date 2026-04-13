package com.salazar.babytraker.core.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BabyPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("baby_prefs", Context.MODE_PRIVATE)

    var activeBabyId: Long
        get() = prefs.getLong("active_baby_id", -1L)
        set(value) = prefs.edit().putLong("active_baby_id", value).apply()
}
