
package com.example.kashio.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity
data class Title(
    var title: String,
    var color: String,

    @PrimaryKey(autoGenerate = true)
   var uid: Int = 0
)



@Dao
interface TitleDao {
    @Query("SELECT * FROM title ORDER BY title")
    fun getTitle(): Flow<List<Title>>

    @Transaction
    suspend fun insertOrReplaceTitle(newTitle: Title) {
        val oldTitle = getTitle(newTitle.title)
        if (oldTitle != null) {
            deleteTitle(oldTitle)
        }
        insertTitle(newTitle)
    }

    @Query("SELECT * FROM title WHERE title = :title LIMIT 1")
    suspend fun getTitle(title: String): Title?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitle(title: Title)

    @Delete
    suspend fun deleteTitle(title: Title)
}



