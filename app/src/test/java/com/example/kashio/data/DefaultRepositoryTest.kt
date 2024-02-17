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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.kashio.data.local.database.Item
import com.example.kashio.data.local.database.Time
import com.example.kashio.data.local.database.ItemDao
import com.example.kashio.data.local.database.TimeDao

/**
 * Unit tests for [DefaultRepository].
 */
// TODO: Remove when stable
class DefaultRepositoryTest {

    @Test
    fun dataItemTypes_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultRepository(FakeItemDao())

        repository.add("Repository")

        assertEquals(repository.dataTitleTypes.first().size, 1)
    }

}

private class FakeItemDao : ItemDao {

    private val data = mutableListOf<Item>()

    override fun getDataItemTypes(): Flow<List<Item>> = flow {
        emit(data)
    }

    override suspend fun insertDataItemType(item: Item) {
        data.add(0, item)
    }
}

private class FakeTimeDao : TimeDao {

    override fun getAllTime(): Flow<List<Time>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTime(time: Time) {
        TODO("Not yet implemented")
    }

    override fun getTime(id: Int): Flow<Time> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTime(time: Time) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTime(time: Time) {

    }


}