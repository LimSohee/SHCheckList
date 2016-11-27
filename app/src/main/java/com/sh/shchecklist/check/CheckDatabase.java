package com.sh.shchecklist.check;

import com.sh.shchecklist.common.BasicInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CheckDatabase {

    public static final String TAG = "mCheckDatabase";

    // 싱글톤 인스턴스
    private static CheckDatabase mCheckDatabase;

    public static int DATABASE_VERSION = 1;

    // Helper class defined
    private DatabaseHelper mDBHelper;

    // SQLiteDatabase 인스턴스
    private SQLiteDatabase mCheckDB;

    // 컨텍스트 객체
    private Context mContext;

    // 생성자
    private CheckDatabase(Context context) {
        this.mContext = context;
    }

    // 인스턴스 가져오기
    public static CheckDatabase getInstance(Context context) {
        if (mCheckDatabase == null)
            mCheckDatabase = new CheckDatabase(context);

        return mCheckDatabase;
    }

    // 데이터베이스 열기
    public boolean open() {
        Log.d(TAG, "opening database [" + BasicInfo.TABLE_CHECK + "].\n");

        mDBHelper = new DatabaseHelper(mContext);
        mCheckDB = mDBHelper.getWritableDatabase();

        return true;
    }

    // 데이터베이스 닫기
    public void close() {
        Log.d(TAG, "closing database [" + BasicInfo.TABLE_CHECK + "].\n");
        mCheckDB.close();

        mCheckDatabase = null;
    }

    public Cursor rawQuery(String SQL) {
        Log.d(TAG, "executeQuery called.\n");

        Cursor c1 = null;
        try {
            c1 = mCheckDB.rawQuery(SQL, null);
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
            mCheckDB.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery\n", ex);
            return false;
        }

        return true;
    }



    // Database Helper inner class
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, BasicInfo.TABLE_CHECK, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "creating database [" + BasicInfo.TABLE_CHECK + "].\n");
            Log.d(TAG, "creating table [" + BasicInfo.TABLE_CHECK + "].\n");

            // drop existing table
            String DROP_SQL = "DROP TABLE IF EXISTS " + BasicInfo.TABLE_CHECK;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL\n", ex);
            }

            // create table
            String CREATE_SQL = "CREATE TABLE " + BasicInfo.TABLE_CHECK + "("
                    + "CheckId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "MemoId INTEGER DEFAULT (0), "
                    + "CheckBoxTEXT TEXT DEFAULT 'ItemText', "
                    + "Description TEXT DEFAULT 'Description', "
                    + "IsChecked INTEGER DEFAULT (0)"
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL\n", ex);
            }

            try {
            	/*
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT, Description, IsChecked) VALUES (1, '1번메모 - 1일', '일 추가설명', 1);" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (1, '1번메모 - 2이');" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (1, '1번메모 - 3삼');" );

            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT, Description, IsChecked) VALUES (2, '2번 메모 - 1일', '일 추가설명', 1);" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (2, '2번 메모 - 2이');" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT, IsChecked) VALUES (2, '2번 메모 - 3삼', 1);" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT, IsChecked) VALUES (2, '2번 메모 - 4사', 1);" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (2, '2번 메모 - 5오');" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (2, '2번 메모 - 6육');" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (2, '2번 메모 - 7칠');" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (2, '2번 메모 - 8팔');" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (2, '2번 메모 - 9구');" );
            	db.execSQL( "INSERT INTO " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT) VALUES (2, '2번 메모 - 10십');" );
            	*/
            } catch(Exception ex) {
                Log.e(TAG, "Exception in insert SQL", ex);
            }
        }

        public void onOpen(SQLiteDatabase db)
        {
            Log.d(TAG, "opened database [" + BasicInfo.TABLE_CHECK + "].\n");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ".\n");
        }
    }
}