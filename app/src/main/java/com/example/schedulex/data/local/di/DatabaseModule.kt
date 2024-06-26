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

package com.example.schedulex.data.local.di

import android.content.Context
import androidx.room.Room
import com.example.schedulex.data.local.database.AppDatabase
import com.example.schedulex.data.local.database.MIGRATION_2_3
import com.example.schedulex.data.local.database.TimeDao
import com.example.schedulex.data.local.database.TitleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideTitleDao(appDatabase: AppDatabase): TitleDao {
        return appDatabase.titleDao() // provide title data table
    }

    @Provides
    fun provideTimeDao(appDatabase: AppDatabase): TimeDao {
        return appDatabase.timeDao() // provide time data table
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase { // provide the app date base
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "DataBase"
        )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }
}
