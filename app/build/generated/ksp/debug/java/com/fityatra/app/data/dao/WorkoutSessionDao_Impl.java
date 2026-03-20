package com.fityatra.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fityatra.app.data.Converters;
import com.fityatra.app.data.entities.WorkoutSession;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WorkoutSessionDao_Impl implements WorkoutSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutSession> __insertionAdapterOfWorkoutSession;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<WorkoutSession> __deletionAdapterOfWorkoutSession;

  private final EntityDeletionOrUpdateAdapter<WorkoutSession> __updateAdapterOfWorkoutSession;

  public WorkoutSessionDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutSession = new EntityInsertionAdapter<WorkoutSession>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `workout_sessions` (`id`,`planId`,`date`,`isDeload`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutSession value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getPlanId());
        final Long _tmp = __converters.dateToTimestamp(value.getDate());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp);
        }
        final int _tmp_1 = value.isDeload() ? 1 : 0;
        stmt.bindLong(4, _tmp_1);
      }
    };
    this.__deletionAdapterOfWorkoutSession = new EntityDeletionOrUpdateAdapter<WorkoutSession>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `workout_sessions` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutSession value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfWorkoutSession = new EntityDeletionOrUpdateAdapter<WorkoutSession>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `workout_sessions` SET `id` = ?,`planId` = ?,`date` = ?,`isDeload` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, WorkoutSession value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getPlanId());
        final Long _tmp = __converters.dateToTimestamp(value.getDate());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp);
        }
        final int _tmp_1 = value.isDeload() ? 1 : 0;
        stmt.bindLong(4, _tmp_1);
        stmt.bindLong(5, value.getId());
      }
    };
  }

  @Override
  public Object insertSession(final WorkoutSession session,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfWorkoutSession.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteSession(final WorkoutSession session,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkoutSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateSession(final WorkoutSession session,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkoutSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<WorkoutSession>> getAllSessions() {
    final String _sql = "SELECT * FROM workout_sessions ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_sessions"}, new Callable<List<WorkoutSession>>() {
      @Override
      public List<WorkoutSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "planId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsDeload = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeload");
          final List<WorkoutSession> _result = new ArrayList<WorkoutSession>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPlanId;
            _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId);
            final Date _tmpDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfDate);
            }
            final Date _tmp_1 = __converters.fromTimestamp(_tmp);
            if(_tmp_1 == null) {
              throw new IllegalStateException("Expected non-null java.util.Date, but it was null.");
            } else {
              _tmpDate = _tmp_1;
            }
            final boolean _tmpIsDeload;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDeload);
            _tmpIsDeload = _tmp_2 != 0;
            _item = new WorkoutSession(_tmpId,_tmpPlanId,_tmpDate,_tmpIsDeload);
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
  public Flow<List<WorkoutSession>> getSessionsByPlan(final long planId) {
    final String _sql = "SELECT * FROM workout_sessions WHERE planId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, planId);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_sessions"}, new Callable<List<WorkoutSession>>() {
      @Override
      public List<WorkoutSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "planId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsDeload = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeload");
          final List<WorkoutSession> _result = new ArrayList<WorkoutSession>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPlanId;
            _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId);
            final Date _tmpDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfDate);
            }
            final Date _tmp_1 = __converters.fromTimestamp(_tmp);
            if(_tmp_1 == null) {
              throw new IllegalStateException("Expected non-null java.util.Date, but it was null.");
            } else {
              _tmpDate = _tmp_1;
            }
            final boolean _tmpIsDeload;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDeload);
            _tmpIsDeload = _tmp_2 != 0;
            _item = new WorkoutSession(_tmpId,_tmpPlanId,_tmpDate,_tmpIsDeload);
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
  public Object getSessionById(final long id,
      final Continuation<? super WorkoutSession> continuation) {
    final String _sql = "SELECT * FROM workout_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkoutSession>() {
      @Override
      public WorkoutSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "planId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsDeload = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeload");
          final WorkoutSession _result;
          if(_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPlanId;
            _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId);
            final Date _tmpDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfDate);
            }
            final Date _tmp_1 = __converters.fromTimestamp(_tmp);
            if(_tmp_1 == null) {
              throw new IllegalStateException("Expected non-null java.util.Date, but it was null.");
            } else {
              _tmpDate = _tmp_1;
            }
            final boolean _tmpIsDeload;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDeload);
            _tmpIsDeload = _tmp_2 != 0;
            _result = new WorkoutSession(_tmpId,_tmpPlanId,_tmpDate,_tmpIsDeload);
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
  public Flow<List<WorkoutSession>> getSessionsBetweenDates(final Date startDate,
      final Date endDate) {
    final String _sql = "SELECT * FROM workout_sessions WHERE date BETWEEN ? AND ? ORDER BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final Long _tmp = __converters.dateToTimestamp(startDate);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    _argIndex = 2;
    final Long _tmp_1 = __converters.dateToTimestamp(endDate);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_sessions"}, new Callable<List<WorkoutSession>>() {
      @Override
      public List<WorkoutSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "planId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsDeload = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeload");
          final List<WorkoutSession> _result = new ArrayList<WorkoutSession>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPlanId;
            _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId);
            final Date _tmpDate;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfDate);
            }
            final Date _tmp_3 = __converters.fromTimestamp(_tmp_2);
            if(_tmp_3 == null) {
              throw new IllegalStateException("Expected non-null java.util.Date, but it was null.");
            } else {
              _tmpDate = _tmp_3;
            }
            final boolean _tmpIsDeload;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsDeload);
            _tmpIsDeload = _tmp_4 != 0;
            _item = new WorkoutSession(_tmpId,_tmpPlanId,_tmpDate,_tmpIsDeload);
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
  public Flow<List<WorkoutSession>> getDeloadSessions() {
    final String _sql = "SELECT * FROM workout_sessions WHERE isDeload = 1 ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"workout_sessions"}, new Callable<List<WorkoutSession>>() {
      @Override
      public List<WorkoutSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "planId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsDeload = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeload");
          final List<WorkoutSession> _result = new ArrayList<WorkoutSession>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final WorkoutSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPlanId;
            _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId);
            final Date _tmpDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfDate);
            }
            final Date _tmp_1 = __converters.fromTimestamp(_tmp);
            if(_tmp_1 == null) {
              throw new IllegalStateException("Expected non-null java.util.Date, but it was null.");
            } else {
              _tmpDate = _tmp_1;
            }
            final boolean _tmpIsDeload;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDeload);
            _tmpIsDeload = _tmp_2 != 0;
            _item = new WorkoutSession(_tmpId,_tmpPlanId,_tmpDate,_tmpIsDeload);
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

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
