package com.sh.shchecklist.widget;

import java.util.ArrayList;

import com.sh.shchecklist.R;
import com.sh.shchecklist.check.CheckDatabase;
import com.sh.shchecklist.check.CheckListItem;
import com.sh.shchecklist.common.BasicInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class ListProvider implements RemoteViewsFactory {

    public static final String TAG = "ListProvider";

    private Context mContext;

    private ArrayList<CheckListItem> mItems = new ArrayList<CheckListItem>();
    public static CheckDatabase mCheckDatabase = null;

    private int mMemoId = -1;

    public ListProvider(Context context, Intent intent) {
        Log.i(TAG, "======================= ListProvider() =======================");
        mContext = context;

        // 데이터베이스 열기
        openCheckDatabase();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.i(TAG, "======================= getViewAt() =======================");

        final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_listitem_check);
        CheckListItem listItem = mItems.get(position);
        remoteView.setTextViewText(R.id.textView_widget_checkbox_text, listItem.getCheckBoxText());

        if(listItem.getIsChecked() == 1)
            remoteView.setImageViewResource(R.id.imageButton_checkbox, R.drawable.ic_checkbox_on);
        else
            remoteView.setImageViewResource(R.id.imageButton_checkbox, R.drawable.ic_checkbox_off);

        return remoteView;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "======================= onCreate() =======================");
    }

    @Override
    public void onDataSetChanged() {
        Log.i(TAG, "======================= onDataSetChanged() =======================");

        SharedPreferences pref = mContext.getSharedPreferences(BasicInfo.FILENAME, Activity.MODE_PRIVATE);
        mMemoId = pref.getInt(BasicInfo.KEY_MEMO_ID, 0);

        openCheckDatabase();
        loadCheckListData(mMemoId, "CheckId", 0);
        closeCheckDatabase();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "======================= onDestroy() =======================");
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.i(TAG, "======================= getLoadingView() =======================");
        return null;
    }

    @Override
    public int getViewTypeCount() {
        Log.i(TAG, "======================= getViewTypeCount() =======================");
        return mItems.size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
    public void openCheckDatabase() {
        if (mCheckDatabase != null) {
            mCheckDatabase.close();
            mCheckDatabase = null;
        }

        mCheckDatabase = CheckDatabase.getInstance(mContext);
        boolean isOpen = mCheckDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Check database is open.");
        } else {
            Log.e(TAG, "Check database is not open.");
        }
    }

    // 데이터베이스 닫기 (데이터베이스가 없을 때는 만들기)
    public void closeCheckDatabase() {
        if (mCheckDatabase != null) {
            mCheckDatabase.close();
            mCheckDatabase = null;
        }
    }

    // 메모 리스트 데이터 로딩
    public int loadCheckListData(int memoId, String standard, int direction) {
        Log.i(TAG, "======================= loadCheckListData() =======================");

        String SELECT_SQL = "";
        if(direction == 0)
            SELECT_SQL = "SELECT CheckId, CheckBoxTEXT, Description, IsChecked FROM " + BasicInfo.TABLE_CHECK +  " WHERE MemoId = " + memoId + " ORDER BY " + standard + " desc";
        else
            SELECT_SQL = "SELECT CheckId, CheckBoxTEXT, Description, IsChecked FROM " + BasicInfo.TABLE_CHECK +  " WHERE MemoId = " + memoId + " ORDER BY " + standard + " asc";

        int recordCount = -1;
        if (mCheckDatabase != null) {
            Cursor outCursor = mCheckDatabase.rawQuery(SELECT_SQL);

            if (outCursor != null) {
                recordCount = outCursor.getCount();
                Log.d(TAG, "cursor count : " + recordCount + "\n");

                mItems.clear();
                for (int i = 0; i < recordCount; i++) {
                    outCursor.moveToNext();

                    int checkId = outCursor.getInt(0);
                    String checkboxText = outCursor.getString(1);
                    String description = outCursor.getString(2);
                    int isChecked = outCursor.getInt(3);

                    CheckListItem listItem = new CheckListItem(memoId, checkId, checkboxText, description, isChecked);
                    mItems.add(listItem);
                }

                outCursor.close();
            }
            else
                Log.e(TAG, "CheckListData loading is faild. - cursor is null.");
        }

        return recordCount;
    }
}