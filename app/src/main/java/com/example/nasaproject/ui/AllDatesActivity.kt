package com.example.nasaproject.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nasaproject.data.database.ApodDatabase
import com.example.nasaproject.databinding.ActivityAllDatesBinding
import kotlinx.coroutines.launch

class AllDatesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllDatesBinding
    private lateinit var adapter: DatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        loadDates()
    }

    private fun setupRecyclerView() {
        adapter = DatesAdapter(
            onDateClick = { date ->
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("SELECTED_DATE", date)
                }
                startActivity(intent)
            },
            onDateLongClick = { date ->
                showDeleteConfirmation(date)
            }
        )
        binding.recyclerViewDates.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDates.adapter = adapter
    }

    private fun loadDates() {
        lifecycleScope.launch {
            val database = ApodDatabase.getDatabase(this@AllDatesActivity)
            val dates = database.apodDao().getAllDates()
            adapter.submitList(dates)
        }
    }

    private fun showDeleteConfirmation(date: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete APOD")
            .setMessage("Do you want to delete this entry from ${date}?")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteApod(date)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteApod(date: String) {
        lifecycleScope.launch {
            try {
                val database = ApodDatabase.getDatabase(this@AllDatesActivity)
                database.apodDao().deleteByDate(date)

                // Refresh the list
                val updatedDates = database.apodDao().getAllDates()
                adapter.submitList(updatedDates)

                Toast.makeText(
                    this@AllDatesActivity,
                    "Deleted APOD for $date",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@AllDatesActivity,
                    "Error deleting: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}