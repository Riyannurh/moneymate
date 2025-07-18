package com.example.moneymate.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class untuk mengelola database SQLite
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "moneymate.db"
        private const val DATABASE_VERSION = 1

        // Tabel User Profile
        private const val TABLE_USER_PROFILE = "user_profile"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_NIM = "nim"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PROFILE_IMAGE = "profile_image"
        private const val COLUMN_INCOME_TARGET = "income_target"
        private const val COLUMN_EXPENSE_LIMIT = "expense_limit"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"

        // Tabel Transaction
        private const val TABLE_TRANSACTION = "transactions"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_DATE = "date"

        // Date format untuk menyimpan tanggal
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Buat tabel user profile
        val createUserProfileTable = """
            CREATE TABLE $TABLE_USER_PROFILE (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_NIM TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PROFILE_IMAGE TEXT,
                $COLUMN_INCOME_TARGET REAL DEFAULT 0,
                $COLUMN_EXPENSE_LIMIT REAL DEFAULT 0,
                $COLUMN_CREATED_AT TEXT NOT NULL,
                $COLUMN_UPDATED_AT TEXT NOT NULL
            )
        """.trimIndent()

        // Buat tabel transaksi
        val createTransactionTable = """
            CREATE TABLE $TABLE_TRANSACTION (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_CREATED_AT TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUserProfileTable)
        db.execSQL(createTransactionTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Jika versi database berubah, hapus tabel lama dan buat yang baru
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_PROFILE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTION")
        onCreate(db)
    }

    // ===== USER PROFILE METHODS =====

    /**
     * Menyimpan profil pengguna ke database
     */
    fun saveUserProfile(userProfile: UserProfile): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, userProfile.name)
            put(COLUMN_NIM, userProfile.nim)
            put(COLUMN_EMAIL, userProfile.email)
            put(COLUMN_PROFILE_IMAGE, userProfile.profileImagePath)
            put(COLUMN_INCOME_TARGET, userProfile.monthlyIncomeTarget)
            put(COLUMN_EXPENSE_LIMIT, userProfile.monthlyExpenseLimit)
            put(COLUMN_CREATED_AT, DATE_FORMAT.format(userProfile.createdAt))
            put(COLUMN_UPDATED_AT, DATE_FORMAT.format(userProfile.updatedAt))
        }

        val id = db.insert(TABLE_USER_PROFILE, null, values)
        db.close()
        return id
    }

    /**
     * Mengupdate profil pengguna
     */
    fun updateUserProfile(userProfile: UserProfile): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, userProfile.name)
            put(COLUMN_NIM, userProfile.nim)
            put(COLUMN_EMAIL, userProfile.email)
            put(COLUMN_PROFILE_IMAGE, userProfile.profileImagePath)
            put(COLUMN_INCOME_TARGET, userProfile.monthlyIncomeTarget)
            put(COLUMN_EXPENSE_LIMIT, userProfile.monthlyExpenseLimit)
            put(COLUMN_UPDATED_AT, DATE_FORMAT.format(Date()))
        }

        val result = db.update(
            TABLE_USER_PROFILE,
            values,
            "$COLUMN_ID = ?",
            arrayOf(userProfile.id.toString())
        )
        db.close()
        return result
    }

    /**
     * Mendapatkan profil pengguna
     */
    fun getUserProfile(): UserProfile? {
        val db = this.readableDatabase
        var userProfile: UserProfile? = null

        val cursor = db.query(
            TABLE_USER_PROFILE,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            userProfile = getUserProfileFromCursor(cursor)
        }

        cursor.close()
        db.close()
        return userProfile
    }

    private fun getUserProfileFromCursor(cursor: Cursor): UserProfile {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val nim = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIM))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val profileImage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE))
        val incomeTarget = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INCOME_TARGET))
        val expenseLimit = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_LIMIT))
        val createdAtStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
        val updatedAtStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))

        val createdAt = DATE_FORMAT.parse(createdAtStr) ?: Date()
        val updatedAt = DATE_FORMAT.parse(updatedAtStr) ?: Date()

        return UserProfile(
            id = id,
            name = name,
            nim = nim,
            email = email,
            profileImagePath = profileImage,
            monthlyIncomeTarget = incomeTarget,
            monthlyExpenseLimit = expenseLimit,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    // ===== TRANSACTION METHODS =====

    /**
     * Menyimpan transaksi ke database
     */
    fun saveTransaction(transaction: Transaction): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_DATE, DATE_FORMAT.format(transaction.date))
            put(COLUMN_CREATED_AT, DATE_FORMAT.format(transaction.createdAt))
        }

        val id = db.insert(TABLE_TRANSACTION, null, values)
        db.close()
        return id
    }

    /**
     * Mendapatkan semua transaksi
     */
    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTION,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val transaction = getTransactionFromCursor(cursor)
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return transactions
    }

    /**
     * Mendapatkan transaksi berdasarkan tipe (pemasukan/pengeluaran)
     */
    fun getTransactionsByType(type: TransactionType): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTION,
            null,
            "$COLUMN_TYPE = ?",
            arrayOf(type.name),
            null,
            null,
            "$COLUMN_DATE DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val transaction = getTransactionFromCursor(cursor)
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return transactions
    }

    private fun getTransactionFromCursor(cursor: Cursor): Transaction {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
        val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
        val typeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
        val dateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
        val createdAtStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))

        val type = TransactionType.valueOf(typeStr)
        val date = DATE_FORMAT.parse(dateStr) ?: Date()
        val createdAt = DATE_FORMAT.parse(createdAtStr) ?: Date()

        return Transaction(
            id = id,
            amount = amount,
            description = description,
            category = category,
            type = type,
            date = date,
            createdAt = createdAt
        )
    }

    /**
     * Menghapus transaksi berdasarkan ID
     */
    fun deleteTransaction(transactionId: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(
            TABLE_TRANSACTION,
            "$COLUMN_ID = ?",
            arrayOf(transactionId.toString())
        )
        db.close()
        return result
    }

    /**
     * Mengupdate transaksi
     */
    fun updateTransaction(transaction: Transaction): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_DATE, DATE_FORMAT.format(transaction.date))
        }

        val result = db.update(
            TABLE_TRANSACTION,
            values,
            "$COLUMN_ID = ?",
            arrayOf(transaction.id.toString())
        )
        db.close()
        return result
    }

    /**
     * Mendapatkan ringkasan keuangan
     */
    fun getFinancialSummary(): FinancialSummary {
        val db = this.readableDatabase
        
        // Total pemasukan
        val totalIncomeQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTION WHERE $COLUMN_TYPE = ?"
        val totalIncomeCursor = db.rawQuery(totalIncomeQuery, arrayOf(TransactionType.INCOME.name))
        totalIncomeCursor.moveToFirst()
        val totalIncome = if (totalIncomeCursor.isNull(0)) 0.0 else totalIncomeCursor.getDouble(0)
        totalIncomeCursor.close()

        // Total pengeluaran
        val totalExpenseQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTION WHERE $COLUMN_TYPE = ?"
        val totalExpenseCursor = db.rawQuery(totalExpenseQuery, arrayOf(TransactionType.EXPENSE.name))
        totalExpenseCursor.moveToFirst()
        val totalExpense = if (totalExpenseCursor.isNull(0)) 0.0 else totalExpenseCursor.getDouble(0)
        totalExpenseCursor.close()

        // Jumlah transaksi
        val transactionCountQuery = "SELECT COUNT(*) FROM $TABLE_TRANSACTION"
        val transactionCountCursor = db.rawQuery(transactionCountQuery, null)
        transactionCountCursor.moveToFirst()
        val transactionCount = transactionCountCursor.getInt(0)
        transactionCountCursor.close()

        // Jumlah transaksi pemasukan
        val incomeCountQuery = "SELECT COUNT(*) FROM $TABLE_TRANSACTION WHERE $COLUMN_TYPE = ?"
        val incomeCountCursor = db.rawQuery(incomeCountQuery, arrayOf(TransactionType.INCOME.name))
        incomeCountCursor.moveToFirst()
        val incomeCount = incomeCountCursor.getInt(0)
        incomeCountCursor.close()

        // Jumlah transaksi pengeluaran
        val expenseCountQuery = "SELECT COUNT(*) FROM $TABLE_TRANSACTION WHERE $COLUMN_TYPE = ?"
        val expenseCountCursor = db.rawQuery(expenseCountQuery, arrayOf(TransactionType.EXPENSE.name))
        expenseCountCursor.moveToFirst()
        val expenseCount = expenseCountCursor.getInt(0)
        expenseCountCursor.close()

        db.close()

        return FinancialSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            currentBalance = totalIncome - totalExpense,
            monthlyIncome = totalIncome, // Untuk sederhananya, kita gunakan total sebagai bulanan
            monthlyExpense = totalExpense,
            transactionCount = transactionCount,
            incomeTransactionCount = incomeCount,
            expenseTransactionCount = expenseCount
        )
    }
}
