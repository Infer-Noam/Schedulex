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

package com.example.kashio.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.kashio.data.local.database.Time
import com.example.kashio.data.local.database.TimeDao
import com.example.kashio.data.local.database.Title
import com.example.kashio.data.local.database.TitleDao
import com.example.kashio.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface Repository {
    val dataTitleTypes: Flow<List<String>>

    suspend fun add(title: String, color: String)
}

class DefaultRepository @Inject constructor(
    private val titleDao: TitleDao,
) : Repository {


    override val dataTitleTypes: Flow<List<String>> =
        titleDao.getTitle().map { items -> items.map { it.title } }
    override suspend fun add(title: String, color: String) {
       titleDao.insertTitle(Title(title, color))
    }
}

interface TimeRepository {

    fun getAllTimesForDate(date: String): Flow<List<Time>>

    suspend fun insert(time: Time)

    suspend fun delete(id: Int)

    suspend fun getTitles(): Flow<List<Title>>

    suspend fun deleteTitle(title: Title)

    suspend fun insertTitle(title: Title)

    suspend fun getTimeByTitle(title: String): Flow<List<Time>>
     fun getTimeBetweenDates(startDate: String, endDate: String): Flow<List<Time>>
}

class DefaultTimeRepository @Inject constructor(
    private val timeDao: TimeDao,
    private val titleDao: TitleDao
) : TimeRepository {

    override fun getAllTimesForDate(date: String): Flow<List<Time>> =
        timeDao.getAllTime(date)

    override suspend fun insert(time: Time) {
        timeDao.insertTime(time)
    }

    override suspend fun delete(id: Int) {
        timeDao.deleteTime(id)
    }

    override suspend fun insertTitle(title: Title) {
        titleDao.insertOrReplaceTitle(title)
    }

    override suspend fun deleteTitle(title: Title) {
        titleDao.deleteTitle(title)
    }

    override suspend fun getTitles() : (Flow<List<Title>>){
        return titleDao.getTitle()
    }

    override suspend fun getTimeByTitle(title: String): Flow<List<Time>> {
        return timeDao.getAllTimeByTitle(title)
    }

    override fun getTimeBetweenDates(startDate: String, endDate: String): Flow<List<Time>> {
        return timeDao.timeBetween(startDate, endDate)
    }

}

interface SettingsRepository {
    val chunksDivider: Flow<Int>
    val play: Flow<String>

    suspend fun updateChunksDivider(newDivider: Int)

    suspend fun updatePlay(newPlay: String)

    suspend fun returnPlay(): String
}

class DefaultSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    companion object {
        val CHUNKS_DIVIDER_KEY = intPreferencesKey("chunks_divider")
        val PLAY_KEY = stringPreferencesKey("play")
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    override val chunksDivider: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[CHUNKS_DIVIDER_KEY] ?: 0
        }

    override val play: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[PLAY_KEY] ?: ""
        }


    override suspend fun updateChunksDivider(newDivider: Int) {
        dataStore.edit { settings ->
            settings[CHUNKS_DIVIDER_KEY] = newDivider
        }
    }

    override suspend fun updatePlay(newPlay: String) {
        dataStore.edit { settings ->
            settings[PLAY_KEY] = newPlay
        }
    }

    override suspend fun returnPlay(): String {
        var tempPlay =  ""

        dataStore.edit { settings ->
            tempPlay = settings[PLAY_KEY].toString()
        }
        return  tempPlay
    }
}



