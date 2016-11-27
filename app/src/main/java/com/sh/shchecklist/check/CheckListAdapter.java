package com.sh.shchecklist.check;

import java.util.ArrayList;

import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.dialog.DescriptionDialog;
import com.sh.shchecklist.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

public class CheckListAdapter extends BaseAdapter {
    public static final String TAG = "CheckListAdapter";

    private Context mContext;

    public ArrayList<CheckListItem> mItems = new ArrayList<CheckListItem>();
    public static ArrayList<CheckListItem> mCheckItems = new ArrayList<CheckListItem>();
    public static ArrayList<CheckListItem> mSearchItems = new ArrayList<CheckListItem>();
    public static ArrayList<CheckListItem> mModifyItems = new ArrayList<CheckListItem>();
    private static int mAdapterType;

    public static CheckDatabase mCheckDatabase = null;

    private static int mCheckListMode;

    public CheckListAdapter(Context context) {
        mContext = context;
        mAdapterType = BasicInfo.ADAPTERTYPE_CHECKITEM;
        mItems = mCheckItems;
    }

    public void ChangeAdapterType(int itemType){
        mAdapterType = itemType;
        if(itemType == BasicInfo.ADAPTERTYPE_CHECKITEM)
            mItems = mCheckItems;
        else if(itemType == BasicInfo.ADAPTERTYPE_SEARCHITEM)
            mItems = mSearchItems;
        else if(itemType == BasicInfo.ADAPTERTYPE_MODIFYITEM)
            mItems = mModifyItems;
    }

    public int getAdapterType()
    {
        return mAdapterType;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public int getCheckCount() {
        int checkCount = 0;
        for(int i=0;i<mItems.size();i++){
            if(mItems.get(i).getIsChecked() == 1)
                checkCount++;
        }
        return checkCount;
    }

    // checkId로 Position값 찾는 함수
    public int getPosition(int checkId) {
        int size = getCount();
        for(int position=0; position<size; position++){
            if(mItems.get(position).getCheckId() == checkId)
                return position;
        }
        return -1;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(CheckListItem listItem) {
        Log.d(TAG, "addItem = " + listItem.getCheckBoxText());
        mItems.add(listItem);
    }

    public void addItem(int index, CheckListItem listItem) {
        Log.d(TAG, "addItem = " + listItem.getCheckBoxText() + ", index = " + index);
        mItems.add(index, listItem);
    }

    public int getCheckListMode() {
        return mCheckListMode;
    }

    public void setCheckListMode(int checkListMode) {
        Log.d(TAG, "setCheckListMode = " + checkListMode);

        CheckListAdapter.mCheckListMode = checkListMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CheckListItemView itemView;

        if(convertView == null)
            itemView = new CheckListItemView(mContext);
        else
            itemView = (CheckListItemView) convertView;

        itemView.setCheckBoxText(mItems.get(position).getCheckBoxText());
        itemView.setIsChecked(mItems.get(position).getIsChecked());

        // Mode에 따라 Delete, Description 버튼 보이기/숨김
        final ImageButton deleteCheckItemBtn = (ImageButton) itemView.findViewById(R.id.button_checkitem_delete);
        final ImageButton descriptionCheckItemBtn = (ImageButton) itemView.findViewById(R.id.button_checkitem_description);
        if(mCheckListMode == BasicInfo.MODE_INSERT){
            deleteCheckItemBtn.setVisibility(View.VISIBLE);
        }
        else if(mCheckListMode == BasicInfo.MODE_VIEW){
            deleteCheckItemBtn.setVisibility(View.GONE);
        }
        else if(mCheckListMode == BasicInfo.MODE_MODIFY){
            deleteCheckItemBtn.setVisibility(View.VISIBLE);
        }

        // 삭제 버튼을 Click했을 때
        deleteCheckItemBtn.setTag(position);
        deleteCheckItemBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "체크항목 삭제 버튼 Click");

                mCheckDatabase = CheckDatabase.getInstance(mContext);
                int position = (Integer)deleteCheckItemBtn.getTag();
                int checkId = mItems.get(position).getCheckId();
                String DELETE_SQL = "DELETE FROM " + BasicInfo.TABLE_CHECK + " WHERE CheckId = " + checkId;

                if(mAdapterType == BasicInfo.ADAPTERTYPE_SEARCHITEM)
                {
                    int findCheckId = mItems.get(position).getCheckId();
                    ChangeAdapterType(BasicInfo.ADAPTERTYPE_MODIFYITEM);
                    int position2 = getPosition(findCheckId);
                    mModifyItems.remove(position2);
                    ChangeAdapterType(BasicInfo.ADAPTERTYPE_SEARCHITEM);
                }

                mItems.remove(position);
                mCheckDatabase.execSQL(DELETE_SQL);

                notifyDataSetChanged();
            }
        });
        deleteCheckItemBtn.setFocusable(false);

        // description 버튼을 Click했을 때
        descriptionCheckItemBtn.setTag(position);
        descriptionCheckItemBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "description 버튼 Click");

                mCheckDatabase = CheckDatabase.getInstance(mContext);
                int position = (Integer)deleteCheckItemBtn.getTag();
                String description = mItems.get(position).getDescription();

                Intent descriptionIntent = new Intent(mContext, DescriptionDialog.class);
                descriptionIntent.putExtra(BasicInfo.KEY_CHECKLIST_MODE, mCheckListMode);
                descriptionIntent.putExtra(BasicInfo.KEY_DESCRIPTION, description);
                descriptionIntent.putExtra(BasicInfo.KEY_CHECK_ID, mItems.get(position).getCheckId());
                descriptionIntent.putExtra(BasicInfo.KEY_CHECKITEM_POSITION, position);
                ((Activity) mContext).startActivityForResult(descriptionIntent, BasicInfo.REQ_DESCRIPTION_DIALOG);
            }
        });
        descriptionCheckItemBtn.setFocusable(false);

        return itemView;
    }

    public void clear() {
        mItems.clear();
    }

    public ArrayList<CheckListItem> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<CheckListItem> items) {
        mItems = items;
    }

    public void copyItems(ArrayList<CheckListItem> items) {
        mItems.clear();
        int size = items.size();
        for(int index = 0; index<size; index++)
        {
            CheckListItem item = items.get(index);
            int memoId = item.getMemoId();
            int checkId = item.getCheckId();
            String checkboxText = item.getCheckBoxText();
            String description = item.getDescription();
            int isChecked = item.getIsChecked();
            mItems.add(new CheckListItem(memoId, checkId, checkboxText, description, isChecked));
        }
    }

    public void setDescription(int position, String description)
    {
        CheckListItem item = mItems.get(position);
        item.setDescription(description);
    }
}
