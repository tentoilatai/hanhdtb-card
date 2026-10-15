package com.kaiz.quanlythe;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
public final class CardDao_Impl implements CardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CreditCard> __insertionAdapterOfCreditCard;

  private final EntityDeletionOrUpdateAdapter<CreditCard> __deletionAdapterOfCreditCard;

  private final EntityDeletionOrUpdateAdapter<CreditCard> __updateAdapterOfCreditCard;

  public CardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCreditCard = new EntityInsertionAdapter<CreditCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `credit_cards` (`id`,`userId`,`cardName`,`bankName`,`last4Digits`,`creditLimit`,`statementDay`,`dueDay`,`cardColorHex`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getCardName());
        statement.bindString(4, entity.getBankName());
        statement.bindString(5, entity.getLast4Digits());
        statement.bindDouble(6, entity.getCreditLimit());
        statement.bindLong(7, entity.getStatementDay());
        statement.bindLong(8, entity.getDueDay());
        statement.bindString(9, entity.getCardColorHex());
      }
    };
    this.__deletionAdapterOfCreditCard = new EntityDeletionOrUpdateAdapter<CreditCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `credit_cards` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCard entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCreditCard = new EntityDeletionOrUpdateAdapter<CreditCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `credit_cards` SET `id` = ?,`userId` = ?,`cardName` = ?,`bankName` = ?,`last4Digits` = ?,`creditLimit` = ?,`statementDay` = ?,`dueDay` = ?,`cardColorHex` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getCardName());
        statement.bindString(4, entity.getBankName());
        statement.bindString(5, entity.getLast4Digits());
        statement.bindDouble(6, entity.getCreditLimit());
        statement.bindLong(7, entity.getStatementDay());
        statement.bindLong(8, entity.getDueDay());
        statement.bindString(9, entity.getCardColorHex());
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insertCard(final CreditCard card, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCreditCard.insertAndReturnId(card);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCard(final CreditCard card, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCreditCard.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCard(final CreditCard card, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCreditCard.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CreditCard>> getCardsByUserId(final long userId) {
    final String _sql = "SELECT * FROM credit_cards WHERE userId = ? ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"credit_cards"}, new Callable<List<CreditCard>>() {
      @Override
      @NonNull
      public List<CreditCard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCardName = CursorUtil.getColumnIndexOrThrow(_cursor, "cardName");
          final int _cursorIndexOfBankName = CursorUtil.getColumnIndexOrThrow(_cursor, "bankName");
          final int _cursorIndexOfLast4Digits = CursorUtil.getColumnIndexOrThrow(_cursor, "last4Digits");
          final int _cursorIndexOfCreditLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "creditLimit");
          final int _cursorIndexOfStatementDay = CursorUtil.getColumnIndexOrThrow(_cursor, "statementDay");
          final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDay");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "cardColorHex");
          final List<CreditCard> _result = new ArrayList<CreditCard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CreditCard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpCardName;
            _tmpCardName = _cursor.getString(_cursorIndexOfCardName);
            final String _tmpBankName;
            _tmpBankName = _cursor.getString(_cursorIndexOfBankName);
            final String _tmpLast4Digits;
            _tmpLast4Digits = _cursor.getString(_cursorIndexOfLast4Digits);
            final double _tmpCreditLimit;
            _tmpCreditLimit = _cursor.getDouble(_cursorIndexOfCreditLimit);
            final int _tmpStatementDay;
            _tmpStatementDay = _cursor.getInt(_cursorIndexOfStatementDay);
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            _item = new CreditCard(_tmpId,_tmpUserId,_tmpCardName,_tmpBankName,_tmpLast4Digits,_tmpCreditLimit,_tmpStatementDay,_tmpDueDay,_tmpCardColorHex);
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
  public Object getCardById(final long cardId, final Continuation<? super CreditCard> $completion) {
    final String _sql = "SELECT * FROM credit_cards WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CreditCard>() {
      @Override
      @Nullable
      public CreditCard call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCardName = CursorUtil.getColumnIndexOrThrow(_cursor, "cardName");
          final int _cursorIndexOfBankName = CursorUtil.getColumnIndexOrThrow(_cursor, "bankName");
          final int _cursorIndexOfLast4Digits = CursorUtil.getColumnIndexOrThrow(_cursor, "last4Digits");
          final int _cursorIndexOfCreditLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "creditLimit");
          final int _cursorIndexOfStatementDay = CursorUtil.getColumnIndexOrThrow(_cursor, "statementDay");
          final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDay");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "cardColorHex");
          final CreditCard _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpCardName;
            _tmpCardName = _cursor.getString(_cursorIndexOfCardName);
            final String _tmpBankName;
            _tmpBankName = _cursor.getString(_cursorIndexOfBankName);
            final String _tmpLast4Digits;
            _tmpLast4Digits = _cursor.getString(_cursorIndexOfLast4Digits);
            final double _tmpCreditLimit;
            _tmpCreditLimit = _cursor.getDouble(_cursorIndexOfCreditLimit);
            final int _tmpStatementDay;
            _tmpStatementDay = _cursor.getInt(_cursorIndexOfStatementDay);
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            _result = new CreditCard(_tmpId,_tmpUserId,_tmpCardName,_tmpBankName,_tmpLast4Digits,_tmpCreditLimit,_tmpStatementDay,_tmpDueDay,_tmpCardColorHex);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public List<CreditCard> getAllCardsSync() {
    final String _sql = "SELECT * FROM credit_cards";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfCardName = CursorUtil.getColumnIndexOrThrow(_cursor, "cardName");
      final int _cursorIndexOfBankName = CursorUtil.getColumnIndexOrThrow(_cursor, "bankName");
      final int _cursorIndexOfLast4Digits = CursorUtil.getColumnIndexOrThrow(_cursor, "last4Digits");
      final int _cursorIndexOfCreditLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "creditLimit");
      final int _cursorIndexOfStatementDay = CursorUtil.getColumnIndexOrThrow(_cursor, "statementDay");
      final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDay");
      final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "cardColorHex");
      final List<CreditCard> _result = new ArrayList<CreditCard>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CreditCard _item;
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        final long _tmpUserId;
        _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
        final String _tmpCardName;
        _tmpCardName = _cursor.getString(_cursorIndexOfCardName);
        final String _tmpBankName;
        _tmpBankName = _cursor.getString(_cursorIndexOfBankName);
        final String _tmpLast4Digits;
        _tmpLast4Digits = _cursor.getString(_cursorIndexOfLast4Digits);
        final double _tmpCreditLimit;
        _tmpCreditLimit = _cursor.getDouble(_cursorIndexOfCreditLimit);
        final int _tmpStatementDay;
        _tmpStatementDay = _cursor.getInt(_cursorIndexOfStatementDay);
        final int _tmpDueDay;
        _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
        final String _tmpCardColorHex;
        _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
        _item = new CreditCard(_tmpId,_tmpUserId,_tmpCardName,_tmpBankName,_tmpLast4Digits,_tmpCreditLimit,_tmpStatementDay,_tmpDueDay,_tmpCardColorHex);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
