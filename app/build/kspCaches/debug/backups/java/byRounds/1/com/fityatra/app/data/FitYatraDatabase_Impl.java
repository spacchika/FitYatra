package com.fityatra.app.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.fityatra.app.data.dao.CategoryDao;
import com.fityatra.app.data.dao.CategoryDao_Impl;
import com.fityatra.app.data.dao.ExerciseDao;
import com.fityatra.app.data.dao.ExerciseDao_Impl;
import com.fityatra.app.data.dao.WorkoutPlanDao;
import com.fityatra.app.data.dao.WorkoutPlanDao_Impl;
import com.fityatra.app.data.dao.WorkoutPlanExerciseDao;
import com.fityatra.app.data.dao.WorkoutPlanExerciseDao_Impl;
import com.fityatra.app.data.dao.WorkoutSessionDao;
import com.fityatra.app.data.dao.WorkoutSessionDao_Impl;
import com.fityatra.app.data.dao.WorkoutSetDao;
import com.fityatra.app.data.dao.WorkoutSetDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FitYatraDatabase_Impl extends FitYatraDatabase {
  private volatile CategoryDao _categoryDao;

  private volatile ExerciseDao _exerciseDao;

  private volatile WorkoutPlanDao _workoutPlanDao;

  private volatile WorkoutPlanExerciseDao _workoutPlanExerciseDao;

  private volatile WorkoutSessionDao _workoutSessionDao;

  private volatile WorkoutSetDao _workoutSetDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `categoryId` INTEGER NOT NULL, `description` TEXT NOT NULL, `isBuiltin` INTEGER NOT NULL, FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `workout_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `daysPerWeek` INTEGER NOT NULL, `isActive` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `workout_plan_exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `planId` INTEGER NOT NULL, `exerciseId` INTEGER NOT NULL, `dayOfWeek` INTEGER NOT NULL, `orderInDay` INTEGER NOT NULL, `exerciseType` TEXT NOT NULL, `sets` INTEGER NOT NULL, `reps` INTEGER NOT NULL, `weight` REAL NOT NULL, `restSeconds` INTEGER NOT NULL, `notes` TEXT NOT NULL, FOREIGN KEY(`planId`) REFERENCES `workout_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `workout_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `planId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `isDeload` INTEGER NOT NULL, FOREIGN KEY(`planId`) REFERENCES `workout_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `workout_sets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `exerciseId` INTEGER NOT NULL, `setNumber` INTEGER NOT NULL, `reps` INTEGER NOT NULL, `weight` REAL NOT NULL, `durationSeconds` INTEGER NOT NULL, `restSeconds` INTEGER NOT NULL, FOREIGN KEY(`sessionId`) REFERENCES `workout_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `user_settings` (`id` INTEGER NOT NULL, `deloadFrequencyWeeks` INTEGER NOT NULL, `deloadWeightDropPercent` INTEGER NOT NULL, `restTimerDefault` INTEGER NOT NULL, `backupAuto` INTEGER NOT NULL, `backupSchedule` TEXT NOT NULL, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '18f2b8510dd371b3c1c63476adcbb553')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `categories`");
        _db.execSQL("DROP TABLE IF EXISTS `exercises`");
        _db.execSQL("DROP TABLE IF EXISTS `workout_plans`");
        _db.execSQL("DROP TABLE IF EXISTS `workout_plan_exercises`");
        _db.execSQL("DROP TABLE IF EXISTS `workout_sessions`");
        _db.execSQL("DROP TABLE IF EXISTS `workout_sets`");
        _db.execSQL("DROP TABLE IF EXISTS `user_settings`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      public void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        _db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(2);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(_db, "categories");
        if (! _infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.fityatra.app.data.entities.Category).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsExercises = new HashMap<String, TableInfo.Column>(5);
        _columnsExercises.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("isBuiltin", new TableInfo.Column("isBuiltin", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExercises = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysExercises.add(new TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION",Arrays.asList("categoryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesExercises = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExercises = new TableInfo("exercises", _columnsExercises, _foreignKeysExercises, _indicesExercises);
        final TableInfo _existingExercises = TableInfo.read(_db, "exercises");
        if (! _infoExercises.equals(_existingExercises)) {
          return new RoomOpenHelper.ValidationResult(false, "exercises(com.fityatra.app.data.entities.Exercise).\n"
                  + " Expected:\n" + _infoExercises + "\n"
                  + " Found:\n" + _existingExercises);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutPlans = new HashMap<String, TableInfo.Column>(5);
        _columnsWorkoutPlans.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlans.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlans.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlans.put("daysPerWeek", new TableInfo.Column("daysPerWeek", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlans.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutPlans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkoutPlans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutPlans = new TableInfo("workout_plans", _columnsWorkoutPlans, _foreignKeysWorkoutPlans, _indicesWorkoutPlans);
        final TableInfo _existingWorkoutPlans = TableInfo.read(_db, "workout_plans");
        if (! _infoWorkoutPlans.equals(_existingWorkoutPlans)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_plans(com.fityatra.app.data.entities.WorkoutPlan).\n"
                  + " Expected:\n" + _infoWorkoutPlans + "\n"
                  + " Found:\n" + _existingWorkoutPlans);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutPlanExercises = new HashMap<String, TableInfo.Column>(11);
        _columnsWorkoutPlanExercises.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("planId", new TableInfo.Column("planId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("exerciseId", new TableInfo.Column("exerciseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("dayOfWeek", new TableInfo.Column("dayOfWeek", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("orderInDay", new TableInfo.Column("orderInDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("exerciseType", new TableInfo.Column("exerciseType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("sets", new TableInfo.Column("sets", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("reps", new TableInfo.Column("reps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("weight", new TableInfo.Column("weight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("restSeconds", new TableInfo.Column("restSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutPlanExercises.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutPlanExercises = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysWorkoutPlanExercises.add(new TableInfo.ForeignKey("workout_plans", "CASCADE", "NO ACTION",Arrays.asList("planId"), Arrays.asList("id")));
        _foreignKeysWorkoutPlanExercises.add(new TableInfo.ForeignKey("exercises", "CASCADE", "NO ACTION",Arrays.asList("exerciseId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWorkoutPlanExercises = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutPlanExercises = new TableInfo("workout_plan_exercises", _columnsWorkoutPlanExercises, _foreignKeysWorkoutPlanExercises, _indicesWorkoutPlanExercises);
        final TableInfo _existingWorkoutPlanExercises = TableInfo.read(_db, "workout_plan_exercises");
        if (! _infoWorkoutPlanExercises.equals(_existingWorkoutPlanExercises)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_plan_exercises(com.fityatra.app.data.entities.WorkoutPlanExercise).\n"
                  + " Expected:\n" + _infoWorkoutPlanExercises + "\n"
                  + " Found:\n" + _existingWorkoutPlanExercises);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutSessions = new HashMap<String, TableInfo.Column>(4);
        _columnsWorkoutSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("planId", new TableInfo.Column("planId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSessions.put("isDeload", new TableInfo.Column("isDeload", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutSessions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWorkoutSessions.add(new TableInfo.ForeignKey("workout_plans", "CASCADE", "NO ACTION",Arrays.asList("planId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWorkoutSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutSessions = new TableInfo("workout_sessions", _columnsWorkoutSessions, _foreignKeysWorkoutSessions, _indicesWorkoutSessions);
        final TableInfo _existingWorkoutSessions = TableInfo.read(_db, "workout_sessions");
        if (! _infoWorkoutSessions.equals(_existingWorkoutSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_sessions(com.fityatra.app.data.entities.WorkoutSession).\n"
                  + " Expected:\n" + _infoWorkoutSessions + "\n"
                  + " Found:\n" + _existingWorkoutSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutSets = new HashMap<String, TableInfo.Column>(8);
        _columnsWorkoutSets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSets.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSets.put("exerciseId", new TableInfo.Column("exerciseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSets.put("setNumber", new TableInfo.Column("setNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSets.put("reps", new TableInfo.Column("reps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSets.put("weight", new TableInfo.Column("weight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSets.put("durationSeconds", new TableInfo.Column("durationSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutSets.put("restSeconds", new TableInfo.Column("restSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutSets = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysWorkoutSets.add(new TableInfo.ForeignKey("workout_sessions", "CASCADE", "NO ACTION",Arrays.asList("sessionId"), Arrays.asList("id")));
        _foreignKeysWorkoutSets.add(new TableInfo.ForeignKey("exercises", "CASCADE", "NO ACTION",Arrays.asList("exerciseId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWorkoutSets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkoutSets = new TableInfo("workout_sets", _columnsWorkoutSets, _foreignKeysWorkoutSets, _indicesWorkoutSets);
        final TableInfo _existingWorkoutSets = TableInfo.read(_db, "workout_sets");
        if (! _infoWorkoutSets.equals(_existingWorkoutSets)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_sets(com.fityatra.app.data.entities.WorkoutSet).\n"
                  + " Expected:\n" + _infoWorkoutSets + "\n"
                  + " Found:\n" + _existingWorkoutSets);
        }
        final HashMap<String, TableInfo.Column> _columnsUserSettings = new HashMap<String, TableInfo.Column>(6);
        _columnsUserSettings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("deloadFrequencyWeeks", new TableInfo.Column("deloadFrequencyWeeks", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("deloadWeightDropPercent", new TableInfo.Column("deloadWeightDropPercent", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("restTimerDefault", new TableInfo.Column("restTimerDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("backupAuto", new TableInfo.Column("backupAuto", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSettings.put("backupSchedule", new TableInfo.Column("backupSchedule", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserSettings = new TableInfo("user_settings", _columnsUserSettings, _foreignKeysUserSettings, _indicesUserSettings);
        final TableInfo _existingUserSettings = TableInfo.read(_db, "user_settings");
        if (! _infoUserSettings.equals(_existingUserSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "user_settings(com.fityatra.app.data.entities.UserSettings).\n"
                  + " Expected:\n" + _infoUserSettings + "\n"
                  + " Found:\n" + _existingUserSettings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "18f2b8510dd371b3c1c63476adcbb553", "7e366b3b820d4528bb2b71a99141838e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "categories","exercises","workout_plans","workout_plan_exercises","workout_sessions","workout_sets","user_settings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `exercises`");
      _db.execSQL("DELETE FROM `workout_plans`");
      _db.execSQL("DELETE FROM `workout_plan_exercises`");
      _db.execSQL("DELETE FROM `workout_sessions`");
      _db.execSQL("DELETE FROM `workout_sets`");
      _db.execSQL("DELETE FROM `user_settings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExerciseDao.class, ExerciseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutPlanDao.class, WorkoutPlanDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutPlanExerciseDao.class, WorkoutPlanExerciseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutSessionDao.class, WorkoutSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutSetDao.class, WorkoutSetDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public ExerciseDao exerciseDao() {
    if (_exerciseDao != null) {
      return _exerciseDao;
    } else {
      synchronized(this) {
        if(_exerciseDao == null) {
          _exerciseDao = new ExerciseDao_Impl(this);
        }
        return _exerciseDao;
      }
    }
  }

  @Override
  public WorkoutPlanDao workoutPlanDao() {
    if (_workoutPlanDao != null) {
      return _workoutPlanDao;
    } else {
      synchronized(this) {
        if(_workoutPlanDao == null) {
          _workoutPlanDao = new WorkoutPlanDao_Impl(this);
        }
        return _workoutPlanDao;
      }
    }
  }

  @Override
  public WorkoutPlanExerciseDao workoutPlanExerciseDao() {
    if (_workoutPlanExerciseDao != null) {
      return _workoutPlanExerciseDao;
    } else {
      synchronized(this) {
        if(_workoutPlanExerciseDao == null) {
          _workoutPlanExerciseDao = new WorkoutPlanExerciseDao_Impl(this);
        }
        return _workoutPlanExerciseDao;
      }
    }
  }

  @Override
  public WorkoutSessionDao workoutSessionDao() {
    if (_workoutSessionDao != null) {
      return _workoutSessionDao;
    } else {
      synchronized(this) {
        if(_workoutSessionDao == null) {
          _workoutSessionDao = new WorkoutSessionDao_Impl(this);
        }
        return _workoutSessionDao;
      }
    }
  }

  @Override
  public WorkoutSetDao workoutSetDao() {
    if (_workoutSetDao != null) {
      return _workoutSetDao;
    } else {
      synchronized(this) {
        if(_workoutSetDao == null) {
          _workoutSetDao = new WorkoutSetDao_Impl(this);
        }
        return _workoutSetDao;
      }
    }
  }
}
