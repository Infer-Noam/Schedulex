package com.example.kashio.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow



@Entity
data class Time(
    val time: String = "1400-01-01-2024",
    val title: String = "i love noam",
    val text: String = "the price for success is pain",
    val tag: String = "im driven by pain, not fear - some random blond finn"
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}


@Dao
interface TimeDao {
    @Query("SELECT * from time ORDER BY uid ASC")
    fun getAllTime(): Flow<List<Time>>

    @Query("SELECT * from time WHERE uid = :id")
    fun getTime(id: Int): Flow<Time>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTime(time: Time)

    @Update
    suspend fun updateTime(time: Time)

    @Delete
    suspend fun deleteTime(time: Time)
}
