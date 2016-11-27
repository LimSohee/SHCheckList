package com.sh.shchecklist.check;

import java.io.IOException;
import java.util.ArrayList;

import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.common.FileManager;
import com.sh.shchecklist.common.Searcher;
import com.sh.shchecklist.dialog.SortDialog;
import com.sh.shchecklist.R;
import com.sh.shchecklist.widget.WidgetProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;

public class CheckActivity extends Activity {
    public static final String TAG = "CheckActivity";

    RelativeLayout mViewModeTitleLayout;
    RelativeLayout mModifyModeTitleLayout;
    RelativeLayout mSearchLayout;
    RelativeLayout mAddLayout;

    private static TextView mViewModeCheckList_Title;
    private static EditText mModifyModeCheckList_Title;

    private static Button mModifyCheckListBtn;
    private static ImageButton mAddCheckItemBtn;
    private static Button mCompleteCheckListBtn;

    private static EditText mAddCheckItem_Text;
    private static EditText mSearchCheckItem_Text;

    private static ListView mCheckListView;
    private static CheckListAdapter mCheckListAdapter;

    public static CheckDatabase mCheckDatabase = null;

    private static int mCheckListMode;
    private static String mMemoTitle;
    private static int mMemoId = -1;

    public static int widgetChangeType = BasicInfo.WIDGET_UPDATE_DONE;

    protected void onDestroy() {
        super.onDestroy();

        if (mCheckDatabase != null) {
            mCheckDatabase.close();
            mCheckDatabase = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        mViewModeTitleLayout = (RelativeLayout) findViewById(R.id.layout_checklist_viewmode_title);
        mModifyModeTitleLayout = (RelativeLayout) findViewById(R.id.layout_checklist_modifymode_title);
        mSearchLayout = (RelativeLayout) findViewById(R.id.layout_checkitem_search);
        mAddLayout = (RelativeLayout) findViewById(R.id.layout_checkitem_add);
        mViewModeCheckList_Title = (TextView) findViewById(R.id.textView_checklist_viewmode_title);
        mModifyModeCheckList_Title = (EditText) findViewById(R.id.editText_checklist_modifymode_title);
        mAddCheckItem_Text = (EditText) findViewById(R.id.editText_add_checkitem_text);
        mSearchCheckItem_Text = (EditText) findViewById(R.id.editText_search_checkitem_text);

        // MainActivity에서 보낸 데이터 가져오기
        Intent receivedItent = getIntent();
        mCheckListMode = receivedItent.getIntExtra(BasicInfo.KEY_CHECKLIST_MODE, -1);
        mMemoTitle = receivedItent.getStringExtra(BasicInfo.KEY_MEMO_TITLE);
        mMemoId = receivedItent.getIntExtra(BasicInfo.KEY_MEMO_ID, -1);

        // CheckList구성을 위해 Adapter 설정
        mCheckListView = (ListView) findViewById(R.id.listView_checklist);
        mCheckListAdapter = new CheckListAdapter(this);
        mCheckListAdapter.setCheckListMode(mCheckListMode);
        mCheckListView.setAdapter(mCheckListAdapter);

        // Checklist의 item을 Click했을 때
        mCheckListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "CheckListItem Click");

                int AdapterType = mCheckListAdapter.getAdapterType();

                CheckListItem clickedCheckListItem = (CheckListItem) mCheckListAdapter.getItem(position);
                int checkId = clickedCheckListItem.getCheckId();
                int isChecked = clickedCheckListItem.getIsChecked();
                if(isChecked == 0)
                    isChecked = 1;
                else
                    isChecked = 0;

                clickedCheckListItem.setIsChecked(isChecked);

                if(mCheckListMode == BasicInfo.MODE_VIEW){
                    String UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_CHECK + " SET IsChecked = " + isChecked + " WHERE CheckId = " + checkId;
                    mCheckDatabase.execSQL(UPDATE_SQL);

                    Context context = WidgetProvider.mContext;
                    if(context != null) {
                        SharedPreferences pref = context.getSharedPreferences(BasicInfo.FILENAME, Activity.MODE_PRIVATE);
                        int widgetMemoId = pref.getInt(BasicInfo.KEY_MEMO_ID, 0);
                        if(widgetMemoId == mMemoId)
                            CheckActivity.widgetChangeType = BasicInfo.WIDGET_UPDATE_ONLY_LIST;
                    }
                }

                if(AdapterType == BasicInfo.ADAPTERTYPE_SEARCHITEM){
                    if(mCheckListMode == BasicInfo.MODE_VIEW)
                        mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                    else if(mCheckListMode == BasicInfo.MODE_MODIFY)
                        mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);

                    // searchAdapter를 보여주고 있던 상태에서 Checkbox 상태를 변경할 때, mCheckListAdapter 또는 mModifyCheckListAdapter도 같이 변경해준다.
                    int position2 = mCheckListAdapter.getPosition(checkId);
                    clickedCheckListItem = (CheckListItem) mCheckListAdapter.getItem(position2);
                    clickedCheckListItem.setIsChecked(isChecked);

                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_SEARCHITEM);
                }

                mCheckListAdapter.notifyDataSetChanged();
            }
        });

        // CheckList 편집 버튼을 Click했을 때
        mModifyCheckListBtn = (Button) findViewById(R.id.button_checklist_modify);
        mModifyCheckListBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "편집 버튼 Click");

                mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                ArrayList<CheckListItem> item = mCheckListAdapter.getItems();

                mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                mCheckListAdapter.copyItems(item);
                changeMode(BasicInfo.MODE_MODIFY);
            }
        });

        // CheckList 편집 완료 버튼을 Click했을 때
        mCompleteCheckListBtn = (Button) findViewById(R.id.button_checklist_complete);
        mCompleteCheckListBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "완료 버튼 Click");

                mMemoTitle = mModifyModeCheckList_Title.getText().toString();

                mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                if(mCheckListMode == BasicInfo.MODE_INSERT){
                    Intent closeItent = new Intent();
                    closeItent.putExtra(BasicInfo.KEY_MEMO_ID, mMemoId);
                    closeItent.putExtra(BasicInfo.KEY_MEMO_TITLE, mMemoTitle);
                    closeItent.putExtra(BasicInfo.KEY_CHECK_COUNT, mCheckListAdapter.getCheckCount());
                    closeItent.putExtra(BasicInfo.KEY_TOTAL_COUNT, mCheckListAdapter.getCount());
                    setResult(RESULT_OK, closeItent);
                    finish();
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                    mCheckListAdapter.clear();
                }
                else{
                    ArrayList<CheckListItem> item = mCheckListAdapter.getItems();
                    saveCheckListData();
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                    mCheckListAdapter.copyItems(item);
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                    mCheckListAdapter.clear();
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                    changeMode(BasicInfo.MODE_VIEW);


                    // widget에서 보여주고 있는 checklist의 값이 변경되면, memoId, memoTitle 값 저장
                    Context context = WidgetProvider.mContext;
                    if(context != null) {
                        SharedPreferences pref = context.getSharedPreferences(BasicInfo.FILENAME, Activity.MODE_PRIVATE);
                        int memoId = pref.getInt(BasicInfo.KEY_MEMO_ID, 0);
                        if(memoId == mMemoId) {
                            CheckActivity.widgetChangeType = BasicInfo.WIDGET_UPDATE_TITLE_AND_LIST;

                            Editor e = pref.edit();
                            e.clear();
                            e.putInt(BasicInfo.KEY_MEMO_ID, memoId);
                            e.putString(BasicInfo.KEY_MEMO_TITLE, mMemoTitle);
                            e.commit();
                        }
                    }
                }
            }
        });

        mSearchCheckItem_Text = (EditText) findViewById(R.id.editText_search_checkitem_text);
        mSearchCheckItem_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int BaseAdapterType = BasicInfo.ADAPTERTYPE_CHECKITEM;
                if(mCheckListMode == BasicInfo.MODE_VIEW)
                    BaseAdapterType = BasicInfo.ADAPTERTYPE_CHECKITEM;
                else if(mCheckListMode == BasicInfo.MODE_MODIFY || mCheckListMode == BasicInfo.MODE_INSERT)
                    BaseAdapterType = BasicInfo.ADAPTERTYPE_MODIFYITEM;

                if (s.length() == 0) {
                    // 검색 Text가 없을 때, 전체 목록을 보여준다
                    mCheckListAdapter.ChangeAdapterType(BaseAdapterType);
                }
                else {
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_SEARCHITEM);
                    mCheckListAdapter.clear();

                    mCheckListAdapter.ChangeAdapterType(BaseAdapterType);
                    int size = mCheckListAdapter.getCount();
                    for (int i = 0; i < size; i++) {
                        CheckListItem checkItem = (CheckListItem) mCheckListAdapter.getItem(i);
                        int memoId = checkItem.getMemoId();
                        int checkId = checkItem.getCheckId();
                        String description = checkItem.getDescription();
                        int isChecked = checkItem.getIsChecked();
                        String checkboxText = checkItem.getCheckBoxText();
                        String searchText = s.toString();
                        boolean isData = Searcher.matchString(checkboxText, searchText);

                        if (isData) {
                            mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_SEARCHITEM);
                            mCheckListAdapter.addItem(new CheckListItem(memoId, checkId, checkboxText, description, isChecked));
                            mCheckListAdapter.ChangeAdapterType(BaseAdapterType);
                        }
                    }
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_SEARCHITEM);
                }
                mCheckListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });

        // CheckList 추가 버튼을 Click했을 때
        mAddCheckItemBtn = (ImageButton) findViewById(R.id.button_checkitem_add);
        mAddCheckItemBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "추가 버튼 Click");

                int memoId = mMemoId;
                String checkboxText =  mAddCheckItem_Text.getText().toString();
                String description = "";
                int isChecked = 0;

                String ADD_SQL = "insert into " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT, Description, IsChecked) values (" + memoId + ", '" + checkboxText + "', '" + description + "', 0);";
                mCheckDatabase.execSQL(ADD_SQL);

                String SELECT = "SELECT CheckId FROM " + BasicInfo.TABLE_CHECK +  " WHERE MemoId = " + memoId + " AND CheckBoxTEXT = '" + checkboxText + "'";
                Cursor outCursor = mCheckDatabase.rawQuery(SELECT);
                outCursor.moveToNext();
                int checkId = outCursor.getInt(0);
                mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                mCheckListAdapter.addItem(0, new CheckListItem(memoId, checkId, checkboxText, description, isChecked));
                mCheckListAdapter.notifyDataSetChanged();

                mAddCheckItem_Text.setText("");
                mSearchCheckItem_Text.setText("");
            }
        });
    }

    protected void onStart() {
        // 데이터베이스 열기
        openCheckDatabase();

        if(mCheckListMode == BasicInfo.MODE_INSERT || mCheckListMode == BasicInfo.MODE_MODIFY)
        {
            mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
            mCheckListAdapter.clear();
        }
        else if(mCheckListMode == BasicInfo.MODE_VIEW || mCheckListMode == BasicInfo.MODE_IMPORT)
        {
            mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
            mCheckListAdapter.clear();
        }
        // 메모 데이터 로딩
        loadCheckListData(mMemoId, "CheckId", 0);
        // Mode에 맞게 UI 설정
        changeMode(mCheckListMode);

        super.onStart();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "BackKey Click");

            if(mCheckListMode == BasicInfo.MODE_INSERT){
                AlertDialog.Builder exitCheckDialog = new AlertDialog.Builder(this);
                exitCheckDialog.setMessage(R.string.checklist_new_memo_cancel_message);
                exitCheckDialog.setPositiveButton(R.string.checklist_alertdialog_positivebutton, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 새메모 추가를 BackKey를 눌러 취소했을 때, 추가했던 CheckItem DB에서 삭제
                        String DELETE_SQL = "DELETE FROM " + BasicInfo.TABLE_CHECK + " WHERE MemoId = " + mMemoId;
                        mCheckDatabase.execSQL(DELETE_SQL);

                        Intent closeItent = new Intent();
                        closeItent.putExtra(BasicInfo.KEY_MEMO_ID, mMemoId);
                        setResult(RESULT_CANCELED, closeItent);
                        finish();
                    }
                });
                exitCheckDialog.setNegativeButton(R.string.checklist_alertdialog_negativebutton, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                exitCheckDialog.show();

                return true;
            }
            else if(mCheckListMode == BasicInfo.MODE_MODIFY){
                AlertDialog.Builder exitCheckDialog = new AlertDialog.Builder(this);
                exitCheckDialog.setMessage(R.string.checklist_modify_cancel_message);
                exitCheckDialog.setPositiveButton(R.string.checklist_alertdialog_positivebutton, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                        saveCheckListData();
                        mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                        mCheckListAdapter.clear();
                        mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                        changeMode(BasicInfo.MODE_VIEW);
                    }
                });
                exitCheckDialog.setNegativeButton(R.string.checklist_alertdialog_negativebutton, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                exitCheckDialog.show();

                return true;
            }
            else { // BasicInfo.MODE_VIEW
                mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                Intent closeItent = new Intent();
                closeItent.putExtra(BasicInfo.KEY_MEMO_ID, mMemoId);
                closeItent.putExtra(BasicInfo.KEY_MEMO_TITLE, mMemoTitle);
                closeItent.putExtra(BasicInfo.KEY_TOTAL_COUNT, mCheckListAdapter.getCount());
                closeItent.putExtra(BasicInfo.KEY_CHECK_COUNT, mCheckListAdapter.getCheckCount());
                setResult(RESULT_OK, closeItent);
                finish();
                return true;
            }
        }
        else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_menu_viewmode, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case(R.id.menu_memo_share):{
                Log.d(TAG, "공유하기 메뉴 항목 Click");

                String fileContent = "\n";
                int size = mCheckListAdapter.getCount();
                for(int i=0;i<size;i++){
                    CheckListItem checkItem = (CheckListItem)mCheckListAdapter.getItem(i);
                    if(checkItem.getIsChecked() == 0)
                        fileContent += "X|";
                    else
                        fileContent += "O|";
                    fileContent += checkItem.getCheckBoxText();
                    fileContent += "|";
                    fileContent += checkItem.getDescription();
                    fileContent += "\n";
                }

                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_SUBJECT, mMemoTitle);
                msg.putExtra(Intent.EXTRA_TEXT, fileContent);
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, getResources().getString(R.string.share_title)));

                return  true;
            }
            case(R.id.menu_memo_sort):{
                Log.d(TAG, "정렬하기 MenuItem Click");

                Intent sortIntent = new Intent(getApplicationContext(), SortDialog.class);
                startActivityForResult(sortIntent, BasicInfo.REQ_SORT_DIALOG);
                return true;
            }
            case(R.id.menu_all_select):{
                Log.d(TAG, "전부 선택 Click");

                if(mCheckListMode == BasicInfo.MODE_VIEW)
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                else if(mCheckListMode == BasicInfo.MODE_MODIFY)
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);

                CheckListItem clickedCheckListItem;
                int size = mCheckListAdapter.getCount();
                if(size > 0 )
                {
                    for(int position=0;position<size;position++){
                        clickedCheckListItem = (CheckListItem) mCheckListAdapter.getItem(position);
                        clickedCheckListItem.setIsChecked(1);
                    }

                    if(mCheckListMode == BasicInfo.MODE_VIEW){
                        clickedCheckListItem = (CheckListItem) mCheckListAdapter.getItem(0);
                        String UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_CHECK + " SET IsChecked = 1" + " WHERE MemoId = " + clickedCheckListItem.getMemoId();
                        mCheckDatabase.execSQL(UPDATE_SQL);

                        Context context = WidgetProvider.mContext;
                        if(context != null) {
                            SharedPreferences pref = context.getSharedPreferences(BasicInfo.FILENAME, Activity.MODE_PRIVATE);
                            int widgetMemoId = pref.getInt(BasicInfo.KEY_MEMO_ID, 0);
                            if(widgetMemoId == mMemoId)
                                CheckActivity.widgetChangeType = BasicInfo.WIDGET_UPDATE_ONLY_LIST;
                        }
                    }
                }

                mCheckListAdapter.notifyDataSetChanged();

                return true;
            }
            case(R.id.menu_all_deselect):{
                Log.d(TAG, "전부 해제 Click");

                if(mCheckListMode == BasicInfo.MODE_VIEW)
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_CHECKITEM);
                else if(mCheckListMode == BasicInfo.MODE_MODIFY)
                    mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);

                CheckListItem clickedCheckListItem;
                int size = mCheckListAdapter.getCount();
                if(size > 0 )
                {
                    for(int position=0;position<size;position++){
                        clickedCheckListItem = (CheckListItem) mCheckListAdapter.getItem(position);
                        clickedCheckListItem.setIsChecked(0);
                    }

                    if(mCheckListMode == BasicInfo.MODE_VIEW){
                        clickedCheckListItem = (CheckListItem) mCheckListAdapter.getItem(0);
                        String UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_CHECK + " SET IsChecked = 0" + " WHERE MemoId = " + clickedCheckListItem.getMemoId();
                        mCheckDatabase.execSQL(UPDATE_SQL);

                        Context context = WidgetProvider.mContext;
                        if(context != null) {
                            SharedPreferences pref = context.getSharedPreferences(BasicInfo.FILENAME, Activity.MODE_PRIVATE);
                            int widgetMemoId = pref.getInt(BasicInfo.KEY_MEMO_ID, 0);
                            if(widgetMemoId == mMemoId)
                                CheckActivity.widgetChangeType = BasicInfo.WIDGET_UPDATE_ONLY_LIST;
                        }
                    }
                }
                mCheckListAdapter.notifyDataSetChanged();

                return true;
            }
        }
        return  false;
    }

    // 데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
    public void openCheckDatabase() {
        // open database
        if (mCheckDatabase != null) {
            mCheckDatabase.close();
            mCheckDatabase = null;
        }

        mCheckDatabase = CheckDatabase.getInstance(this);
        boolean isOpen = mCheckDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Check database is open.");
        } else {
            Log.d(TAG, "Check database is not open.");
        }
    }

    // 메모 리스트 데이터 로딩
    public int loadCheckListData(int memoId, String standard, int direction) {
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

                mCheckListAdapter.clear();
                for (int i = 0; i < recordCount; i++) {
                    outCursor.moveToNext();

                    int checkId = outCursor.getInt(0);
                    String checkboxText = outCursor.getString(1);
                    String description = outCursor.getString(2);
                    int isChecked = outCursor.getInt(3);

                    mCheckListAdapter.addItem(new CheckListItem(memoId, checkId, checkboxText, description, isChecked));
                }

                outCursor.close();
                mCheckListAdapter.notifyDataSetChanged();
            }
            else
                Log.d(TAG, "CheckListData loading is faild. - cursor is null.");
        }

        return recordCount;
    }

    // 메모 리스트 데이터 저장
    public void saveCheckListData() {
        String DELETE_SQL = "DELETE FROM " + BasicInfo.TABLE_CHECK + " WHERE MemoId = " + mMemoId;
        mCheckDatabase.execSQL(DELETE_SQL);

        int size = mCheckListAdapter.getCount();
        for(int position=0;position<size;position++){
            CheckListItem item = (CheckListItem) mCheckListAdapter.getItem(position);
            int checkId = item.getCheckId();
            int memoId = item.getMemoId();
            String checkboxText = item.getCheckBoxText();
            String description = item.getDescription();
            int isChecked = item.getIsChecked();

            String ADD_SQL = "insert into " + BasicInfo.TABLE_CHECK + "(CheckId, MemoId, CheckBoxTEXT, Description, IsChecked) values (" + checkId + ", " + memoId + ", '" + checkboxText + "', '" + description + "', " + isChecked + ");";
            mCheckDatabase.execSQL(ADD_SQL);
        }
    }

    // Mode에 따라 UI 변경
    public void changeMode(int mode) {
        if(mode == BasicInfo.MODE_INSERT){
            mCheckListMode = BasicInfo.MODE_INSERT;

            // Memo Title 설정
            mModifyModeCheckList_Title.setText(mMemoTitle);

            // 레이아웃 설정
            mViewModeTitleLayout.setVisibility(View.GONE);
            mModifyModeTitleLayout.setVisibility(View.VISIBLE);
            mSearchLayout.setVisibility(View.VISIBLE);
            mAddLayout.setVisibility(View.VISIBLE);

            mModifyCheckListBtn.setVisibility(View.GONE);
            mCompleteCheckListBtn.setVisibility(View.VISIBLE);

            mCheckListAdapter.setCheckListMode(BasicInfo.MODE_INSERT);
        }
        else if(mode == BasicInfo.MODE_VIEW){
            mCheckListMode = BasicInfo.MODE_VIEW;

            // Memo Title 설정
            mViewModeCheckList_Title.setText(mMemoTitle);

            // 레이아웃 설정
            mViewModeTitleLayout.setVisibility(View.VISIBLE);
            mModifyModeTitleLayout.setVisibility(View.GONE);
            mSearchLayout.setVisibility(View.VISIBLE);
            mAddLayout.setVisibility(View.GONE);

            mModifyCheckListBtn.setVisibility(View.VISIBLE);
            mCompleteCheckListBtn.setVisibility(View.GONE);

            mCheckListAdapter.setCheckListMode(BasicInfo.MODE_VIEW);
        }
        else if(mode == BasicInfo.MODE_MODIFY){
            mCheckListMode = BasicInfo.MODE_MODIFY;

            // Memo Title 설정
            mModifyModeCheckList_Title.setText(mMemoTitle);

            // 레이아웃 설정
            mViewModeTitleLayout.setVisibility(View.GONE);
            mModifyModeTitleLayout.setVisibility(View.VISIBLE);
            mSearchLayout.setVisibility(View.VISIBLE);
            mAddLayout.setVisibility(View.VISIBLE);

            mModifyCheckListBtn.setVisibility(View.GONE);
            mCompleteCheckListBtn.setVisibility(View.VISIBLE);

            mCheckListAdapter.setCheckListMode(BasicInfo.MODE_MODIFY);
        }
        else if(mode == BasicInfo.MODE_IMPORT){
            mCheckListMode = BasicInfo.MODE_VIEW;

            // Memo Title 설정
            mViewModeCheckList_Title.setText(mMemoTitle);

            // 레이아웃 설정
            mViewModeTitleLayout.setVisibility(View.VISIBLE);
            mModifyModeTitleLayout.setVisibility(View.GONE);
            mSearchLayout.setVisibility(View.VISIBLE);
            mAddLayout.setVisibility(View.GONE);

            mModifyCheckListBtn.setVisibility(View.VISIBLE);
            mCompleteCheckListBtn.setVisibility(View.GONE);

            mCheckListAdapter.setCheckListMode(BasicInfo.MODE_VIEW);

            // MainActivity에서 보낸 데이터 가져오기
            Intent receivedItent = getIntent();
            String filePath = receivedItent.getStringExtra(BasicInfo.KEY_FILEPATH);

            // CheckBox 추가
            try {
                String readStr = FileManager.ReadFile(getBaseContext(), filePath);

                String[] checkItem = readStr.split("\n");
                for(int i=0;i<checkItem.length;i++)
                {
                    int memoId = mMemoId;
                    String[] checkData= checkItem[i].split("\\|");
                    if(checkData.length == 2 || checkData.length == 3){
                        String checked = checkData[0].toString();
                        int isChecked = 0;
                        if(checked.equals("X"))
                            isChecked = 0;
                        else
                            isChecked = 1;
                        String checkboxText = checkData[1].toString();
                        String description = "";
                        if(checkData.length == 3)
                            description = checkData[2].toString();

                        String ADD_SQL = "insert into " + BasicInfo.TABLE_CHECK + "(MemoId, CheckBoxTEXT, Description, IsChecked) values (" + memoId + ", '" + checkboxText + "', '" + description + "', " + isChecked + ");";
                        mCheckDatabase.execSQL(ADD_SQL);

                        String SELECT_SQL = "SELECT CheckId FROM " + BasicInfo.TABLE_CHECK +  " WHERE MemoId = " + memoId + " AND CheckBoxTEXT = '" + checkboxText + "'";
                        Cursor outCursor = mCheckDatabase.rawQuery(SELECT_SQL);
                        outCursor.moveToNext();
                        int checkId = outCursor.getInt(0);
                        mCheckListAdapter.addItem(0, new CheckListItem(memoId, checkId, checkboxText, description, isChecked));
                    }
                    else{
                        Intent closeItent = new Intent();
                        closeItent.putExtra(BasicInfo.KEY_MEMO_ID, mMemoId);
                        setResult(RESULT_FIRST_USER, closeItent);
                        finish();

                        Toast.makeText(getApplicationContext(), R.string.fail_import, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                Toast.makeText(getApplicationContext(), mMemoTitle + " " + getResources().getString(R.string.sucess_import), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mAddCheckItem_Text.setText("");
        mSearchCheckItem_Text.setText("");
    }

    //다른 액티비티의 응답 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case BasicInfo.REQ_SORT_DIALOG:
                if(resultCode == RESULT_OK) {
                    String standard = data.getExtras().getString(BasicInfo.KEY_SORT_STANDARD);
                    int direction = data.getExtras().getInt(BasicInfo.KEY_SORT_DIRECTION);

                    // 정렬
                    loadCheckListData(mMemoId, standard, direction);

                    mSearchCheckItem_Text.setText("");
                }
                break;
            case BasicInfo.REQ_DESCRIPTION_DIALOG:
                if(resultCode == RESULT_OK) {
                    String description = data.getExtras().getString(BasicInfo.KEY_DESCRIPTION);
                    int checkId = data.getExtras().getInt(BasicInfo.KEY_CHECK_ID);
                    int position = data.getExtras().getInt(BasicInfo.KEY_CHECKITEM_POSITION);

                    mCheckListAdapter.setDescription(position, description);

                    int AdapterType = mCheckListAdapter.getAdapterType();
                    if(AdapterType == BasicInfo.ADAPTERTYPE_SEARCHITEM){
                        mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                        position = mCheckListAdapter.getPosition(checkId);
                        mCheckListAdapter.setDescription(position, description);
                        mCheckListAdapter.ChangeAdapterType(BasicInfo.ADAPTERTYPE_SEARCHITEM);
                    }

                    String UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_CHECK + " SET Description = '" + description + "' WHERE CheckId = " + checkId;
                    mCheckDatabase.execSQL(UPDATE_SQL);
                }
                break;
        }
    }
}
