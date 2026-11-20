package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.ExpenseCategory

class ExpenseConverters {
    @TypeConverter
    fun fromCategory(category: ExpenseCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): ExpenseCategory =
        runCatching { ExpenseCategory.valueOf(value) }.getOrDefault(ExpenseCategory.OTHER)
}
