package com.example.moneymate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymate.data.DatabaseHelper
import com.example.moneymate.data.UserProfile
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userProfile: UserProfile? = null
    
    // UI Components
    private lateinit var ivProfileImage: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserNim: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvMemberSince: TextView
    private lateinit var tvCurrentBalance: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvTransactionCount: TextView
    private lateinit var tvIncomeTarget: TextView
    private lateinit var tvExpenseLimit: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnViewTransactions: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Initialize UI components
        initViews()
        
        // Setup click listeners
        setupClickListeners()
        
        // Load user profile data
        loadUserProfile()
        
        // Load financial summary
        loadFinancialSummary()
    }

    private fun initViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserNim = findViewById(R.id.tvUserNim)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvMemberSince = findViewById(R.id.tvMemberSince)
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        tvTransactionCount = findViewById(R.id.tvTransactionCount)
        tvIncomeTarget = findViewById(R.id.tvIncomeTarget)
        tvExpenseLimit = findViewById(R.id.tvExpenseLimit)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnViewTransactions = findViewById(R.id.btnViewTransactions)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupClickListeners() {
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        btnViewTransactions.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadUserProfile() {
        userProfile = dbHelper.getUserProfile()
        
        if (userProfile == null) {
            // Jika belum ada profil, buat profil default
            val defaultProfile = UserProfile(
                name = "Pengguna MoneyMate",
                nim = "123456789",
                email = "user@moneymate.com",
                monthlyIncomeTarget = 5000000.0,
                monthlyExpenseLimit = 3000000.0
            )
            
            val id = dbHelper.saveUserProfile(defaultProfile)
            userProfile = defaultProfile.copy(id = id)
        }
        
        // Display user profile data
        userProfile?.let { profile ->
            tvUserName.text = profile.name
            tvUserNim.text = "NIM: ${profile.nim}"
            tvUserEmail.text = profile.email

            // Load and display profile image
            profile.profileImagePath?.let { imagePath ->
                val imageFile = File(imagePath)
                if (imageFile.exists()) {
                    ivProfileImage.setImageURI(Uri.fromFile(imageFile))
                } else {
                    // Set default image if file doesn't exist
                    ivProfileImage.setImageResource(R.drawable.logo_baru1)
                }
            } ?: run {
                // Set default image if no image path
                ivProfileImage.setImageResource(R.drawable.logo_baru1)
            }

            // Format date for member since
            val dateFormat = SimpleDateFormat("MMM yyyy", Locale("id", "ID"))
            val memberSinceText = "Member sejak: ${dateFormat.format(profile.createdAt)}"
            tvMemberSince.text = memberSinceText

            // Format currency for targets
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            tvIncomeTarget.text = currencyFormat.format(profile.monthlyIncomeTarget)
            tvExpenseLimit.text = currencyFormat.format(profile.monthlyExpenseLimit)
        }
    }

    private fun loadFinancialSummary() {
        val summary = dbHelper.getFinancialSummary()
        
        // Format currency values
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvCurrentBalance.text = currencyFormat.format(summary.currentBalance)
        tvTotalIncome.text = currencyFormat.format(summary.totalIncome)
        tvTotalExpense.text = currencyFormat.format(summary.totalExpense)
        
        // Format transaction count
        tvTransactionCount.text = "${summary.transactionCount} transaksi"
    }

    override fun onResume() {
        super.onResume()
        // Reload data when returning to this activity
        loadUserProfile()
        loadFinancialSummary()
    }
}
