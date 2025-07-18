package com.example.moneymate

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set layout
        setContentView(R.layout.activity_main)

        // Atur padding sesuai system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi menu navigasi
        aturMenuNavigasi()
    }

    private fun aturMenuNavigasi() {
        // Menu Transaksi
        findViewById<LinearLayout>(R.id.menuTransaksi).setOnClickListener {
            navigasiKeActivity(TransaksiActivity::class.java)
        }

        // Menu Riwayat
        findViewById<LinearLayout>(R.id.menuRiwayat).setOnClickListener {
            navigasiKeActivity(RiwayatActivity::class.java)
        }

        // Menu Profil
        findViewById<LinearLayout>(R.id.menuProfil).setOnClickListener {
            navigasiKeActivity(ProfileActivity::class.java)
        }

        // Menu Quit
        findViewById<LinearLayout>(R.id.menuQuit).setOnClickListener {
            keluarAplikasi()
        }
    }

    private fun navigasiKeActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun keluarAplikasi() {
        finishAffinity() // Menutup semua activity dan keluar dari aplikasi
    }
}