package com.fityatra.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fityatra.app.data.entities.WorkoutPlanExercise;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WorkoutPlanExerciseDao_Impl implements WorkoutPlanExerciseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutPlanExercise> __insertionAdapterOfWorkoutPlanExercise;

  private final EntityDeletionOrUpdateAdapter<WorkoutPlanExercise> __deletionAdapterOfWorkoutPlanExercise;

  private final EntityDeletionOrUpdateAdapter<WorkoutPlanExercise> __updateAdapterOfWorkoutPlanExercise;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExercisesByPlan;

  public WorkoutPlanExerciseDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutPlanExercise = new EntityInsertionAdapter<WorkoutPlanExercise>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `workout_plan_exercises` (`id`,`planId`,`exerciseId`,`dayOfWeek`,`orderInDay`,`exerciseType`,`sets`,`reps`,`weight`,`restSeconds`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutPlanExercise value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getPlanId());
        stmt.bindLong(3, value.getExerciseId());
        stmt.bindLong(4, value.getDayOfWeek());
        stmt.bindLong(5, value.getOrderInDay());
        if (value.getExerciseType() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getExerciseType());
        }
        stmt.bindLong(7, value.getSets());
        stmt.bindLong(8, value.getReps());
        stmt.bindDouble(9, value.getWeight());
        stmt.bindLong(10, value.getRestSeconds());
        if (value.getNotes() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getNotes());
        }
      }
    };
    this.__deletionAdapterOfWorkoutPlanExercise = new EntityDeletionOrUpdateAdapter<WorkoutPlanExercise>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `workout_plan_exercises` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutPlanExercise value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfWorkoutPlanExercise = new EntityDeletionOrUpdateAdapter<WorkoutPlanExercise>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `workout_plan_exercises` SET `id` = ?,`planId` = ?,`exerciseId` = ?,`dayOfWeek` = ?,`orderInDay` = ?,`exerciseType` = ?,`sets` = ?,`reps` = ?,`weight` = ?,`restSeconds` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutPlanExercise value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getPlanId());
        stmt.bindLong(3, value.getExerciseId());
        stmt.bindLong(4, value.getDayOfWeek());
        stmt.bindLong(5, value.getOrderInDay());
        if (value.getExerciseType() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getExerciseType());
        }
        stmt.bindLong(7, value.getSets());
        stmt.bindLong(8, value.getReps());
        stmt.bindDouble(9, value.getWeight());
        stmt.bindLong(10, value.getRestSeconds());
        if (value.getNotes() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getNotes());
        }
        stmt.bindLong(12, value.getId());
      }
    };
    this.__preparedStmtOfDeleteExercisesByPlan = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM workout_plan_exercises WHERE planId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertWorkoutPlanExercise(final WorkoutPlanExercise exercise,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfWorkoutPlanExercise.insertAndReturnId(exercise);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertWorkoutPlanExercises(final List<WorkoutPlanExercise> exercises,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutPlanExercise.insert(exercises);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteWorkoutPlanExercise(final WorkoutPlanExercise exercise,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkoutPlanExercise.handle(exercise);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateWorkoutPlanExercise(final WorkoutPlanExercise exercise,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkoutPlanExercise.handle(exercise);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteExercisesByPlan(final long planId,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExercisesByPlan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, planId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteExercisesByPlan.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<WorkoutPlanExercise>> getExercisesByPlan(final long planId) {
    final String _sql = "SELECT * FROM workout_plan_exercises WHERE planId = ? ORDER BY dayOfWeek, orderInDay";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, planId);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_plan_exercises"}, new Callable<List<WorkoutPlanExercise>>() {
      @Override
      public List<WorkoutPlanExercise> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "planId");
          final int _cursorIndexOfExerciseId = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseId");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfOrderInDay = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInDay");
          final int _cursorIndexOfExerciseType = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseType");
          final int _cursorIndexOfSets = CursorUtil.getColumnIndexOrThrow(_cursor, "sets");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfRestSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "restSeconds");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<WorkoutPlanExercise> _result = new ArrayList<WorkoutPlanExercise>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutPlanExercise _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPlanId;
            _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId);
            final long _tmpExerciseId;
            _tmpExerciseId = _cursor.getLong(_cursorIndexOfExerciseId);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpOrderInDay;
            _tmpOrderInDay = _cursor.getInt(_cursorIndexOfOrderInDay);
            final String _tmpExerciseType;
            if (_cursor.isNull(_cursorIndexOfExerciseType)) {
              _tmpExerciseType = null;
            } else {
              _tmpExerciseType = _cursor.getString(_cursorIndexOfExerciseType);
            }
            final int _tmpSets;
            _tmpSets = _cursor.getInt(_cursorIndexOfSets);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeight;
            _tmpWeight = _cursor.getDouble(_cursorIndexOfWeight);
            final int _tmpRestSeconds;
            _tmpRestSeconds = _cursor.getInt(_cursorIndexOfRestSeconds);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new WorkoutPlanExercise(_tmpId,_tmpPlanId,_tmpExerciseId,_tmpDayOfWeek,_tmpOrderInDay,_tmpExerciseType,_tmpSets,_tmpReps,_tmpWeight,_tmpRestSeconds,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<WorkoutPlanExercise>> getExercisesByPlanAndDay(final long planId,
      final int dayOfWeek) {
    final String _sql = "SELECT * FROM workout_plan_exercises WHERE planId = ? AND dayOfWeek = ? ORDER BY orderInDay";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, planId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, dayOfWeek);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_plan_exercises"}, new Callable<List<WorkoutPlanExercise>>() {
      @Override
      public List<WorkoutPlanExercise> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "planId");
          final int _cursorIndexOfExerciseId = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseId");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfOrderInDay = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInDay");
          final int _cursorIndexOfExerciseType = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseType");
          final int _cursorIndexOfSets = CursorUtil.getColumnIndexOrThrow(_cursor, "sets");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfRestSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "restSeconds");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<WorkoutPlanExercise> _result = new ArrayList<WorkoutPlanExercise>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutPlanExercise _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPlanId;
            _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId);
            final long _tmpExerciseId;
            _tmpExerciseId = _cursor.getLong(_cursorIndexOfExerciseId);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpOrderInDay;
            _tmpOrderInDay = _cursor.getInt(_cursorIndexOfOrderInDay);
            final String _tmpExerciseType;
            if (_cursor.isNull(_cursorIndexOfExerciseType)) {
              _tmpExerciseType = null;
            } else {
              _tmpExerciseType = _cursor.getString(_cursorIndexOfExerciseType);
            }
            final int _tmpSets;
            _tmpSets = _cursor.getInt(_cursorIndexOfSets);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeight;
            _tmpWeight = _cursor.getDouble(_cursorIndexOfWeight);
            final int _tmpRestSeconds;
            _tmpRestSeconds = _cursor.getInt(_cursorIndexOfRestSeconds);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new WorkoutPlanExercise(_tmpId,_tmpPlanId,_tmpExerciseId,_tmpDayOfWeek,_tmpOrderInDay,_tmpExerciseType,_tmpSets,_tmpReps,_tmpWeight,_tmpRestSeconds,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMaxOrderForDay(final long planId, final int dayOfWeek, final String exerciseType,
      final Continuation<? super Integer> continuation) {
    final String _sql = "SELECT MAX(orderInDay) FROM workout_plan_exercises WHERE planId = ? AND dayOfWeek = ? AND exerciseType = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, planId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, dayOfWeek);
    _argIndex = 3;
    if (exerciseType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, exerciseType);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if(_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
