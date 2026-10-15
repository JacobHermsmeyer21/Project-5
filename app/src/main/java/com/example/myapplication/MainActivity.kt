package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var entryDao: WaterEntryDao
    private lateinit var entryAdapter: WaterEntryAdapter
    private lateinit var todayTotal: TextView
    private lateinit var emptyState: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val root = findViewById<View>(R.id.main)
        val leftPadding = root.paddingLeft
        val topPadding = root.paddingTop
        val rightPadding = root.paddingRight
        val bottomPadding = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                leftPadding + systemBars.left,
                topPadding + systemBars.top,
                rightPadding + systemBars.right,
                bottomPadding + systemBars.bottom
            )
            insets
        }

        entryDao = WaterDatabase.getInstance(this).waterEntryDao()
        todayTotal = findViewById(R.id.todayTotal)
        emptyState = findViewById(R.id.emptyState)
        entryAdapter = WaterEntryAdapter()
        findViewById<RecyclerView>(R.id.entryList).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = entryAdapter
        }
        findViewById<MaterialButton>(R.id.addEntryButton).setOnClickListener { showAddEntryDialog() }
        loadEntries()
    }

    private fun showAddEntryDialog() {
        val content = layoutInflater.inflate(R.layout.dialog_add_entry, null)
        val amountInput = content.findViewById<TextInputEditText>(R.id.amountInput)
        val noteInput = content.findViewById<TextInputEditText>(R.id.noteInput)
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_entry_title)
            .setView(content)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save_entry, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val amount = amountInput.text?.toString()?.trim()?.toIntOrNull()
                if (amount == null || amount !in 1..10000) {
                    amountInput.error = getString(R.string.invalid_amount)
                    return@setOnClickListener
                }

                dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = false
                val entry = WaterEntry(
                    amount,
                    noteInput.text?.toString()?.trim().orEmpty(),
                    System.currentTimeMillis()
                )
                databaseExecutor.execute {
                    entryDao.insert(entry)
                    val entries = entryDao.getAll()
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            renderEntries(entries)
                            dialog.dismiss()
                            Toast.makeText(this, R.string.entry_saved, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        dialog.show()
    }

    private fun loadEntries() {
        databaseExecutor.execute {
            val entries = entryDao.getAll()
            runOnUiThread {
                if (!isFinishing && !isDestroyed) renderEntries(entries)
            }
        }
    }

    private fun renderEntries(entries: List<WaterEntry>) {
        entryAdapter.submitList(entries)
        emptyState.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE

        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val total = entries.filter { it.recordedAt >= startOfToday }
            .sumOf { it.amountMl.toLong() }
        todayTotal.text = getString(R.string.today_total_format, total)
    }

    companion object {
        private val databaseExecutor = Executors.newSingleThreadExecutor()
    }
}
