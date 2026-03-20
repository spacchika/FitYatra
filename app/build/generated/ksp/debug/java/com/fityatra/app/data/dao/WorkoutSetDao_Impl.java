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
import com.fityatra.app.data.entities.WorkoutSet;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
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
public final class WorkoutSetDao_Impl implements WorkoutSetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutSet> __insertionAdapterOfWorkoutSet;

  private final EntityDeletionOrUpdateAdapter<WorkoutSet> __deletionAdapterOfWorkoutSet;

  private final EntityDeletionOrUpdateAdapter<WorkoutSet> __updateAdapterOfWorkoutSet;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSetsBySession;

  public WorkoutSetDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutSet = new EntityInsertionAdapter<WorkoutSet>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `workout_sets` (`id`,`sessionId`,`exerciseId`,`setNumber`,`reps`,`weight`,`durationSeconds`,`restSeconds`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutSet value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getSessionId());
        stmt.bindLong(3, value.getExerciseId());
        stmt.bindLong(4, value.getSetNumber());
        stmt.bindLong(5, value.getReps());
        stmt.bindDouble(6, value.getWeight());
        stmt.bindLong(7, value.getDurationSeconds());
        stmt.bindLong(8, value.getRestSeconds());
      }
    };
    this.__deletionAdapterOfWorkoutSet = new EntityDeletionOrUpdateAdapter<WorkoutSet>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `workout_sets` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutSet value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfWorkoutSet = new EntityDeletionOrUpdateAdapter<WorkoutSet>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `workout_sets` SET `id` = ?,`sessionId` = ?,`exerciseId` = ?,`setNumber` = ?,`reps` = ?,`weight` = ?,`durationSeconds` = ?,`restSeconds` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutSet value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getSessionId());
        stmt.bindLong(3, value.getExerciseId());
        stmt.bindLong(4, value.getSetNumber());
        stmt.bindLong(5, value.getReps());
        stmt.bindDouble(6, value.getWeight());
        stmt.bindLong(7, value.getDurationSeconds());
        stmt.bindLong(8, value.getRestSeconds());
        stmt.bindLong(9, value.getId());
      }
    };
    this.__preparedStmtOfDeleteSetsBySession = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM workout_sets WHERE sessionId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSet(final WorkoutSet set, final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfWorkoutSet.insertAndReturnId(set);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertSets(final List<WorkoutSet> sets,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutSet.insert(sets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteSet(final WorkoutSet set, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkoutSet.handle(set);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateSet(final WorkoutSet set, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkoutSet.handle(set);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteSetsBySession(final long sessionId,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSetsBySession.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, sessionId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteSetsBySession.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<WorkoutSet>> getSetsBySession(final long sessionId) {
    final String _sql = "SELECT * FROM workout_sets WHERE sessionId = ? ORDER BY exerciseId, setNumber";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_sets"}, new Callable<List<WorkoutSet>>() {
      @Override
      public List<WorkoutSet> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfExerciseId = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseId");
          final int _cursorIndexOfSetNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "setNumber");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSeconds");
          final int _cursorIndexOfRestSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "restSeconds");
          final List<WorkoutSet> _result = new ArrayList<WorkoutSet>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutSet _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final long _tmpExerciseId;
            _tmpExerciseId = _cursor.getLong(_cursorIndexOfExerciseId);
            final int _tmpSetNumber;
            _tmpSetNumber = _cursor.getInt(_cursorIndexOfSetNumber);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeight;
            _tmpWeight = _cursor.getDouble(_cursorIndexOfWeight);
            final int _tmpDurationSeconds;
            _tmpDurationSeconds = _cursor.getInt(_cursorIndexOfDurationSeconds);
            final int _tmpRestSeconds;
            _tmpRestSeconds = _cursor.getInt(_cursorIndexOfRestSeconds);
            _item = new WorkoutSet(_tmpId,_tmpSessionId,_tmpExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpDurationSeconds,_tmpRestSeconds);
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
  public Flow<List<WorkoutSet>> getSetsByExercise(final long exerciseId) {
    final String _sql = "SELECT * FROM workout_sets WHERE exerciseId = ? ORDER BY sessionId DESC, setNumber";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, exerciseId);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_sets"}, new Callable<List<WorkoutSet>>() {
      @Override
      public List<WorkoutSet> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfExerciseId = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseId");
          final int _cursorIndexOfSetNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "setNumber");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSeconds");
          final int _cursorIndexOfRestSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "restSeconds");
          final List<WorkoutSet> _result = new ArrayList<WorkoutSet>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutSet _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final long _tmpExerciseId;
            _tmpExerciseId = _cursor.getLong(_cursorIndexOfExerciseId);
            final int _tmpSetNumber;
            _tmpSetNumber = _cursor.getInt(_cursorIndexOfSetNumber);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeight;
            _tmpWeight = _cursor.getDouble(_cursorIndexOfWeight);
            final int _tmpDurationSeconds;
            _tmpDurationSeconds = _cursor.getInt(_cursorIndexOfDurationSeconds);
            final int _tmpRestSeconds;
            _tmpRestSeconds = _cursor.getInt(_cursorIndexOfRestSeconds);
            _item = new WorkoutSet(_tmpId,_tmpSessionId,_tmpExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpDurationSeconds,_tmpRestSeconds);
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
  public Object getSetById(final long id, final Continuation<? super WorkoutSet> continuation) {
    final String _sql = "SELECT * FROM workout_sets WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkoutSet>() {
      @Override
      public WorkoutSet call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfExerciseId = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseId");
          final int _cursorIndexOfSetNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "setNumber");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSeconds");
          final int _cursorIndexOfRestSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "restSeconds");
          final WorkoutSet _result;
          if(_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final long _tmpExerciseId;
            _tmpExerciseId = _cursor.getLong(_cursorIndexOfExerciseId);
            final int _tmpSetNumber;
            _tmpSetNumber = _cursor.getInt(_cursorIndexOfSetNumber);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeight;
            _tmpWeight = _cursor.getDouble(_cursorIndexOfWeight);
            final int _tmpDurationSeconds;
            _tmpDurationSeconds = _cursor.getInt(_cursorIndexOfDurationSeconds);
            final int _tmpRestSeconds;
            _tmpRestSeconds = _cursor.getInt(_cursorIndexOfRestSeconds);
            _result = new WorkoutSet(_tmpId,_tmpSessionId,_tmpExerciseId,_tmpSetNumber,_tmpReps,_tmpWeight,_tmpDurationSeconds,_tmpRestSeconds);
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

  @Override
  public Object getMaxWeightForExercise(final long exerciseId,
      final Continuation<? super Double> continuation) {
    final String _sql = "SELECT MAX(weight) FROM workout_sets WHERE exerciseId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, exerciseId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if(_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
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

  @Override
  public Object getAverageWeightSince(final long exerciseId, final long sinceDate,
      final Continuation<? super Double> continuation) {
    final String _sql = "SELECT AVG(weight) FROM workout_sets WHERE exerciseId = ? AND sessionId IN (SELECT id FROM workout_sessions WHERE date >= ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, exerciseId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, sinceDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if(_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
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
