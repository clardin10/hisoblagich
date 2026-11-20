package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.ExpenseEntity
import com.example.model.CurrencyOption
import com.example.model.ExpenseCategory
import com.example.util.Formatters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditExpenseSheet(
    editingExpense: ExpenseEntity?,
    currency: CurrencyOption,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: ExpenseCategory) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember(editingExpense) {
        mutableStateOf(editingExpense?.title ?: "")
    }
    var amountText by remember(editingExpense) {
        mutableStateOf(
            editingExpense?.let { Formatters.formatEditableAmount(it.amount, currency) } ?: ""
        )
    }
    var selectedCategory by remember(editingExpense) {
        mutableStateOf(editingExpense?.category ?: ExpenseCategory.FOOD)
    }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val validateAndSave = {
        val parsedAmount = amountText
            .replace(',', '.')
            .replace(" ", "")
            .toDoubleOrNull()
        val isTitleValid = title.trim().isNotEmpty()
        val isAmountValid = parsedAmount != null && parsedAmount > 0.0

        titleError = !isTitleValid
        amountError = !isAmountValid

        if (isTitleValid && isAmountValid && parsedAmount != null) {
            onSave(title.trim(), parsedAmount, selectedCategory)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            Text(
                text = if (editingExpense == null) {
                    stringResource(R.string.add_expense_title)
                } else {
                    stringResource(R.string.edit_expense_title)
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (titleError) titleError = false
                },
                label = { Text(stringResource(R.string.expense_name_label)) },
                placeholder = { Text(stringResource(R.string.expense_name_hint)) },
                singleLine = true,
                isError = titleError,
                supportingText = if (titleError) {
                    { Text(stringResource(R.string.error_empty_name)) }
                } else null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { raw ->
                    val filtered = raw.filter { it.isDigit() || it == '.' || it == ',' }
                    amountText = filtered
                    if (amountError) amountError = false
                },
                label = { Text(stringResource(R.string.expense_amount_label)) },
                suffix = if (currency.isSuffix) {
                    { Text(currency.symbol) }
                } else null,
                prefix = if (!currency.isSuffix) {
                    { Text(currency.symbol) }
                } else null,
                singleLine = true,
                isError = amountError,
                supportingText = if (amountError) {
                    { Text(stringResource(R.string.error_invalid_amount)) }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { validateAndSave() }
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_amount_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.expense_category_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseCategory.entries.forEach { category ->
                    val selected = selectedCategory == category
                    val accent = category.accentColor()
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = category },
                        label = { Text(stringResource(category.labelRes)) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (selected) Icons.Rounded.Check else category.icon(),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accent.copy(alpha = 0.18f),
                            selectedLabelColor = accent,
                            selectedLeadingIconColor = accent
                        ),
                        modifier = Modifier.testTag("category_chip_${category.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("cancel_expense_button")
                ) {
                    Text(stringResource(R.string.cancel_button))
                }
                Button(
                    onClick = validateAndSave,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("save_expense_button")
                ) {
                    Text(stringResource(R.string.save_button))
                }
            }
        }
    }
}
