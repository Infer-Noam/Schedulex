package com.example.kashio.data.local.database

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
    var uid: Int = 0,

    val date: String,
    val title: String,
    val text: String,
    val tag: String,
    val startTime: String,
    val endTime: String,
    val color: String
)


@Dao
interface TimeDao {
    //@Query("SELECT * from time ORDER BY uid ASC")
    //fun getAllTime(): Flow<List<Time>>

    @Query("SELECT * from time WHERE uid = :id")
    fun getTime(id: Int): Flow<Time>

    //@Query("SELECT * from time WHERE date = :date ORDER BY date ASC")
  //  fun getTimeByDate(date: String): Flow<List<Time>>

    @Query("SELECT * from time WHERE date = :date ORDER BY (endTime - startTime) ASC")
    fun getAllTime(date: String): Flow<List<Time>>

    @Query("SELECT * from time WHERE title = :title")
    fun getAllTimeByTitle(title: String): Flow<List<Time>>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
   //@Upsert
    //suspend fun insertTime(time: Time)

    @Query("SELECT * FROM time WHERE date = :date AND startTime = :startTime AND endTime = :endTime LIMIT 1")
    suspend fun getTimeByDateAndTime(date: String, startTime: String, endTime: String): Time?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceTime(time: Time)

    @Transaction
    suspend fun insertTime(time: Time) {
        val existingTime = getTimeByDateAndTime(time.date, time.startTime, time.endTime)
        if (existingTime != null) {
            time.uid = existingTime.uid
        }
        insertOrReplaceTime(time)
    }
    @Update
    suspend fun updateTime(time: Time)

    @Query("DELETE FROM time WHERE uid = :id")
    suspend fun deleteTime(id: Int)

    @Query("SELECT * FROM time WHERE date BETWEEN :startTime AND :endTime")
    fun timeBetween(startTime: String, endTime: String): Flow<List<Time>>

}
