package com.example.schedulex.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS `Time`")
        // Create the new table
        db.execSQL("CREATE TABLE IF NOT EXISTS `Time` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `title` TEXT NOT NULL, `text` TEXT NOT NULL, `notification` TEXT NOT NULL, `startTime` TEXT NOT NULL, `endTime` TEXT NOT NULL, `color` TEXT NOT NULL)")
    }
}

// migration file for the time data base