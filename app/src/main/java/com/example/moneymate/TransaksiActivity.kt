package com.example.moneymate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.moneymate.data.DatabaseHelper
import com.example.moneymate.data.Transaction
import com.example.moneymate.data.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class TransaksiActivity : AppCompatActivity() {

    private lateinit var etNominal: EditText
    private lateinit var spinnerJenisTransaksi: Spinner
    private lateinit var layoutTanggal: LinearLayout
    private lateinit var tvTanggal: TextView
    private lateinit var etDeskripsi: EditText
    private lateinit var btnSimpanTransaksi: Button
    private lateinit var btnKembali: Button
    
    private lateinit var databaseHelper: DatabaseHelper
    private var tanggalTerpilih: Date = Date()
    private val formatTanggal = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        inisialisasiKomponen()
        aturSpinnerJenisTransaksi()
        aturEventListener()
        
        // Set tanggal default ke hari ini
        tvTanggal.text = formatTanggal.format(tanggalTerpilih)
        tvTanggal.setTextColor(ContextCompat.getColor(this, android.R.color.black))
    }

    private fun inisialisasiKomponen() {
        etNominal = findViewById(R.id.etNominal)
        spinnerJenisTransaksi = findViewById(R.id.spinnerJenisTransaksi)
        layoutTanggal = findViewById(R.id.layoutTanggal)
        tvTanggal = findViewById(R.id.tvTanggal)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        btnSimpanTransaksi = findViewById(R.id.btnSimpanTransaksi)
        btnKembali = findViewById(R.id.btnKembali)
        
        databaseHelper = DatabaseHelper(this)
    }

    private fun aturSpinnerJenisTransaksi() {
        val jenisTransaksi = arrayOf("Pengeluaran", "Pemasukan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisTransaksi)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenisTransaksi.adapter = adapter
    }

    private fun aturEventListener() {
        // Event listener untuk pemilihan tanggal
        layoutTanggal.setOnClickListener {
            tampilkanDatePicker()
        }

        // Event listener untuk tombol simpan
        btnSimpanTransaksi.setOnClickListener {
            simpanTransaksi()
        }

        // Event listener untuk tombol kembali
        btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun tampilkanDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = tanggalTerpilih

        val datePickerDialog = DatePickerDialog(
            this,
            { _, tahun, bulan, hari ->
                val calendarBaru = Calendar.getInstance()
                calendarBaru.set(tahun, bulan, hari)
                tanggalTerpilih = calendarBaru.time
                tvTanggal.text = formatTanggal.format(tanggalTerpilih)
                tvTanggal.setTextColor(ContextCompat.getColor(this@TransaksiActivity, android.R.color.black))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.show()
    }

    private fun simpanTransaksi() {
        val nominalText = etNominal.text.toString().trim()
        val deskripsi = etDeskripsi.text.toString().trim()
        val jenisTransaksiTerpilih = spinnerJenisTransaksi.selectedItemPosition

        // Validasi input
        if (nominalText.isEmpty()) {
            etNominal.error = "Nominal harus diisi"
            etNominal.requestFocus()
            return
        }

        if (deskripsi.isEmpty()) {
            etDeskripsi.error = "Deskripsi harus diisi"
            etDeskripsi.requestFocus()
            return
        }

        val nominal = try {
            nominalText.toDouble()
        } catch (e: NumberFormatException) {
            etNominal.error = "Nominal tidak valid"
            etNominal.requestFocus()
            return
        }

        if (nominal <= 0) {
            etNominal.error = "Nominal harus lebih dari 0"
            etNominal.requestFocus()
            return
        }

        // Tentukan jenis transaksi
        val jenisTransaksi = if (jenisTransaksiTerpilih == 0) {
            TransactionType.EXPENSE // Pengeluaran
        } else {
            TransactionType.INCOME // Pemasukan
        }

        // Buat objek transaksi
        val transaksi = Transaction(
            amount = nominal,
            description = deskripsi,
            category = if (jenisTransaksi == TransactionType.EXPENSE) "Pengeluaran" else "Pemasukan",
            type = jenisTransaksi,
            date = tanggalTerpilih
        )

        // Simpan ke database
        val hasilSimpan = databaseHelper.saveTransaction(transaksi)
        
        if (hasilSimpan != -1L) {
            Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
            kembaliKeMenuUtama()
        } else {
            Toast.makeText(this, "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun kembaliKeMenuUtama() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun bersihkanForm() {
        etNominal.text.clear()
        etDeskripsi.text.clear()
        spinnerJenisTransaksi.setSelection(0)
        tanggalTerpilih = Date()
        tvTanggal.text = formatTanggal.format(tanggalTerpilih)
        tvTanggal.setTextColor(ContextCompat.getColor(this, android.R.color.black))
    }
}
