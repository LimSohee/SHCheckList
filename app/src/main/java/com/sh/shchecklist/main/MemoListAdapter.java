package com.sh.shchecklist.main;

import java.io.IOException;
import java.util.ArrayList;

import com.sh.shchecklist.check.CheckDatabase;
import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.common.FileManager;
import com.sh.shchecklist.R;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

public class MemoListAdapter extends BaseAdapter {
    public static final String TAG = "MemoListAdapter";

    private Context mContext;
    private ArrayList<MemoListItem> mItems = new ArrayList<MemoListItem>();
    public static MemoDatabase mMemoDatabase = null;

    private static int mMemoListMode;

    public MemoListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getMemoListMode() {
        return mMemoListMode;
    }

    public void setMemoListMode(int MemoListMode) {
        MemoListAdapter.mMemoListMode = MemoListMode;
    }

    // memoId로 Position값 찾는 함수
    public int getPosition(int memoId) {
        int size = getCount();
        for(int position=0; position<size; position++){
            if(mItems.get(position).getMemoId() == memoId)
                return position;
        }
        return -1;
    }

    // 해당 memoId의 memo의 Data을 변경해주는 함수
    public void setItemData(int memoId, String memotitle, int checkCount, int totalCount) {
        int position = getPosition(memoId);
        mItems.get(position).setMemoTitle(memotitle);
        mItems.get(position).setCheckCount(checkCount);
        mItems.get(position).setTotalCount(totalCount);
    }

    public void addItem(MemoListItem listItemData) {
        mItems.add(listItemData);
    }

    public void removeItem(int position) {
        mItems.remove(position);
    }

    public void changeItem(int to, int from) {
        MemoListItem fromItem = mItems.get(from);

        mItems.remove(from);
        mItems.add(to, fromItem);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemoListItemView itemView;

        if(convertView == null)
            itemView = new MemoListItemView(mContext);
        else
            itemView = (MemoListItemView) convertView;

        itemView.setTitle(mItems.get(position).getMemoTitle());
        String count = "(" + mItems.get(position).getCheckCount() + "/" + mItems.get(position).getTotalCount() + ")";
        itemView.setCount(count);
        itemView.setPriority(mItems.get(position).getPriority());
        itemView.setProgressbar(mItems.get(position).getTotalCount(), mItems.get(position).getCheckCount());

        final ImageButton priorityBtn = (ImageButton) itemView.findViewById(R.id.imageButton_priority);
        final ImageButton memoItemDeleteBtn = (ImageButton) itemView.findViewById(R.id.imageButton_memoitem_delete);
        final ImageButton memoItemExportBtn = (ImageButton) itemView.findViewById(R.id.imageButton_memoitem_export);
        if(mMemoListMode == BasicInfo.MODE_PRIORITY){
            priorityBtn.setVisibility(View.VISIBLE);
            memoItemDeleteBtn.setVisibility(View.GONE);
            memoItemExportBtn.setVisibility(View.GONE);
        }
        else if(mMemoListMode == BasicInfo.MODE_DELETE){
            priorityBtn.setVisibility(View.GONE);
            memoItemDeleteBtn.setVisibility(View.VISIBLE);
            memoItemExportBtn.setVisibility(View.GONE);
        }
        else if(mMemoListMode == BasicInfo.MODE_EXPORT){
            priorityBtn.setVisibility(View.GONE);
            memoItemDeleteBtn.setVisibility(View.GONE);
            memoItemExportBtn.setVisibility(View.VISIBLE);
        }

        // 중요도 버튼 Click했을 때
        // On->Off, Off->On으로 변경
        priorityBtn.setTag(position);
        priorityBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "중요도 버튼 Click");

                mMemoDatabase = MemoDatabase.getInstance(mContext);
                String UPDATE_SQL = null;
                int position = (Integer)priorityBtn.getTag();
                int memoId = mItems.get(position).getMemoId();
                if(mItems.get(position).getPriority() == 0) {
                    UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_MEMO + " SET Priority = 1 WHERE MemoId = " + memoId;
                    mItems.get(position).setPriority(1);
                    priorityBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);

                }
                else {
                    UPDATE_SQL = "UPDATE " + BasicInfo.TABLE_MEMO + " SET Priority = 0 WHERE MemoId = " + memoId;
                    mItems.get(position).setPriority(0);
                    priorityBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                }
                mMemoDatabase.execSQL(UPDATE_SQL);
                notifyDataSetChanged();
            }
        });
        priorityBtn.setFocusable(false);

        // memo 삭제 버튼 Click했을 때
        // 메모 삭제
        memoItemDeleteBtn.setTag(position);
        memoItemDeleteBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "메모 삭제 버튼 Click");

                mMemoDatabase = MemoDatabase.getInstance(mContext);
                int position = (Integer)memoItemDeleteBtn.getTag();
                int memoId = mItems.get(position).getMemoId();
                String DELETE_SQL = "DELETE FROM " + BasicInfo.TABLE_MEMO + " WHERE MemoId = " + memoId;

                mItems.remove(position);
                mMemoDatabase.execSQL(DELETE_SQL);
                notifyDataSetChanged();
            }
        });
        memoItemDeleteBtn.setFocusable(false);

        // memo 내보내기 버튼 Click했을 때
        // 메모 내보내기
        memoItemExportBtn.setTag(position);
        memoItemExportBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "메모 내보내기 버튼 Click");

                CheckDatabase checkDatabase = CheckDatabase.getInstance(mContext);
                boolean isOpen = checkDatabase.open();
                if (isOpen) {
                    Log.d(TAG, "Check database is open.");
                } else {
                    Log.d(TAG, "Check database is not open.");
                }

                if (checkDatabase != null) {
                    int position = (Integer)memoItemExportBtn.getTag();
                    int memoId = mItems.get(position).getMemoId();
                    String SQL = "SELECT CheckId, CheckBoxTEXT, Description, IsChecked FROM " + BasicInfo.TABLE_CHECK +  " WHERE MemoId = " + memoId + " ORDER BY CheckId desc";
                    Cursor outCursor = checkDatabase.rawQuery(SQL);

                    if (outCursor != null) {
                        int size = outCursor.getCount();
                        String fileContent = "";

                        for(int i=0;i<size;i++){
                            outCursor.moveToNext();

                            String checkboxText = outCursor.getString(1);
                            String description = outCursor.getString(2);
                            int isChecked = outCursor.getInt(3);

                            if(isChecked == 0)
                                fileContent += "X|";
                            else
                                fileContent += "O|";
                            fileContent += checkboxText;
                            fileContent += "|";
                            fileContent += description;
                            fileContent += "\n";
                        }
                        outCursor.close();

                        String memoTitle = mItems.get(position).getMemoTitle();
                        try {
                            FileManager.WriteFile(mContext, memoTitle, fileContent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(mContext, memoTitle + mContext.getResources().getString(R.string.sucess_export), Toast.LENGTH_LONG).show();

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, fileContent);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                    else
                        Log.d(TAG, "CheckListData loading is faild. - cursor is null.");
                }
            }
        });
        memoItemExportBtn.setFocusable(false);

        return itemView;
    }

    protected void startActivity(Intent sendIntent) {
        // TODO Auto-generated method stub

    }

    public void clear() {
        mItems.clear();
    }

}
