package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.model.ExpenseCategory
import com.example.ui.theme.LocalTravelColors

fun ExpenseCategory.icon(): ImageVector = when (this) {
    ExpenseCategory.FOOD -> Icons.Rounded.Restaurant
    ExpenseCategory.TRANSPORT -> Icons.Rounded.DirectionsCar
    ExpenseCategory.HOTEL -> Icons.Rounded.Hotel
    ExpenseCategory.OTHER -> Icons.Rounded.ReceiptLong
}

@Composable
fun ExpenseCategory.accentColor(): Color {
    val colors = LocalTravelColors.current
    return when (this) {
        ExpenseCategory.FOOD -> colors.foodAccent
        ExpenseCategory.TRANSPORT -> colors.transportAccent
        ExpenseCategory.HOTEL -> colors.hotelAccent
        ExpenseCategory.OTHER -> colors.otherAccent
    }
}
