package com.example.nasaproject.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apod")
data class ApodEntity(
    @PrimaryKey val date: String,
    val title: String,
    val url: String,
    val explanation: String
)