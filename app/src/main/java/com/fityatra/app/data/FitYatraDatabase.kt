package com.fityatra.app.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.fityatra.app.data.dao.*
import com.fityatra.app.data.entities.*

@Database(
    entities = [
        Category::class,
        Exercise::class,
        WorkoutPlan::class,
        WorkoutPlanExercise::class,
        WorkoutSession::class,
        WorkoutSet::class,
        UserSettings::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitYatraDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutPlanExerciseDao(): WorkoutPlanExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutSetDao(): WorkoutSetDao

    companion object {
        @Volatile
        private var INSTANCE: FitYatraDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to workout_plan_exercises table
                database.execSQL(
                    "ALTER TABLE workout_plan_exercises ADD COLUMN exerciseType TEXT NOT NULL DEFAULT 'main'"
                )
                database.execSQL(
                    "ALTER TABLE workout_plan_exercises ADD COLUMN sets INTEGER NOT NULL DEFAULT 3"
                )
                database.execSQL(
                    "ALTER TABLE workout_plan_exercises ADD COLUMN reps INTEGER NOT NULL DEFAULT 10"
                )
                database.execSQL(
                    "ALTER TABLE workout_plan_exercises ADD COLUMN weight REAL NOT NULL DEFAULT 0.0"
                )
                database.execSQL(
                    "ALTER TABLE workout_plan_exercises ADD COLUMN restSeconds INTEGER NOT NULL DEFAULT 60"
                )
                database.execSQL(
                    "ALTER TABLE workout_plan_exercises ADD COLUMN notes TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun getDatabase(context: Context): FitYatraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitYatraDatabase::class.java,
                    "fityatra_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
