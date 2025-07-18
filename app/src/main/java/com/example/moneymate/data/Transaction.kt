package com.example.moneymate.data

import java.util.Date

/**
 * Data class untuk menyimpan informasi transaksi
 */
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val type: TransactionType,
    val date: Date,
    val createdAt: Date = Date()
)

/**
 * Enum untuk tipe transaksi
 */
enum class TransactionType {
    INCOME,    // Pemasukan
    EXPENSE    // Pengeluaran
}
