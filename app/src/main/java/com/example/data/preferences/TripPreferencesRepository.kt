package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.CurrencyOption
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tripDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "travel_budget_prefs"
)

data class TripSettings(
    val tripName: String = "Mening sayohatim",
    val totalBudget: Double = 5_000_000.0,
    val currency: CurrencyOption = CurrencyOption.UZS,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

class TripPreferencesRepository(private val context: Context) {

    private object Keys {
        val TRIP_NAME = stringPreferencesKey("trip_name")
        val TOTAL_BUDGET = doublePreferencesKey("total_budget")
        val CURRENCY_CODE = stringPreferencesKey("currency_code")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val tripSettings: Flow<TripSettings> = context.tripDataStore.data.map { prefs ->
        TripSettings(
            tripName = prefs[Keys.TRIP_NAME]?.takeIf { it.isNotBlank() } ?: "Mening sayohatim",
            totalBudget = prefs[Keys.TOTAL_BUDGET] ?: 5_000_000.0,
            currency = CurrencyOption.fromCode(prefs[Keys.CURRENCY_CODE] ?: CurrencyOption.UZS.code),
            themeMode = runCatching {
                ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
            }.getOrDefault(ThemeMode.SYSTEM)
        )
    }

    suspend fun updateBudgetSettings(
        tripName: String,
        totalBudget: Double,
        currency: CurrencyOption
    ) {
        context.tripDataStore.edit { prefs ->
            prefs[Keys.TRIP_NAME] = tripName.trim().ifEmpty { "Mening sayohatim" }
            prefs[Keys.TOTAL_BUDGET] = totalBudget.coerceAtLeast(0.0)
            prefs[Keys.CURRENCY_CODE] = currency.code
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.tripDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = themeMode.name
        }
    }
}
