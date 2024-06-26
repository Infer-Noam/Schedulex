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

package com.example.schedulex.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.schedulex.data.DefaultSettingsRepository
import com.example.schedulex.data.DefaultTimeRepository
import com.example.schedulex.data.Repository
import com.example.schedulex.data.SettingsRepository
import com.example.schedulex.data.TimeRepository
import com.example.schedulex.dataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

   /* @Singleton
    @Binds
    fun bindsDataItemTypeRepository(
        dataItemTypeRepository: DefaultRepository
    ): Repository */

    @Singleton
    @Binds
    fun bindsTimeRepository(
        timeRepository: DefaultTimeRepository
    ): TimeRepository // initiate the time repository

    @Module
    @InstallIn(SingletonComponent::class)
    object DataStoreModule {

        @Singleton
        @Provides
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.dataStore
        }  // initiate the data store / shared preferences
    }


    @Singleton
    @Binds
    fun bindsSettingsRepository(
        settingsRepository: DefaultSettingsRepository
    ): SettingsRepository  // initiate the settings repository

}

class FakeRepository @Inject constructor() : Repository {
    override val dataTitleTypes: Flow<List<String>> = flowOf(fakeDataItemTypes)

    override suspend fun add(title: String, color: String) {
        throw NotImplementedError()
    }
}

val fakeDataItemTypes = listOf("One", "Two", "Three")
