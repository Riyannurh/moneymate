package com.example.moneymate

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymate.adapter.TransactionAdapter
import com.example.moneymate.data.DatabaseHelper
import com.example.moneymate.data.Transaction
import com.example.moneymate.data.TransactionType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiwayatActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var transactionAdapter: TransactionAdapter

    // UI Components
    private lateinit var rvTransactions: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var fabAddTransaction: FloatingActionButton
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Initialize UI
        initViews()
        setupRecyclerView()
        setupClickListeners()

        // Load data
        loadTransactions()
        loadSummary()
    }

    private fun initViews() {
        rvTransactions = findViewById(R.id.rvTransactions)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        fabAddTransaction = findViewById(R.id.fabAddTransaction)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onEditClick = { transaction -> showEditTransactionDialog(transaction) },
            onDeleteClick = { transaction -> showDeleteConfirmationDialog(transaction) }
        )
        rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@RiwayatActivity)
            adapter = transactionAdapter
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        fabAddTransaction.setOnClickListener { view ->
            showTransactionMenu(view)
        }
    }

    private fun showTransactionMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.transaction_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_income -> {
                    val intent = Intent(this, TransaksiActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_expense -> {
                    val intent = Intent(this, TransaksiActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_print_pdf -> {
                    cetakRiwayatKePDF()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun loadTransactions() {
        val transactions = dbHelper.getAllTransactions()

        if (transactions.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvTransactions.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvTransactions.visibility = View.VISIBLE
            transactionAdapter.updateTransactions(transactions)
        }
    }

    private fun loadSummary() {
        val summary = dbHelper.getFinancialSummary()

        // Format currency
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvTotalIncome.text = currencyFormat.format(summary.totalIncome)
        tvTotalExpense.text = currencyFormat.format(summary.totalExpense)
    }

    private fun cetakRiwayatKePDF() {
        try {
            // Buat dokumen PDF
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            // Setup paint untuk teks
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 24f
                isFakeBoldText = true
            }

            val headerPaint = Paint().apply {
                color = Color.BLACK
                textSize = 16f
                isFakeBoldText = true
            }

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }

            val linePaint = Paint().apply {
                color = Color.GRAY
                strokeWidth = 1f
            }

            var yPosition = 50f
            val leftMargin = 50f
            val rightMargin = 545f

            // Judul
            canvas.drawText("LAPORAN RIWAYAT TRANSAKSI", leftMargin, yPosition, titlePaint)
            yPosition += 30f

            // Tanggal cetak
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            canvas.drawText("Dicetak pada: ${dateFormat.format(Date())}", leftMargin, yPosition, textPaint)
            yPosition += 40f

            // Ringkasan keuangan
            val summary = dbHelper.getFinancialSummary()
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

            canvas.drawText("RINGKASAN KEUANGAN", leftMargin, yPosition, headerPaint)
            yPosition += 25f

            canvas.drawText("Total Pemasukan: ${currencyFormat.format(summary.totalIncome)}", leftMargin, yPosition, textPaint)
            yPosition += 20f

            canvas.drawText("Total Pengeluaran: ${currencyFormat.format(summary.totalExpense)}", leftMargin, yPosition, textPaint)
            yPosition += 20f

            canvas.drawText("Saldo: ${currencyFormat.format(summary.currentBalance)}", leftMargin, yPosition, textPaint)
            yPosition += 40f

            // Garis pemisah
            canvas.drawLine(leftMargin, yPosition, rightMargin, yPosition, linePaint)
            yPosition += 30f

            // Header tabel transaksi
            canvas.drawText("RIWAYAT TRANSAKSI", leftMargin, yPosition, headerPaint)
            yPosition += 25f

            // Header kolom
            canvas.drawText("Tanggal", leftMargin, yPosition, headerPaint)
            canvas.drawText("Tipe", leftMargin + 80, yPosition, headerPaint)
            canvas.drawText("Deskripsi", leftMargin + 150, yPosition, headerPaint)
            canvas.drawText("Jumlah", leftMargin + 350, yPosition, headerPaint)
            yPosition += 20f

            // Garis bawah header
            canvas.drawLine(leftMargin, yPosition, rightMargin, yPosition, linePaint)
            yPosition += 15f

            // Data transaksi
            val transactions = dbHelper.getAllTransactions()
            val transactionDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            for (transaction in transactions) {
                if (yPosition > 780) { // Jika mendekati batas halaman
                    pdfDocument.finishPage(page)
                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    val newPage = pdfDocument.startPage(newPageInfo)
                    canvas = newPage.canvas
                    yPosition = 50f
                }

                val tanggal = transactionDateFormat.format(transaction.date)
                val tipe = if (transaction.type.name == "INCOME") "Masuk" else "Keluar"
                val deskripsi = if (transaction.description.length > 25) {
                    transaction.description.substring(0, 25) + "..."
                } else {
                    transaction.description
                }
                val jumlah = currencyFormat.format(transaction.amount)

                canvas.drawText(tanggal, leftMargin, yPosition, textPaint)
                canvas.drawText(tipe, leftMargin + 80, yPosition, textPaint)
                canvas.drawText(deskripsi, leftMargin + 150, yPosition, textPaint)
                canvas.drawText(jumlah, leftMargin + 350, yPosition, textPaint)

                yPosition += 18f
            }

            pdfDocument.finishPage(page)

            // Simpan PDF ke Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "Riwayat_Transaksi_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val file = File(downloadsDir, fileName)

            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()

            Toast.makeText(this, "PDF berhasil disimpan di Downloads/$fileName", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuat PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditTransactionDialog(transaction: Transaction) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_transaction, null)

        val etAmount = dialogView.findViewById<EditText>(R.id.etEditAmount)
        val etDescription = dialogView.findViewById<EditText>(R.id.etEditDescription)
        val etCategory = dialogView.findViewById<EditText>(R.id.etEditCategory)

        // Set current values
        etAmount.setText(transaction.amount.toString())
        etDescription.setText(transaction.description)
        etCategory.setText(transaction.category)

        AlertDialog.Builder(this)
            .setTitle("Edit Transaksi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newAmount = etAmount.text.toString().toDoubleOrNull()
                val newDescription = etDescription.text.toString().trim()
                val newCategory = etCategory.text.toString().trim()

                if (newAmount != null && newDescription.isNotEmpty() && newCategory.isNotEmpty()) {
                    val updatedTransaction = transaction.copy(
                        amount = newAmount,
                        description = newDescription,
                        category = newCategory
                    )

                    val result = dbHelper.updateTransaction(updatedTransaction)
                    if (result > 0) {
                        Toast.makeText(this, "Transaksi berhasil diupdate", Toast.LENGTH_SHORT).show()
                        loadTransactions()
                        loadSummary()
                    } else {
                        Toast.makeText(this, "Gagal mengupdate transaksi", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Mohon isi semua field dengan benar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Transaksi")
            .setMessage("Apakah Anda yakin ingin menghapus transaksi ini?\n\n" +
                    "Jumlah: ${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(transaction.amount)}\n" +
                    "Deskripsi: ${transaction.description}")
            .setPositiveButton("Hapus") { _, _ ->
                val result = dbHelper.deleteTransaction(transaction.id)
                if (result > 0) {
                    Toast.makeText(this, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadTransactions()
                    loadSummary()
                } else {
                    Toast.makeText(this, "Gagal menghapus transaksi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Reload data when returning to this activity
        loadTransactions()
        loadSummary()
    }
}