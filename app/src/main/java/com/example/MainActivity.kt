package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.preferences.TripPreferencesRepository
import com.example.data.repository.ExpenseRepository
import com.example.ui.TravelExpenseDashboardScreen
import com.example.ui.TravelExpenseViewModel
import com.example.ui.theme.TravelExpenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val expenseRepository = ExpenseRepository(database.expenseDao())
        val preferencesRepository = TripPreferencesRepository(applicationContext)
        val viewModelFactory = TravelExpenseViewModel.Factory(
            expenseRepository = expenseRepository,
            preferencesRepository = preferencesRepository
        )

        setContent {
            val viewModel: TravelExpenseViewModel = viewModel(factory = viewModelFactory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TravelExpenseTheme(themeMode = uiState.tripSettings.themeMode) {
                TravelExpenseDashboardScreen(viewModel = viewModel)
            }
        }
    }
}
