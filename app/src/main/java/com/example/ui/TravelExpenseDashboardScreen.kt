package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.ExpenseEntity
import com.example.model.ExpenseCategory
import com.example.model.ThemeMode
import com.example.ui.components.AddEditExpenseSheet
import com.example.ui.components.BudgetHeroCard
import com.example.ui.components.BudgetSettingsDialog
import com.example.ui.components.EmptyExpensesState
import com.example.ui.components.ExpenseItemCard
import com.example.ui.components.accentColor
import com.example.ui.components.icon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelExpenseDashboardScreen(
    viewModel: TravelExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showExpenseSheet by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    val deletedMessage = stringResource(R.string.expense_deleted_snackbar)
    val undoLabel = stringResource(R.string.undo_action)

    val onDeleteWithUndo: (ExpenseEntity) -> Unit = { expense ->
        viewModel.deleteExpense(expense)
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = "$deletedMessage: ${expense.title}",
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.restoreExpense(expense)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.cycleThemeMode() },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        val themeIcon = when (uiState.tripSettings.themeMode) {
                            ThemeMode.SYSTEM -> Icons.Rounded.BrightnessAuto
                            ThemeMode.LIGHT -> Icons.Rounded.LightMode
                            ThemeMode.DARK -> Icons.Rounded.DarkMode
                        }
                        Icon(
                            imageVector = themeIcon,
                            contentDescription = stringResource(R.string.toggle_theme_cd),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { showBudgetDialog = true },
                        modifier = Modifier.testTag("topbar_budget_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.edit_budget_cd),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingExpense = null
                    showExpenseSheet = true
                },
                shape = RoundedCornerShape(18.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add_expense_cd),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 720.dp

            if (isWideScreen) {
                // Two-Pane Adaptive Layout for Tablets / Foldables
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 8.dp, bottom = 24.dp)
                    ) {
                        BudgetHeroCard(
                            tripName = uiState.tripSettings.tripName,
                            totalBudget = uiState.tripSettings.totalBudget,
                            totalSpent = uiState.totalSpent,
                            remainingBudget = uiState.remainingBudget,
                            spentProgress = uiState.spentProgress,
                            isOverBudget = uiState.isOverBudget,
                            currency = uiState.tripSettings.currency,
                            categoryTotals = uiState.categoryTotals,
                            onEditBudgetClick = { showBudgetDialog = true }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight()
                    ) {
                        ExpenseSectionHeader(
                            expenseCount = uiState.filteredExpenses.size,
                            selectedFilter = uiState.selectedCategoryFilter,
                            onSelectFilter = viewModel::selectCategoryFilter
                        )

                        ExpenseListPane(
                            expenses = uiState.filteredExpenses,
                            isFiltered = uiState.selectedCategoryFilter != null,
                            uiState = uiState,
                            onExpenseClick = { expense ->
                                editingExpense = expense
                                showExpenseSheet = true
                            },
                            onExpenseDelete = onDeleteWithUndo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Single-Column Mobile Layout
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp)
                        .align(Alignment.TopCenter),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "hero_budget_card") {
                        BudgetHeroCard(
                            tripName = uiState.tripSettings.tripName,
                            totalBudget = uiState.tripSettings.totalBudget,
                            totalSpent = uiState.totalSpent,
                            remainingBudget = uiState.remainingBudget,
                            spentProgress = uiState.spentProgress,
                            isOverBudget = uiState.isOverBudget,
                            currency = uiState.tripSettings.currency,
                            categoryTotals = uiState.categoryTotals,
                            onEditBudgetClick = { showBudgetDialog = true }
                        )
                    }

                    item(key = "filter_header") {
                        ExpenseSectionHeader(
                            expenseCount = uiState.filteredExpenses.size,
                            selectedFilter = uiState.selectedCategoryFilter,
                            onSelectFilter = viewModel::selectCategoryFilter,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    if (uiState.filteredExpenses.isEmpty() && uiState.isLoaded) {
                        item(key = "empty_state") {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                EmptyExpensesState(
                                    isFiltered = uiState.selectedCategoryFilter != null
                                )
                            }
                        }
                    } else {
                        items(
                            items = uiState.filteredExpenses,
                            key = { it.id }
                        ) { expense ->
                            ExpenseItemCard(
                                expense = expense,
                                currency = uiState.tripSettings.currency,
                                onClick = {
                                    editingExpense = expense
                                    showExpenseSheet = true
                                },
                                onDelete = { onDeleteWithUndo(expense) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExpenseSheet) {
        AddEditExpenseSheet(
            editingExpense = editingExpense,
            currency = uiState.tripSettings.currency,
            onDismiss = {
                showExpenseSheet = false
                editingExpense = null
            },
            onSave = { title, amount, category ->
                viewModel.saveExpense(
                    existingExpense = editingExpense,
                    title = title,
                    amount = amount,
                    category = category
                )
                showExpenseSheet = false
                editingExpense = null
            }
        )
    }

    if (showBudgetDialog) {
        BudgetSettingsDialog(
            initialTripName = uiState.tripSettings.tripName,
            initialBudget = uiState.tripSettings.totalBudget,
            initialCurrency = uiState.tripSettings.currency,
            onDismiss = { showBudgetDialog = false },
            onSave = { tripName, totalBudget, currency ->
                viewModel.updateBudgetSettings(
                    tripName = tripName,
                    totalBudget = totalBudget,
                    currency = currency
                )
                showBudgetDialog = false
            }
        )
    }
}

@Composable
private fun ExpenseSectionHeader(
    expenseCount: Int,
    selectedFilter: ExpenseCategory?,
    onSelectFilter: (ExpenseCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.expenses_section_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$expenseCount ta",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "filter_all") {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { onSelectFilter(null) },
                    label = { Text(stringResource(R.string.filter_all)) },
                    modifier = Modifier.testTag("filter_chip_all")
                )
            }
            items(ExpenseCategory.entries, key = { it.name }) { category ->
                val selected = selectedFilter == category
                val accent = category.accentColor()
                FilterChip(
                    selected = selected,
                    onClick = {
                        onSelectFilter(if (selected) null else category)
                    },
                    label = { Text(stringResource(category.labelRes)) },
                    leadingIcon = {
                        Icon(
                            imageVector = category.icon(),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accent.copy(alpha = 0.18f),
                        selectedLabelColor = accent,
                        selectedLeadingIconColor = accent
                    ),
                    modifier = Modifier.testTag("filter_chip_${category.name.lowercase()}")
                )
            }
        }
    }
}

@Composable
private fun ExpenseListPane(
    expenses: List<ExpenseEntity>,
    isFiltered: Boolean,
    uiState: TravelExpenseUiState,
    onExpenseClick: (ExpenseEntity) -> Unit,
    onExpenseDelete: (ExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (expenses.isEmpty() && uiState.isLoaded) {
        EmptyExpensesState(
            isFiltered = isFiltered,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = expenses,
                key = { it.id }
            ) { expense ->
                ExpenseItemCard(
                    expense = expense,
                    currency = uiState.tripSettings.currency,
                    onClick = { onExpenseClick(expense) },
                    onDelete = { onExpenseDelete(expense) }
                )
            }
        }
    }
}
