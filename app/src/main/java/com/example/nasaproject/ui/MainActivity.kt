package com.example.nasaproject.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nasaproject.data.database.ApodDatabase
import com.example.nasaproject.data.database.ApodEntity
import com.example.nasaproject.databinding.ActivityMainBinding
import com.example.nasaproject.network.NasaService
import com.example.nasaproject.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var nasaService: NasaService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize View Bindin

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.textViewExplanation.movementMethod = ScrollingMovementMethod()
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Initialize Retrofit Service
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nasaService = retrofit.create(NasaService::class.java)

        binding.editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Set up button click listener
        binding.buttonFetch.setOnClickListener {
            val date = binding.editTextDate.text.toString()
            val validatedDate = validateAndFormatDate(date)

            if (validatedDate != null) {
                fetchApod(validatedDate)
            }
        }

        binding.buttonViewAll.setOnClickListener {
            val intent = Intent(this, AllDatesActivity::class.java)
            startActivity(intent)
        }

        val selectedDate = intent.getStringExtra("SELECTED_DATE")
        if (selectedDate != null) {
            binding.editTextDate.setText(selectedDate)
            fetchApod(selectedDate)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Format the date
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDayOfMonth
                )
                binding.editTextDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        // Set max date to today
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Set minimum date (NASA APOD start date)
        val minDate = Calendar.getInstance()
        minDate.set(1995, 5, 16) // June 16, 1995
        datePickerDialog.datePicker.minDate = minDate.timeInMillis

        datePickerDialog.show()
    }

    private fun validateAndFormatDate(inputDate: String): String? {
        // Remove any non-digit characters
        val cleanDate = inputDate.replace("[^0-9]".toRegex(), "")

        // Check if input is valid
        if (cleanDate.length != 8) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            return null
        }

        try {
            // Parse the input
            val year = cleanDate.substring(0, 4).toInt()
            val month = cleanDate.substring(4, 6).toInt()
            val day = cleanDate.substring(6, 8).toInt()

            // Validate date components
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show()
                return null
            }

            // Format to YYYY-MM-DD
            return String.format("%04d-%02d-%02d", year, month, day)
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid date input", Toast.LENGTH_SHORT).show()
            return null
        }
    }



    private fun fetchApod(date: String) {
        lifecycleScope.launch {
            try {
                // Switch to IO dispatcher for network call
                val response = withContext(Dispatchers.IO) {
                    nasaService.getApod(Constants.API_KEY, date)
                }
                //Database structure for data
                val entity = ApodEntity(
                    date = response.date,
                    title = response.title,
                    explanation = response.explanation,
                    url = response.url
                )
                //Put data in database
                val database = ApodDatabase.getDatabase(this@MainActivity)
                database.apodDao().insert(entity)

                // Update UI on Main dispatcher
                withContext(Dispatchers.Main) {
                    // Make cards visible
                    binding.imageCardView.visibility = View.VISIBLE
                    binding.infoCardView.visibility = View.VISIBLE

                    // Load image using Picasso
                    Picasso.get()
                        .load(response.url)
                        .into(binding.imageView)

                    // Set title and explanation
                    binding.textViewTitle.text = response.title
                    binding.textViewExplanation.text = response.explanation
                }
            } catch (e: Exception) {
                // Handle network or parsing errors
                Toast.makeText(
                    this@MainActivity,
                    "Error fetching APOD: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }
}