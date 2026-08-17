package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class AppSetting(
    @PrimaryKey val id: Int = 1, // Only 1 row ever exists
    val jengaMode: Boolean = true,
    val textSize: Int = 18, // 14 to 28
    val bleachLevel: String = "High", // "Standard", "High", "Max"
    val hdSymbolLogic: Boolean = true,
    val animationDuration: Int = 100, // 0 to 300 ms
    val refreshMode: String = "Normal" // "Normal", "A2", "Speed"
)
