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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.kashio.data.local.database.Time
import com.example.kashio.data.local.database.TimeAc
import com.example.kashio.data.local.database.TimeDao
import com.example.kashio.data.local.database.TimeDaoAc

/**
 * Unit tests for [DefaultRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultRepositoryTest {

    @Test
    fun dataItemTypes_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultRepository(FakeTimeDao(), FakeTimeDaoAc())

        repository.add("Repository")

        assertEquals(repository.dataItemTypes.first().size, 1)
    }

}

private class FakeTimeDao : TimeDao {

    private val data = mutableListOf<Time>()

    override fun getDataItemTypes(): Flow<List<Time>> = flow {
        emit(data)
    }

    override suspend fun insertDataItemType(item: Time) {
        data.add(0, item)
    }
}

private class FakeTimeDaoAc : TimeDaoAc {

    override fun getAllTime(): Flow<List<TimeAc>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTime(time: TimeAc) {
        TODO("Not yet implemented")
    }

    override fun getTime(id: Int): Flow<TimeAc> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTime(time: TimeAc) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTime(time: TimeAc) {

    }


}