package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String, // Also serves as User ID or login ID
    val email: String,
    val phone: String,
    val identityProofType: String, // e.g., Aadhaar, PAN Card, Driving License
    val identityProofValue: String,
    val brokerName: String, // e.g., Zerodha, AngelOne, Groww, Upstox
    val brokerClientId: String,
    val brokerApiKey: String,
    val isOwner: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // Username of client who placed the order
    val userDisplayName: String, // To display client's name to owner
    val stockSymbol: String,
    val stockName: String,
    val quantity: Int,
    val targetPrice: Double,
    val notes: String = "",
    val orderType: String, // "BUY" or "SELL"
    val status: String, // "Pending", "Executed", "Cancelled"
    val timestamp: Long = System.currentTimeMillis()
)
