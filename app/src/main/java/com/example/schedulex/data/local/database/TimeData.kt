package com.example.schedulex.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow



@Entity
data class Time(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0, // each block has a unique id

    val date: String, // each block has a specific date
    val title: String, // each block can have a title
    val text: String,  // each block can have text
    val notification: String,  // each block can have a notification in this format: hh-mm-dd-mm-yyyy
    val startTime: String, // each block has a start time
    val endTime: String, // each block has a end time
    val color: String // // each block can have a color
) // a data class of a time block


@Dao
interface TimeDao {
    @Query("SELECT * from time WHERE uid = :id") // returns a time object by id
    fun getTime(id: Int): Flow<Time> // function

    @Query("SELECT * from time WHERE date = :date ORDER BY (endTime - startTime) ASC") // returns all of the time blocks in the table
    fun getAllTime(date: String): Flow<List<Time>>

    @Query("SELECT * from time WHERE title = :title") // returns all time blocks that have a specific title
    fun getAllTimeByTitle(title: String): Flow<List<Time>>

    @Query("SELECT * FROM time WHERE date = :date AND startTime = :startTime AND endTime = :endTime LIMIT 1") // returns a time block by date, start time ad end time
    suspend fun getTimeByDateAndTime(date: String, startTime: String, endTime: String): Time?

    @Insert(onConflict = OnConflictStrategy.REPLACE) // insert or replace a time block
    suspend fun insertOrReplaceTime(time: Time)

    @Transaction
    suspend fun insertTime(time: Time) { // insert a time block
        val existingTime = getTimeByDateAndTime(time.date, time.startTime, time.endTime)
        if (existingTime != null) {
            time.uid = existingTime.uid
        }
        insertOrReplaceTime(time)
    }
    @Update
    suspend fun updateTime(time: Time) // update a time block

    @Query("DELETE FROM time WHERE uid = :id") // delete a time block
    suspend fun deleteTime(id: Int)

    @Query("SELECT * FROM time WHERE date BETWEEN :startTime AND :endTime") // returns all time blocks within a range of dates
    fun timeBetween(startTime: String, endTime: String): Flow<List<Time>>

}
