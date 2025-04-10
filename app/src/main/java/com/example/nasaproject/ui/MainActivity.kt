package com.example.nasaproject.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nasaproject.databinding.ActivityMainBinding
import com.example.nasaproject.network.NasaService
import com.example.nasaproject.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var nasaService: NasaService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Retrofit Service
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nasaService = retrofit.create(NasaService::class.java)

        // Set up button click listener
        binding.buttonFetch.setOnClickListener {
            val date = binding.editTextDate.text.toString()
            fetchApod(date)
        }
    }

    private fun fetchApod(date: String) {
        lifecycleScope.launch {
            try {
                // Switch to IO dispatcher for network call
                val response = withContext(Dispatchers.IO) {
                    nasaService.getApod(Constants.API_KEY, date)
                }

                // Update UI on Main dispatcher
                withContext(Dispatchers.Main) {
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