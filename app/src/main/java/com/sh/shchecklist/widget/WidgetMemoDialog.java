package com.sh.shchecklist.widget;

import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.main.MemoDatabase;
import com.sh.shchecklist.main.MemoListAdapter;
import com.sh.shchecklist.main.MemoListItem;
import com.sh.shchecklist.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class WidgetMemoDialog extends Activity {

    public static final String TAG = "WidgetMemoDialog";

    public static MemoDatabase mMemoDatabase = null;
    private static MemoListAdapter mMemoListAdapter;
    private ListView mMemoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "======================= onCreate() =======================");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.widget_dialog_memo);

        mMemoListView = (ListView) this.findViewById(R.id.listView_widget_memolist);
        mMemoListAdapter = new MemoListAdapter(this);
        mMemoListView.setAdapter(mMemoListAdapter);
        mMemoListView.setDividerHeight(2);

        // 데이터베이스 열기
        openMemoDatabase();
        // 메모 데이터 로딩
        loadMemoListData();

        // Memolist의 item을 Click했을 때
        mMemoListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "======================= mMemoListView - onItemClick() =======================");

                MemoListItem item = (MemoListItem)mMemoListAdapter.getItem(position);
                Log.d(TAG, "mMemoId = " + item.getMemoId() + ", mMemoTitle = " + item.getMemoId());

                // memoId, memoTitle 값 저장
                final Context context = WidgetProvider.mContext;//WidgetMemoDialog.this;
                SharedPreferences pref = context.getSharedPreferences(BasicInfo.FILENAME, MODE_PRIVATE);
                Editor e = pref.edit();
                e.clear();
                e.putInt(BasicInfo.KEY_MEMO_ID, item.getMemoId());
                e.putString(BasicInfo.KEY_MEMO_TITLE, item.getMemoTitle());
                e.commit();

                // Wdiget의 List를 선택한 Memo로 변경하기 위해 브로드캐스트 날림
                Intent intent = new Intent();
                intent.setAction(BasicInfo.ACTION_WIDGET_CHANGE_LIST);
                context.sendBroadcast(intent);

                finish();
            }
        });
    }

    // 데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
    public void openMemoDatabase() {
        if (mMemoDatabase != null) {
            mMemoDatabase.close();
            mMemoDatabase = null;
        }

        mMemoDatabase = MemoDatabase.getInstance(this);
        boolean isOpen = mMemoDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Memo database is open.");
        } else {
            Log.e(TAG, "Memo database is not open.");
        }
    }

    // 메모 리스트 데이터 로딩
    public int loadMemoListData() {
        String SELECT_SQL = "SELECT * FROM " + BasicInfo.TABLE_MEMO + " ORDER BY CreateDate desc";
        int recordCount = -1;
        if (mMemoDatabase != null) {
            Cursor outCursor = mMemoDatabase.rawQuery(SELECT_SQL);

            if (outCursor != null) {
                recordCount = outCursor.getCount();
                Log.d(TAG, "cursor count : " + recordCount + "\n");

                mMemoListAdapter.clear();
                for (int i = 0; i < recordCount; i++) {
                    outCursor.moveToNext();

                    int memoId = outCursor.getInt(0);
                    String memoTitle = outCursor.getString(1);
                    int totalCount = outCursor.getInt(2);
                    int checkCount = outCursor.getInt(3);
                    String creationDate = outCursor.getString(4);
                    int priority = outCursor.getInt(5);

                    mMemoListAdapter.addItem(new MemoListItem(memoId, memoTitle, totalCount, checkCount, creationDate, priority));
                }

                outCursor.close();
                mMemoListAdapter.notifyDataSetChanged();
            }
            else
                Log.e(TAG, "MemoListData loading is faild. - cursor is null.");
        }

        return recordCount;
    }
}
