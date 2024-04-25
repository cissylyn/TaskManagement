package com.example.taskmanagement

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskItem::class], version = 4, exportSchema = false)
abstract class TaskItemDatabase : RoomDatabase() {

    abstract fun taskItemDao(): TaskItemDao

    companion object {
        @Volatile
        private var INSTANCE: TaskItemDatabase? = null

        fun getDatabase(context: Context): TaskItemDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): TaskItemDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                TaskItemDatabase::class.java,
                "task_item_database"
            )
                // Add migrations from version 1 to 3
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
        }



        // Define migrations from version 1 to 2 and from version 2 to 3
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic from version 1 to 2
                // This is where you would modify the schema or perform data migration
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic from version 2 to 3
                // This is where you would modify the schema or perform data migration
            }
        }

        // Define migration from version 3 to 4
        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic from version 3 to 4
                database.execSQL("ALTER TABLE task_item_table ADD COLUMN startDateString TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE task_item_table ADD COLUMN endDateString TEXT DEFAULT NULL")
            }
        }

        private val MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
    }
}
//i have changed this code