
package com.example.kashio.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class Item(
    val name: String,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}



@Dao
interface ItemDao {
    @Query("SELECT * FROM item ORDER BY uid DESC LIMIT 10")
    fun getDataItemTypes(): Flow<List<Item>>

    @Insert
    suspend fun insertDataItemType(item: Item)

}


