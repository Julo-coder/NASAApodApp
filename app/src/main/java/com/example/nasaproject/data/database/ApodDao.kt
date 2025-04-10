package com.example.nasaproject.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ApodDao {
    @Query("SELECT * FROM apod") // Replace with your actual table name.
    suspend fun getAllApod(): List<ApodEntity> // Must return List<ApodEntity>

    @Query("SELECT * FROM apod WHERE date = :date LIMIT 1") // Replace with your actual table name.
    suspend fun getApodByDate(date: String): ApodEntity? // Must return ApodEntity or null

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apodEntity: ApodEntity) // Accepts ApodEntity as parameter
}