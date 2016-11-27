package com.sh.shchecklist.main;

import com.sh.shchecklist.common.BasicInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MemoDatabase {

    public static final String TAG = "MemoDatabase";

    // 싱글톤 인스턴스
    private static MemoDatabase mMemoDatabase;

    public static int DATABASE_VERSION = 1;

    // Helper class defined
    private DatabaseHelper mDBHelper;

    // SQLiteDatabase 인스턴스
    private SQLiteDatabase mMemoDB;

    // 컨텍스트 객체
    private Context mContext;

    // 생성자
    private MemoDatabase(Context context) {
        this.mContext = context;
    }

    // 인스턴스 가져오기
    public static MemoDatabase getInstance(Context context) {
        if (mMemoDatabase == null)
            mMemoDatabase = new MemoDatabase(context);

        return mMemoDatabase;
    }

    // 데이터베이스 열기
    public boolean open() {
        Log.d(TAG, "opening database [" + BasicInfo.DATABASE_NAME + "].\n");

        mDBHelper = new DatabaseHelper(mContext);
        mMemoDB = mDBHelper.getWritableDatabase();

        return true;
    }

    // 데이터베이스 닫기
    public void close() {
        Log.d(TAG, "closing database [" + BasicInfo.DATABASE_NAME + "].\n");
        mMemoDB.close();

        mMemoDatabase = null;
    }

    public Cursor rawQuery(String SQL) {
        Log.d(TAG, "executeQuery called.\n");

        Cursor c1 = null;
        try {
            c1 = mMemoDB.rawQuery(SQL, null);
            Log.d(TAG, "cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery\n", ex);
        }

        return c1;
    }

    public boolean execSQL(String SQL) {
        Log.d(TAG, "execute called.\n");

        try {
            Log.d(TAG, "SQL : " + SQL);
            mMemoDB.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery\n", ex);
            return false;
        }

        return true;
    }



    // Database Helper inner class
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, BasicInfo.DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "creating database [" + BasicInfo.DATABASE_NAME + "].\n");

            // TABLE_MEMO
            Log.d(TAG, "creating table [" + BasicInfo.TABLE_MEMO + "].\n");

            // drop existing table
            String DROP_SQL = "DROP TABLE IF EXISTS " + BasicInfo.TABLE_MEMO;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL\n", ex);
            }

            // create table
            String CREATE_SQL = "CREATE TABLE " + BasicInfo.TABLE_MEMO + "("
                    + "MemoId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "MemoTitle TEXT DEFAULT 'Memo Title', "
                    + "TotalCount INTEGER DEFAULT (0), "
                    + "CheckCount INTEGER DEFAULT (0), "
                    + "CreateDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "Priority INTEGER DEFAULT (0)"
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL\n", ex);
            }

            try {
            	/*
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_MEMO + "(MemoTitle, Priority, TotalCount, CheckCount) VALUES ('일번 메모', 0, 3, 1);" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_MEMO + "(MemoTitle, Priority, TotalCount, CheckCount) VALUES ('이번 메모', 1, 7, 3);" );
            	*/
            } catch(Exception ex) {
                Log.e(TAG, "Exception in insert SQL", ex);
            }
        }

        public void onOpen(SQLiteDatabase db)
        {
            Log.d(TAG, "opened database [" + BasicInfo.DATABASE_NAME + "].\n");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ".\n");
        }
    }
}