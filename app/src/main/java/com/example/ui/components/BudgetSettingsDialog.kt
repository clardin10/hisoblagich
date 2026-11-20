package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.CurrencyOption
import com.example.util.Formatters

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BudgetSettingsDialog(
    initialTripName: String,
    initialBudget: Double,
    initialCurrency: CurrencyOption,
    onDismiss: () -> Unit,
    onSave: (tripName: String, totalBudget: Double, currency: CurrencyOption) -> Unit
) {
    var tripName by remember(initialTripName) { mutableStateOf(initialTripName) }
    var budgetText by remember(initialBudget, initialCurrency) {
        mutableStateOf(Formatters.formatEditableAmount(initialBudget, initialCurrency))
    }
    var selectedCurrency by remember(initialCurrency) { mutableStateOf(initialCurrency) }
    var budgetError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.budget_dialog_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = tripName,
                    onValueChange = { tripName = it },
                    label = { Text(stringResource(R.string.trip_name_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trip_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { raw ->
                        budgetText = raw.filter { it.isDigit() || it == '.' || it == ',' }
                        if (budgetError) budgetError = false
                    },
                    label = { Text(stringResource(R.string.total_budget_label)) },
                    suffix = if (selectedCurrency.isSuffix) {
                        { Text(selectedCurrency.symbol) }
                    } else null,
                    prefix = if (!selectedCurrency.isSuffix) {
                        { Text(selectedCurrency.symbol) }
                    } else null,
                    singleLine = true,
                    isError = budgetError,
                    supportingText = if (budgetError) {
                        { Text(stringResource(R.string.error_invalid_amount)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_amount_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.currency_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurrencyOption.entries.forEach { option ->
                        FilterChip(
                            selected = selectedCurrency == option,
                            onClick = { selectedCurrency = option },
                            label = { Text("${option.code} (${option.symbol})") },
                            modifier = Modifier.testTag("currency_chip_${option.code.lowercase()}")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedBudget = budgetText
                        .replace(',', '.')
                        .replace(" ", "")
                        .toDoubleOrNull()
                    if (parsedBudget == null || parsedBudget <= 0.0) {
                        budgetError = true
                    } else {
                        onSave(tripName.trim(), parsedBudget, selectedCurrency)
                    }
                },
                modifier = Modifier.testTag("save_budget_button")
            ) {
                Text(stringResource(R.string.save_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}
