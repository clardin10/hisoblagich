package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ExpenseEntity
import com.example.model.CurrencyOption
import com.example.model.ExpenseCategory
import com.example.util.Formatters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Sayohat Hisobi", appName)
    }

    @Test
    fun `insert and delete expenses in Room DAO`() = runBlocking {
        val dao = database.expenseDao()
        val id = dao.insertExpense(
            ExpenseEntity(
                title = "Tushlik",
                amount = 85000.0,
                category = ExpenseCategory.FOOD,
                createdAtMillis = 1_700_000_000_000L
            )
        )
        assertTrue(id > 0)

        val listAfterInsert = dao.getAllExpenses().first()
        assertEquals(1, listAfterInsert.size)
        assertEquals("Tushlik", listAfterInsert.first().title)
        assertEquals(ExpenseCategory.FOOD, listAfterInsert.first().category)

        dao.deleteExpenseById(id)
        val listAfterDelete = dao.getAllExpenses().first()
        assertTrue(listAfterDelete.isEmpty())
    }

    @Test
    fun `format money formats UZS and USD accurately`() {
        assertEquals("5 000 000 so'm", Formatters.formatMoney(5_000_000.0, CurrencyOption.UZS))
        assertEquals("-$125.5", Formatters.formatMoney(-125.5, CurrencyOption.USD))
    }
}
