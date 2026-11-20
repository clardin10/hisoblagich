package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ExpenseCategory

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val createdAtMillis: Long = System.currentTimeMillis()
)
