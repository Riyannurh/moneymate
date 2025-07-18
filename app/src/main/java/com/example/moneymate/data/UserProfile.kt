package com.example.moneymate.data

import java.util.Date

/**
 * Data class untuk menyimpan informasi profil pengguna
 */
data class UserProfile(
    val id: Long = 0,
    val name: String,
    val nim: String,
    val email: String,
    val profileImagePath: String? = null,
    val monthlyIncomeTarget: Double = 0.0,
    val monthlyExpenseLimit: Double = 0.0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
