package com.example.moneymate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymate.R
import com.example.moneymate.data.Transaction
import com.example.moneymate.data.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction> = emptyList(),
    private val onEditClick: (Transaction) -> Unit = {},
    private val onDeleteClick: (Transaction) -> Unit = {}
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionType: TextView = itemView.findViewById(R.id.tvTransactionType)
        val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        val tvTransactionDescription: TextView = itemView.findViewById(R.id.tvTransactionDescription)
        val tvTransactionCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        val btnEditTransaction: Button = itemView.findViewById(R.id.btnEditTransaction)
        val btnDeleteTransaction: Button = itemView.findViewById(R.id.btnDeleteTransaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.itemView.context
        
        // Format currency
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        
        // Format date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        // Set transaction type and color
        when (transaction.type) {
            TransactionType.INCOME -> {
                holder.tvTransactionType.text = "PEMASUKAN"
                holder.tvTransactionType.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            }
            TransactionType.EXPENSE -> {
                holder.tvTransactionType.text = "PENGELUARAN"
                holder.tvTransactionType.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
        }
        
        // Set other fields
        holder.tvTransactionDate.text = dateFormat.format(transaction.date)
        holder.tvTransactionAmount.text = currencyFormat.format(transaction.amount)
        holder.tvTransactionDescription.text = transaction.description
        holder.tvTransactionCategory.text = "Kategori: ${transaction.category}"

        // Set click listeners for buttons
        holder.btnEditTransaction.setOnClickListener {
            onEditClick(transaction)
        }

        holder.btnDeleteTransaction.setOnClickListener {
            onDeleteClick(transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size

    /**
     * Update the transaction list and refresh the RecyclerView
     */
    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    /**
     * Add a new transaction to the list
     */
    fun addTransaction(transaction: Transaction) {
        val mutableList = transactions.toMutableList()
        mutableList.add(0, transaction) // Add to the beginning of the list
        transactions = mutableList
        notifyItemInserted(0)
    }

    /**
     * Clear all transactions
     */
    fun clearTransactions() {
        transactions = emptyList()
        notifyDataSetChanged()
    }
}
