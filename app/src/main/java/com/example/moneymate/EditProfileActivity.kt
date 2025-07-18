package com.example.moneymate

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moneymate.data.DatabaseHelper
import com.example.moneymate.data.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date

class EditProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userProfile: UserProfile? = null
    private var selectedImageUri: Uri? = null
    private var savedImagePath: String? = null

    // UI Components
    private lateinit var ivEditProfileImage: ImageView
    private lateinit var btnChangeImage: Button
    private lateinit var etEditName: EditText
    private lateinit var etEditNim: EditText
    private lateinit var etEditEmail: EditText
    private lateinit var etIncomeTarget: EditText
    private lateinit var etExpenseLimit: EditText
    private lateinit var btnCancelEdit: Button
    private lateinit var btnSaveProfile: Button

    // Activity Result Launcher for image picker
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                ivEditProfileImage.setImageURI(uri)
                saveImageToInternalStorage(uri)
            }
        }
    }

    // Alternative launcher using GetContent contract (no permission needed)
    private val getContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            ivEditProfileImage.setImageURI(it)
            saveImageToInternalStorage(it)
        }
    }

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Permission diperlukan untuk mengakses galeri", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Initialize UI components
        initViews()
        
        // Setup click listeners
        setupClickListeners()
        
        // Load current user profile data
        loadCurrentProfile()
    }

    private fun initViews() {
        ivEditProfileImage = findViewById(R.id.ivEditProfileImage)
        btnChangeImage = findViewById(R.id.btnChangeImage)
        etEditName = findViewById(R.id.etEditName)
        etEditNim = findViewById(R.id.etEditNim)
        etEditEmail = findViewById(R.id.etEditEmail)
        etIncomeTarget = findViewById(R.id.etIncomeTarget)
        etExpenseLimit = findViewById(R.id.etExpenseLimit)
        btnCancelEdit = findViewById(R.id.btnCancelEdit)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
    }

    private fun setupClickListeners() {
        btnCancelEdit.setOnClickListener {
            finish()
        }

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }

        btnChangeImage.setOnClickListener {
            // Menggunakan GetContent launcher yang tidak memerlukan permission
            openImagePickerModern()
        }
    }

    private fun loadCurrentProfile() {
        userProfile = dbHelper.getUserProfile()
        
        userProfile?.let { profile ->
            etEditName.setText(profile.name)
            etEditNim.setText(profile.nim)
            etEditEmail.setText(profile.email)
            etIncomeTarget.setText(profile.monthlyIncomeTarget.toString())
            etExpenseLimit.setText(profile.monthlyExpenseLimit.toString())

            // Load existing profile image if available
            profile.profileImagePath?.let { imagePath ->
                val imageFile = File(imagePath)
                if (imageFile.exists()) {
                    ivEditProfileImage.setImageURI(Uri.fromFile(imageFile))
                    savedImagePath = imagePath
                }
            }
        }
    }

    private fun checkPermissionAndOpenGallery() {
        // Untuk Android 13+ (API 33+) gunakan READ_MEDIA_IMAGES
        // Untuk Android 12 dan dibawah gunakan READ_EXTERNAL_STORAGE
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    /**
     * Metode modern untuk memilih gambar tanpa permission
     */
    private fun openImagePickerModern() {
        // Menggunakan GetContent contract yang tidak memerlukan permission
        getContentLauncher.launch("image/*")
    }

    /**
     * Metode lama dengan permission (sebagai backup)
     */
    private fun openImagePicker() {
        // Menggunakan ACTION_GET_CONTENT yang tidak memerlukan permission
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        // Fallback ke ACTION_PICK jika ACTION_GET_CONTENT tidak tersedia
        val chooserIntent = Intent.createChooser(intent, "Pilih Foto")
        imagePickerLauncher.launch(chooserIntent)
    }

    private fun saveImageToInternalStorage(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)

            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            savedImagePath = file.absolutePath
            Toast.makeText(this, "Foto berhasil disimpan", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menyimpan foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile() {
        val name = etEditName.text.toString().trim()
        val nim = etEditNim.text.toString().trim()
        val email = etEditEmail.text.toString().trim()
        val incomeTargetStr = etIncomeTarget.text.toString().trim()
        val expenseLimitStr = etExpenseLimit.text.toString().trim()

        // Validasi input
        when {
            name.isEmpty() -> {
                etEditName.error = "Nama tidak boleh kosong"
                etEditName.requestFocus()
                return
            }
            nim.isEmpty() -> {
                etEditNim.error = "NIM tidak boleh kosong"
                etEditNim.requestFocus()
                return
            }
            email.isEmpty() -> {
                etEditEmail.error = "Email tidak boleh kosong"
                etEditEmail.requestFocus()
                return
            }
            !isValidEmail(email) -> {
                etEditEmail.error = "Format email tidak valid"
                etEditEmail.requestFocus()
                return
            }
        }

        // Parse numeric values
        val incomeTarget = try {
            if (incomeTargetStr.isEmpty()) 0.0 else incomeTargetStr.toDouble()
        } catch (e: NumberFormatException) {
            etIncomeTarget.error = "Format angka tidak valid"
            etIncomeTarget.requestFocus()
            return
        }

        val expenseLimit = try {
            if (expenseLimitStr.isEmpty()) 0.0 else expenseLimitStr.toDouble()
        } catch (e: NumberFormatException) {
            etExpenseLimit.error = "Format angka tidak valid"
            etExpenseLimit.requestFocus()
            return
        }

        // Validasi nilai negatif
        if (incomeTarget < 0) {
            etIncomeTarget.error = "Target pemasukan tidak boleh negatif"
            etIncomeTarget.requestFocus()
            return
        }

        if (expenseLimit < 0) {
            etExpenseLimit.error = "Batas pengeluaran tidak boleh negatif"
            etExpenseLimit.requestFocus()
            return
        }

        // Update atau create profile
        val updatedProfile = if (userProfile != null) {
            userProfile!!.copy(
                name = name,
                nim = nim,
                email = email,
                profileImagePath = savedImagePath ?: userProfile!!.profileImagePath,
                monthlyIncomeTarget = incomeTarget,
                monthlyExpenseLimit = expenseLimit,
                updatedAt = Date()
            )
        } else {
            UserProfile(
                name = name,
                nim = nim,
                email = email,
                profileImagePath = savedImagePath,
                monthlyIncomeTarget = incomeTarget,
                monthlyExpenseLimit = expenseLimit
            )
        }

        // Save to database
        val result: Long = if (userProfile != null) {
            dbHelper.updateUserProfile(updatedProfile).toLong()
        } else {
            dbHelper.saveUserProfile(updatedProfile)
        }

        if (result > 0) {
            Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
