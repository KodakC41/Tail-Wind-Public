package com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Database(entities = [Flight::class], version = 3)
@TypeConverters(Converters::class)
abstract class FlightList : RoomDatabase() {
    abstract fun FlightDao(): FlightDao
}
val migration_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val defaultUUID: String = UUID.randomUUID().toString()
        database.execSQL(
            "ALTER TABLE flights_list ADD COLUMN name TEXT NOT NULL DEFAULT '$defaultUUID'"
        )
    }
}

/**
 * Supports the FlightResponse which will allow the user to store their comments as fixes.
 * I am currently too scared to use this.
 */
val migration_1_3 = object : Migration(1,3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val defaultUUID: String = UUID.randomUUID().toString()
        database.execSQL(
            "ALTER TABLE flights_list ADD COLUMN name TEXT NOT NULL DEFAULT '$defaultUUID'"
        )
    }
}
