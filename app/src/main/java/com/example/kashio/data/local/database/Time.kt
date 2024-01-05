/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    val name: String,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Entity
data class TimeAc(
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
    @Query("SELECT * FROM time ORDER BY uid DESC LIMIT 10")
    fun getDataItemTypes(): Flow<List<Time>>

    @Insert
    suspend fun insertDataItemType(item: Time)

}

@Dao
interface TimeDaoAc {
    @Query("SELECT * from timeac ORDER BY time ASC")
    fun getAllTime(): Flow<List<TimeAc>>

    @Query("SELECT * from timeac WHERE uid = :id")
    fun getTime(id: Int): Flow<TimeAc>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTime(time: TimeAc)

    @Update
    suspend fun updateTime(time: TimeAc)

    @Delete
    suspend fun deleteTime(time: TimeAc)
}
