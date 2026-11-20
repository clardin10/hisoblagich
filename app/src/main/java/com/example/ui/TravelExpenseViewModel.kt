package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExpenseEntity
import com.example.data.preferences.TripPreferencesRepository
import com.example.data.preferences.TripSettings
import com.example.data.repository.ExpenseRepository
import com.example.model.CurrencyOption
import com.example.model.ExpenseCategory
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TravelExpenseUiState(
    val tripSettings: TripSettings = TripSettings(),
    val allExpenses: List<ExpenseEntity> = emptyList(),
    val filteredExpenses: List<ExpenseEntity> = emptyList(),
    val selectedCategoryFilter: ExpenseCategory? = null,
    val totalSpent: Double = 0.0,
    val remainingBudget: Double = 5_000_000.0,
    val spentProgress: Float = 0f,
    val isOverBudget: Boolean = false,
    val categoryTotals: Map<ExpenseCategory, Double> = emptyMap(),
    val isLoaded: Boolean = false
)

class TravelExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val preferencesRepository: TripPreferencesRepository
) : ViewModel() {

    private val selectedFilter = MutableStateFlow<ExpenseCategory?>(null)

    val uiState: StateFlow<TravelExpenseUiState> = combine(
        expenseRepository.allExpenses,
        preferencesRepository.tripSettings,
        selectedFilter
    ) { expenses, settings, filter ->
        val totalSpent = expenses.sumOf { it.amount }
        val remaining = settings.totalBudget - totalSpent
        val progress = if (settings.totalBudget > 0.0) {
            (totalSpent / settings.totalBudget).toFloat().coerceAtLeast(0f)
        } else if (totalSpent > 0.0) {
            1.1f
        } else {
            0f
        }
        val filtered = if (filter == null) {
            expenses
        } else {
            expenses.filter { it.category == filter }
        }
        val categoryTotals = ExpenseCategory.entries.associateWith { cat ->
            expenses.filter { it.category == cat }.sumOf { it.amount }
        }

        TravelExpenseUiState(
            tripSettings = settings,
            allExpenses = expenses,
            filteredExpenses = filtered,
            selectedCategoryFilter = filter,
            totalSpent = totalSpent,
            remainingBudget = remaining,
            spentProgress = progress,
            isOverBudget = remaining < 0.0,
            categoryTotals = categoryTotals,
            isLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TravelExpenseUiState()
    )

    fun selectCategoryFilter(category: ExpenseCategory?) {
        selectedFilter.value = category
    }

    fun saveExpense(
        existingExpense: ExpenseEntity?,
        title: String,
        amount: Double,
        category: ExpenseCategory
    ) {
        viewModelScope.launch {
            if (existingExpense == null) {
                expenseRepository.insertExpense(
                    ExpenseEntity(
                        title = title.trim(),
                        amount = amount,
                        category = category,
                        createdAtMillis = System.currentTimeMillis()
                    )
                )
            } else {
                expenseRepository.updateExpense(
                    existingExpense.copy(
                        title = title.trim(),
                        amount = amount,
                        category = category
                    )
                )
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.deleteExpenseById(expense.id)
        }
    }

    fun restoreExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.insertExpense(expense)
        }
    }

    fun updateBudgetSettings(
        tripName: String,
        totalBudget: Double,
        currency: CurrencyOption
    ) {
        viewModelScope.launch {
            preferencesRepository.updateBudgetSettings(
                tripName = tripName,
                totalBudget = totalBudget,
                currency = currency
            )
        }
    }

    fun cycleThemeMode() {
        viewModelScope.launch {
            val current = uiState.value.tripSettings.themeMode
            val next = when (current) {
                ThemeMode.SYSTEM -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.SYSTEM
            }
            preferencesRepository.setThemeMode(next)
        }
    }

    class Factory(
        private val expenseRepository: ExpenseRepository,
        private val preferencesRepository: TripPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TravelExpenseViewModel::class.java)) {
                return TravelExpenseViewModel(expenseRepository, preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
