package com.example.model

import androidx.annotation.StringRes
import com.example.R

enum class ExpenseCategory(@StringRes val labelRes: Int) {
    FOOD(R.string.category_food),
    TRANSPORT(R.string.category_transport),
    HOTEL(R.string.category_hotel),
    OTHER(R.string.category_other)
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class CurrencyOption(
    val code: String,
    val symbol: String,
    val isSuffix: Boolean,
    val showDecimals: Boolean
) {
    UZS(code = "UZS", symbol = "so'm", isSuffix = true, showDecimals = false),
    USD(code = "USD", symbol = "$", isSuffix = false, showDecimals = true),
    EUR(code = "EUR", symbol = "€", isSuffix = false, showDecimals = true),
    TRY(code = "TRY", symbol = "₺", isSuffix = false, showDecimals = true);

    companion object {
        fun fromCode(code: String): CurrencyOption =
            entries.find { it.code == code } ?: UZS
    }
}
