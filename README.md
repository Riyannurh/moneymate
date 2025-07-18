# MoneyMate - Personal Finance Tracker

MoneyMate adalah aplikasi Android untuk mengelola keuangan pribadi dengan fitur pencatatan transaksi, riwayat, dan profil pengguna.

## 🚀 Fitur Utama

### 📱 Menu Utama
- **Transaksi**: Input transaksi baru (pemasukan/pengeluaran)
- **Riwayat**: Melihat semua transaksi yang telah dicatat
- **Profile**: Mengelola profil pengguna dan melihat ringkasan keuangan
- **Quit**: Keluar dari aplikasi

### 💰 Fitur Transaksi
- Input nominal transaksi
- Dropdown jenis transaksi (Pengeluaran/Pemasukan)
- Pemilihan tanggal dengan DatePicker
- Deskripsi transaksi
- Validasi input form
- Auto kembali ke menu utama setelah simpan

### 📊 Fitur Riwayat
- Menampilkan semua transaksi dari database
- Edit dan hapus transaksi
- Export ke PDF
- Floating Action Button untuk input cepat

### 👤 Fitur Profile
- Informasi profil pengguna (Nama, NIM, Email)
- Upload foto profil dari galeri
- Ringkasan keuangan (Total pemasukan, pengeluaran, saldo)
- Target bulanan dan limit pengeluaran

## 🛠️ Teknologi yang Digunakan

- **Language**: Kotlin
- **Database**: SQLite dengan DatabaseHelper
- **UI**: XML Layouts dengan Material Design
- **Architecture**: Activity-based dengan data classes
- **Storage**: Internal storage untuk foto profil

## 📁 Struktur Project

```
app/src/main/java/com/example/moneymate/
├── MainActivity.kt                 # Menu utama aplikasi
├── TransaksiActivity.kt           # Form input transaksi
├── RiwayatActivity.kt             # Daftar riwayat transaksi
├── ProfileActivity.kt             # Profil pengguna
├── EditProfileActivity.kt         # Edit profil
├── splash_activity.kt             # Splash screen
├── adapter/
│   └── TransactionAdapter.kt      # Adapter untuk RecyclerView
└── data/
    ├── DatabaseHelper.kt          # Helper untuk SQLite
    ├── Transaction.kt             # Data class transaksi
    ├── UserProfile.kt             # Data class profil
    └── FinancialSummary.kt        # Data class ringkasan keuangan
```

## 🎨 Design Features

- Background custom dengan gambar
- Rounded corners untuk UI elements
- Color scheme yang konsisten
- Icon yang sesuai untuk setiap menu
- Responsive layout design

## 📱 Screenshots

*Screenshots akan ditambahkan setelah testing aplikasi*

## 🔧 Instalasi dan Setup

1. Clone repository ini:
```bash
git clone https://github.com/Riyannurh/moneymate.git
```

2. Buka project di Android Studio

3. Sync project dengan Gradle files

4. Run aplikasi di emulator atau device Android

## 📋 Requirements

- Android Studio Arctic Fox atau lebih baru
- Android SDK API 21 (Android 5.0) atau lebih tinggi
- Kotlin 1.8+
- Gradle 7.0+

## 🤝 Kontribusi

Kontribusi selalu diterima! Silakan:

1. Fork repository ini
2. Buat branch fitur baru (`git checkout -b feature/AmazingFeature`)
3. Commit perubahan (`git commit -m 'Add some AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buat Pull Request

## 📄 License

Project ini menggunakan MIT License. Lihat file `LICENSE` untuk detail lebih lanjut.

## 👨‍💻 Developer

Dikembangkan dengan ❤️ oleh [Riyannurh](https://github.com/Riyannurh)

## 📞 Kontak

Jika ada pertanyaan atau saran, silakan hubungi melalui:
- GitHub Issues: [MoneyMate Issues](https://github.com/Riyannurh/moneymate/issues)
- Email: [Your Email]

---

⭐ Jangan lupa berikan star jika project ini membantu Anda!
