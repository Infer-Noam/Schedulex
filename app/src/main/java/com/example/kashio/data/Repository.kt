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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.kashio.data.local.database.Item
import com.example.kashio.data.local.database.ItemDao
import com.example.kashio.data.local.database.Time
import com.example.kashio.data.local.database.TimeDao
import javax.inject.Inject

interface Repository {
    val dataItemTypes: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultRepository @Inject constructor(
    private val itemDao: ItemDao,
) : Repository {


    override val dataItemTypes: Flow<List<String>> =
        itemDao.getDataItemTypes().map { items -> items.map { it.name } }
    override suspend fun add(name: String) {
        itemDao.insertDataItemType(Item(name = name))
    }
}

interface TimeRepository {

    val times: Flow<List<Time>>

    suspend fun insert(time: Time)
}

class DefaultTimeRepository @Inject constructor(
    private val timeDao: TimeDao
) : TimeRepository {

    override val times: Flow<List<Time>> =
        timeDao.getAllTime()

    override suspend fun insert(time: Time) {
        timeDao.insertTime(time)
        }
    }
