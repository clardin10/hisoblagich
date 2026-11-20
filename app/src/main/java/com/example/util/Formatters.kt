package com.example.util

import com.example.model.CurrencyOption
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object Formatters {
    private val uzbekMonths = arrayOf(
        "yan", "fev", "mar", "apr", "may", "iyun",
        "iyul", "avg", "sen", "okt", "noy", "dek"
    )

    fun formatMoney(amount: Double, currency: CurrencyOption, showSign: Boolean = false): String {
        val absValue = abs(amount)
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ' '
            decimalSeparator = '.'
        }
        val pattern = if (currency.showDecimals) "#,##0.##" else "#,##0"
        val formattedNumber = DecimalFormat(pattern, symbols).format(absValue)
        val signPrefix = when {
            amount < 0 -> "-"
            showSign && amount > 0 -> "+"
            else -> ""
        }
        return if (currency.isSuffix) {
            "$signPrefix$formattedNumber ${currency.symbol}"
        } else {
            "$signPrefix${currency.symbol}$formattedNumber"
        }
    }

    fun formatEditableAmount(amount: Double, currency: CurrencyOption): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            decimalSeparator = '.'
        }
        val pattern = if (currency.showDecimals) "0.##" else "0"
        return DecimalFormat(pattern, symbols).format(amount)
    }

    fun formatExpenseDate(timestampMillis: Long): String {
        val date = Date(timestampMillis)
        val monthIndex = SimpleDateFormat("M", Locale.US).format(date).toIntOrNull()?.minus(1) ?: 0
        val day = SimpleDateFormat("d", Locale.US).format(date)
        val year = SimpleDateFormat("yyyy", Locale.US).format(date)
        val time = SimpleDateFormat("HH:mm", Locale.US).format(date)
        val monthName = uzbekMonths.getOrElse(monthIndex) { "" }
        return "$day-$monthName, $year · $time"
    }
}
