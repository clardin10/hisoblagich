package com.example.data.repository

import com.example.data.local.ExpenseDao
import com.example.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    suspend fun insertExpense(expense: ExpenseEntity): Long =
        expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseEntity) =
        expenseDao.updateExpense(expense)

    suspend fun deleteExpenseById(id: Long) =
        expenseDao.deleteExpenseById(id)
}
