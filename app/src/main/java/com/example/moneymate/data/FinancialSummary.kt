package com.example.moneymate.data

/**
 * Data class untuk ringkasan keuangan pengguna
 */
data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val currentBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val transactionCount: Int = 0,
    val incomeTransactionCount: Int = 0,
    val expenseTransactionCount: Int = 0
) {
    /**
     * Menghitung persentase pengeluaran dari pemasukan
     */
    fun getExpensePercentage(): Double {
        return if (totalIncome > 0) {
            (totalExpense / totalIncome) * 100
        } else {
            0.0
        }
    }

    /**
     * Menghitung rata-rata transaksi per hari (30 hari terakhir)
     */
    fun getAverageTransactionPerDay(): Double {
        return transactionCount / 30.0
    }
}
