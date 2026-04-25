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
        UserSettings::class,
        UserProfile::class,
        AiCoachMessage::class
    ],
    version = 3,
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
    abstract fun userProfileDao(): UserProfileDao
    abstract fun aiCoachMessageDao(): AiCoachMessageDao

    companion object {
        @Volatile
        private var INSTANCE: FitYatraDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val cursor = database.query(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='workout_plan_exercises'"
                )
                val tableExists = cursor.moveToFirst()
                cursor.close()

                if (tableExists) {
                    database.execSQL("ALTER TABLE workout_plan_exercises ADD COLUMN exerciseType TEXT NOT NULL DEFAULT 'main'")
                    database.execSQL("ALTER TABLE workout_plan_exercises ADD COLUMN sets INTEGER NOT NULL DEFAULT 3")
                    database.execSQL("ALTER TABLE workout_plan_exercises ADD COLUMN reps INTEGER NOT NULL DEFAULT 10")
                    database.execSQL("ALTER TABLE workout_plan_exercises ADD COLUMN weight REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("ALTER TABLE workout_plan_exercises ADD COLUMN restSeconds INTEGER NOT NULL DEFAULT 60")
                    database.execSQL("ALTER TABLE workout_plan_exercises ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
                } else {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS workout_plan_exercises (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            planId INTEGER NOT NULL,
                            exerciseId INTEGER NOT NULL,
                            dayOfWeek INTEGER NOT NULL,
                            orderInDay INTEGER NOT NULL,
                            exerciseType TEXT NOT NULL DEFAULT 'main',
                            sets INTEGER NOT NULL DEFAULT 3,
                            reps INTEGER NOT NULL DEFAULT 10,
                            weight REAL NOT NULL DEFAULT 0.0,
                            restSeconds INTEGER NOT NULL DEFAULT 60,
                            notes TEXT NOT NULL DEFAULT '',
                            FOREIGN KEY(planId) REFERENCES workout_plans(id) ON DELETE CASCADE,
                            FOREIGN KEY(exerciseId) REFERENCES exercises(id) ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )
                }
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id INTEGER NOT NULL PRIMARY KEY,
                        age INTEGER NOT NULL DEFAULT 0,
                        weightKg REAL NOT NULL DEFAULT 0.0,
                        heightCm REAL NOT NULL DEFAULT 0.0,
                        fitnessGoal TEXT NOT NULL DEFAULT '',
                        healthConditions TEXT NOT NULL DEFAULT '',
                        workoutPreferences TEXT NOT NULL DEFAULT '',
                        availableEquipment TEXT NOT NULL DEFAULT '',
                        experienceLevel TEXT NOT NULL DEFAULT 'beginner',
                        daysPerWeek INTEGER NOT NULL DEFAULT 4,
                        isOnboarded INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ai_coach_messages (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        role TEXT NOT NULL,
                        content TEXT NOT NULL,
                        timestamp INTEGER NOT NULL DEFAULT 0,
                        sessionDate TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): FitYatraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitYatraDatabase::class.java,
                    "fityatra_database_v3"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
