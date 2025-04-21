package com.example.nasaproject.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ApodDao {
    @Query("SELECT * FROM apod")
    suspend fun getAllApod(): List<ApodEntity>

    @Query("SELECT * FROM apod WHERE date = :date LIMIT 1")
    suspend fun getApodByDate(date: String): ApodEntity?

    @Query("SELECT date FROM apod ORDER BY date DESC") // New query for just dates
    suspend fun getAllDates(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apodEntity: ApodEntity)

    @Query("SELECT COUNT(*) FROM apod")
    suspend fun getCount(): Int

    @Delete
    suspend fun delete(apodEntity: ApodEntity)

    @Query("DELETE FROM apod WHERE date = :date")
    suspend fun deleteByDate(date: String)
}