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
import com.fityatra.app.data.entities.WorkoutPlan;
import java.lang.Class;
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
public final class WorkoutPlanDao_Impl implements WorkoutPlanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutPlan> __insertionAdapterOfWorkoutPlan;

  private final EntityDeletionOrUpdateAdapter<WorkoutPlan> __deletionAdapterOfWorkoutPlan;

  private final EntityDeletionOrUpdateAdapter<WorkoutPlan> __updateAdapterOfWorkoutPlan;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAllPlans;

  private final SharedSQLiteStatement __preparedStmtOfSetActivePlan;

  public WorkoutPlanDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutPlan = new EntityInsertionAdapter<WorkoutPlan>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `workout_plans` (`id`,`name`,`description`,`daysPerWeek`,`isActive`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutPlan value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDescription());
        }
        stmt.bindLong(4, value.getDaysPerWeek());
        final int _tmp = value.isActive() ? 1 : 0;
        stmt.bindLong(5, _tmp);
      }
    };
    this.__deletionAdapterOfWorkoutPlan = new EntityDeletionOrUpdateAdapter<WorkoutPlan>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `workout_plans` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutPlan value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfWorkoutPlan = new EntityDeletionOrUpdateAdapter<WorkoutPlan>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `workout_plans` SET `id` = ?,`name` = ?,`description` = ?,`daysPerWeek` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutPlan value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDescription());
        }
        stmt.bindLong(4, value.getDaysPerWeek());
        final int _tmp = value.isActive() ? 1 : 0;
        stmt.bindLong(5, _tmp);
        stmt.bindLong(6, value.getId());
      }
    };
    this.__preparedStmtOfDeactivateAllPlans = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE workout_plans SET isActive = 0";
        return _query;
      }
    };
    this.__preparedStmtOfSetActivePlan = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE workout_plans SET isActive = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertWorkoutPlan(final WorkoutPlan plan,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfWorkoutPlan.insertAndReturnId(plan);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteWorkoutPlan(final WorkoutPlan plan,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkoutPlan.handle(plan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateWorkoutPlan(final WorkoutPlan plan,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkoutPlan.handle(plan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deactivateAllPlans(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAllPlans.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeactivateAllPlans.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object setActivePlan(final long planId, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetActivePlan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, planId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfSetActivePlan.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<WorkoutPlan>> getAllWorkoutPlans() {
    final String _sql = "SELECT * FROM workout_plans ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_plans"}, new Callable<List<WorkoutPlan>>() {
      @Override
      public List<WorkoutPlan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDaysPerWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "daysPerWeek");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<WorkoutPlan> _result = new ArrayList<WorkoutPlan>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutPlan _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final int _tmpDaysPerWeek;
            _tmpDaysPerWeek = _cursor.getInt(_cursorIndexOfDaysPerWeek);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new WorkoutPlan(_tmpId,_tmpName,_tmpDescription,_tmpDaysPerWeek,_tmpIsActive);
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
  public Object getWorkoutPlanById(final long id,
      final Continuation<? super WorkoutPlan> continuation) {
    final String _sql = "SELECT * FROM workout_plans WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkoutPlan>() {
      @Override
      public WorkoutPlan call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDaysPerWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "daysPerWeek");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final WorkoutPlan _result;
          if(_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final int _tmpDaysPerWeek;
            _tmpDaysPerWeek = _cursor.getInt(_cursorIndexOfDaysPerWeek);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _result = new WorkoutPlan(_tmpId,_tmpName,_tmpDescription,_tmpDaysPerWeek,_tmpIsActive);
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
  public Object getActivePlan(final Continuation<? super WorkoutPlan> continuation) {
    final String _sql = "SELECT * FROM workout_plans WHERE isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkoutPlan>() {
      @Override
      public WorkoutPlan call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDaysPerWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "daysPerWeek");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final WorkoutPlan _result;
          if(_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final int _tmpDaysPerWeek;
            _tmpDaysPerWeek = _cursor.getInt(_cursorIndexOfDaysPerWeek);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _result = new WorkoutPlan(_tmpId,_tmpName,_tmpDescription,_tmpDaysPerWeek,_tmpIsActive);
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
