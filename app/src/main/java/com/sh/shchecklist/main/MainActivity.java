package com.sh.shchecklist.main;

import android.os.Bundle;

import com.sh.shchecklist.R;
import com.sh.shchecklist.check.CheckActivity;
import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.common.ExplorerActivity;
import com.sh.shchecklist.widget.WidgetProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class MainActivity extends Activity{

    public static final String TAG = "MainActivity";

    private static ImageButton mNewMemoBtn;
    private static ImageButton mMemoDeleteBtn;
    private static ImageButton mMemoImportBtn;
    private static ImageButton mMemoExportBtn;
    private static Button mMemoCompleteBtn;

    private static ListView mMemoListView;
    private static MemoListAdapter mMemoListAdapter;

    public static MemoDatabase mMemoDatabase = null;

    private static int mMemoListMode;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "======================= onDestroy() =======================");

        if(CheckActivity.widgetChangeType == BasicInfo.WIDGET_UPDATE_TITLE_AND_LIST) {
            Context context = WidgetProvider.mContext;
            if(context != null) {
                Intent intent = new Intent();
                intent.setAction(BasicInfo.ACTION_WIDGET_CHANGE_LIST);
                context.sendBroadcast(intent);
            }
        }
        else if(CheckActivity.widgetChangeType == BasicInfo.WIDGET_UPDATE_ONLY_LIST) {
            Context context = WidgetProvider.mContext;
            if(context != null) {
                Intent intent = new Intent();
                intent.setAction(BasicInfo.ACTION_WIDGET_UPDATE_LIST);
                context.sendBroadcast(intent);
            }
        }

        if (mMemoDatabase != null) {
            mMemoDatabase.close();
            mMemoDatabase = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "======================= onCreate() =======================");

        setContentView(R.layout.activity_main);

        mMemoListView = (ListView) this.findViewById(R.id.listView_memolist);
        mMemoListAdapter = new MemoListAdapter(this);
        mMemoListView.setAdapter(mMemoListAdapter);
        mMemoListMode = BasicInfo.MODE_PRIORITY;
        mMemoListAdapter.setMemoListMode(mMemoListMode);
        mMemoListView.setDividerHeight(2);

        // Memolist의 item을 Click했을 때
        mMemoListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "======================= MemoList의 item - onItemClick() =======================");

                if(mMemoListMode == BasicInfo.MODE_PRIORITY)
                {
                    MemoListItem item = (MemoListItem)mMemoListAdapter.getItem(position);

                    Intent viewMemoIntent = new Intent(getApplicationContext(), CheckActivity.class);
                    viewMemoIntent.putExtra(BasicInfo.KEY_CHECKLIST_MODE, BasicInfo.MODE_VIEW);
                    viewMemoIntent.putExtra(BasicInfo.KEY_MEMO_TITLE, item.getMemoTitle());
                    viewMemoIntent.putExtra(BasicInfo.KEY_MEMO_ID, item.getMemoId());
                    startActivityForResult(viewMemoIntent, BasicInfo.REQ_VIEW_ACTIVITY);
                }
            }
        });

        // 새 메모 추가 버튼을 Click했을 때
        mNewMemoBtn = (ImageButton) findViewById(R.id.button_newMemo_insert);
        mNewMemoBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "======================= 새 메모 추가 버튼 - onItemClick() =======================");

                // 새 메모 DB에 추가
                String newMemoTitle = "";
                String ADD_SQL = "insert into " + BasicInfo.TABLE_MEMO + "(MemoTitle) values ('" + newMemoTitle + "');";
                mMemoDatabase.execSQL(ADD_SQL);

                String SELECT_SQL = "SELECT MemoId, CreateDate FROM " + BasicInfo.TABLE_MEMO + " WHERE MemoTitle = '" + newMemoTitle + "' AND TotalCount = 0 AND CheckCount = 0 AND Priority = 0";
                Cursor outCursor = mMemoDatabase.rawQuery(SELECT_SQL);
                outCursor.moveToNext();
                int memoId = outCursor.getInt(0);
                String createDate = outCursor.getString(1);
                mMemoListAdapter.addItem(new MemoListItem(memoId, newMemoTitle, 0, 0, createDate, 0));

                // 새 메모 추가 창 띄우기
                Intent newMemoIntent = new Intent(getApplicationContext(), CheckActivity.class);
                newMemoIntent.putExtra(BasicInfo.KEY_CHECKLIST_MODE, BasicInfo.MODE_INSERT);
                newMemoIntent.putExtra(BasicInfo.KEY_MEMO_TITLE, newMemoTitle);
                newMemoIntent.putExtra(BasicInfo.KEY_MEMO_ID, memoId);
                startActivityForResult(newMemoIntent, BasicInfo.REQ_INSERT_ACTIVITY);
            }
        });

        // 완료 버튼을 Click했을 때
        mMemoCompleteBtn = (Button) findViewById(R.id.button_memo_complete);
        mMemoCompleteBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "======================= 완료 버튼 - onItemClick() =======================");

                changeMode(BasicInfo.MODE_PRIORITY);
                mMemoCompleteBtn.setVisibility(View.GONE);
                mMemoListAdapter.setMemoListMode(mMemoListMode);
                mMemoListAdapter.notifyDataSetChanged();
            }
        });

        // 메모 삭제 버튼을 Click했을 때
        mMemoDeleteBtn = (ImageButton) findViewById(R.id.button_memo_delete);
        mMemoDeleteBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "======================= 메모 삭제 버튼 - onItemClick() =======================");

                changeMode(BasicInfo.MODE_DELETE);
                mMemoCompleteBtn.setVisibility(View.VISIBLE);
                mMemoListAdapter.setMemoListMode(mMemoListMode);
                mMemoListAdapter.notifyDataSetChanged();
            }
        });

        // 메모 불러오기 버튼을 Click했을 때
        mMemoImportBtn = (ImageButton) findViewById(R.id.button_memo_import);
        mMemoImportBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "======================= 메모 불러오기 버튼 - onItemClick() =======================");

                // 파일탐색기 창 띄우기
                Intent explorerIntent = new Intent(getApplicationContext(), ExplorerActivity.class);
                startActivityForResult(explorerIntent, BasicInfo.REQ_EXPLORER_ACTIVITY);
            }
        });

        // 메모 내보내기 버튼을 Click했을 때
        mMemoExportBtn = (ImageButton) findViewById(R.id.button_memo_export);
        mMemoExportBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "======================= 메모 내보내기 버튼 - onItemClick() =======================");

                changeMode(BasicInfo.MODE_EXPORT);
                mMemoCompleteBtn.setVisibility(View.VISIBLE);
                mMemoListAdapter.setMemoListMode(mMemoListMode);
                mMemoListAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void onStart() {
        // 데이터베이스 열기
        openMemoDatabase();
        // 메모 데이터 로딩
        loadMemoListData();

        super.onStart();
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
                Log.d(TAG, "MemoListData loading is faild. - cursor is null.");
        }

        return recordCount;
    }

    // Mode에 따라 UI 변경
    public void changeMode(int mode) {
        if(mode == BasicInfo.MODE_PRIORITY){
            mMemoListMode = BasicInfo.MODE_PRIORITY;

            mNewMemoBtn.setVisibility(View.VISIBLE);
            mMemoDeleteBtn.setVisibility(View.VISIBLE);
            mMemoImportBtn.setVisibility(View.VISIBLE);
            mMemoExportBtn.setVisibility(View.VISIBLE);
            mMemoCompleteBtn.setVisibility(View.GONE);
        }
        else if(mode == BasicInfo.MODE_DELETE){
            mMemoListMode = BasicInfo.MODE_DELETE;

            mNewMemoBtn.setVisibility(View.GONE);
            mMemoDeleteBtn.setVisibility(View.GONE);
            mMemoImportBtn.setVisibility(View.GONE);
            mMemoExportBtn.setVisibility(View.GONE);
            mMemoCompleteBtn.setVisibility(View.VISIBLE);
        }
        else if(mode == BasicInfo.MODE_EXPORT){
            mMemoListMode = BasicInfo.MODE_EXPORT;

            mNewMemoBtn.setVisibility(View.GONE);
            mMemoDeleteBtn.setVisibility(View.GONE);
            mMemoImportBtn.setVisibility(View.GONE);
            mMemoExportBtn.setVisibility(View.GONE);
            mMemoCompleteBtn.setVisibility(View.VISIBLE);
        }
    }

    //다른 액티비티의 응답 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case BasicInfo.REQ_INSERT_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    int memoId = data.getExtras().getInt(BasicInfo.KEY_MEMO_ID);
                    String memoTitle = data.getExtras().getString(BasicInfo.KEY_MEMO_TITLE);
                    int checkCount = data.getExtras().getInt(BasicInfo.KEY_CHECK_COUNT);
                    int totalCount = data.getExtras().getInt(BasicInfo.KEY_TOTAL_COUNT);
                    String UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_MEMO + " SET MemoTitle = '"+ memoTitle + "', CheckCount = " + checkCount +", TotalCount = " + totalCount +" WHERE MemoId = " + memoId;
                    mMemoDatabase.execSQL(UPDATE_SQL);
                    mMemoListAdapter.setItemData(memoId, memoTitle, checkCount, totalCount);

                    mMemoListAdapter.notifyDataSetChanged();
                }
                else if(resultCode == RESULT_CANCELED) {
                    // 새 메모 추가 취소시, 새 메모 DB에서 삭제
                    int memoId = data.getExtras().getInt(BasicInfo.KEY_MEMO_ID);
                    String DELETE_SQL = "DELETE FROM " + BasicInfo.TABLE_MEMO + " WHERE MemoId = " + memoId;
                    mMemoDatabase.execSQL(DELETE_SQL);
                    mMemoListAdapter.removeItem(mMemoListAdapter.getPosition(memoId));
                }
                break;
            case BasicInfo.REQ_VIEW_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    int memoId = data.getExtras().getInt(BasicInfo.KEY_MEMO_ID);
                    String memoTitle = data.getExtras().getString(BasicInfo.KEY_MEMO_TITLE);
                    int checkCount = data.getExtras().getInt(BasicInfo.KEY_CHECK_COUNT);
                    int totalCount = data.getExtras().getInt(BasicInfo.KEY_TOTAL_COUNT);
                    String UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_MEMO + " SET MemoTitle = '"+ memoTitle + "', CheckCount = " + checkCount +", TotalCount = " + totalCount +" WHERE MemoId = " + memoId;
                    mMemoDatabase.execSQL(UPDATE_SQL);
                    mMemoListAdapter.setItemData(memoId, memoTitle, checkCount, totalCount);

                    mMemoListAdapter.notifyDataSetChanged();
                }
                else if(resultCode == RESULT_FIRST_USER) {
                    int memoId = data.getExtras().getInt(BasicInfo.KEY_MEMO_ID);
                    String DELETE_SQL = "DELETE FROM " + BasicInfo.TABLE_MEMO + " WHERE MemoId = " + memoId;
                    mMemoDatabase.execSQL(DELETE_SQL);
                    mMemoListAdapter.removeItem(mMemoListAdapter.getPosition(memoId));
                }
                break;
            case BasicInfo.REQ_EXPLORER_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    String filePath = data.getExtras().getString(BasicInfo.KEY_FILEPATH);
                    String fileName = data.getExtras().getString(BasicInfo.KEY_FILENAME);

                    // 새 메모 추가
                    String ADD_SQL = "insert into " + BasicInfo.TABLE_MEMO + "(MemoTitle) values ('" + fileName + "');";
                    mMemoDatabase.execSQL(ADD_SQL);

                    String SELECT_SQL = "SELECT MemoId, CreateDate FROM " + BasicInfo.TABLE_MEMO + " WHERE MemoTitle = '" + fileName + "' AND TotalCount = 0 AND CheckCount = 0 AND Priority = 0";
                    Cursor outCursor = mMemoDatabase.rawQuery(SELECT_SQL);
                    outCursor.moveToNext();
                    int memoId = outCursor.getInt(0);
                    String createDate = outCursor.getString(1);
                    mMemoListAdapter.addItem(new MemoListItem(memoId, fileName, 0, 0, createDate, 0));

                    Intent viewMemoIntent = new Intent(getApplicationContext(), CheckActivity.class);
                    viewMemoIntent.putExtra(BasicInfo.KEY_CHECKLIST_MODE, BasicInfo.MODE_IMPORT);
                    viewMemoIntent.putExtra(BasicInfo.KEY_MEMO_TITLE, fileName);
                    viewMemoIntent.putExtra(BasicInfo.KEY_MEMO_ID, memoId);
                    viewMemoIntent.putExtra(BasicInfo.KEY_FILEPATH, filePath);
                    startActivityForResult(viewMemoIntent, BasicInfo.REQ_VIEW_ACTIVITY);
                }
                break;
        }
        mMemoListMode = BasicInfo.MODE_PRIORITY;
        mMemoCompleteBtn.setVisibility(View.GONE);
        mMemoListAdapter.setMemoListMode(mMemoListMode);
        mMemoListAdapter.notifyDataSetChanged();
    }
}
