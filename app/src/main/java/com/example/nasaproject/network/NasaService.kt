package com.example.nasaproject.network

import com.example.nasaproject.data.model.ApodResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaService {
    @GET("planetary/apod")
    suspend fun getApod(@Query("api_key") apiKey: String, @Query("date") date: String): ApodResponse
}