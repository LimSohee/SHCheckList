package com.sh.shchecklist.check;

import com.sh.shchecklist.R;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class CheckListItemView extends LinearLayout {

    CheckBox mCheckItem;

    public CheckListItemView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem_check, this, true);

        mCheckItem = (CheckBox) findViewById(R.id.checkBox_checkitem);
    }

    public void setCheckBoxText(String checkBox_Text){
        mCheckItem.setText(checkBox_Text);
    }

    public void setIsChecked(int isChecked){
        if(isChecked == 0) {
            mCheckItem.setChecked(false);
            mCheckItem.setPaintFlags(mCheckItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mCheckItem.setPaintFlags(mCheckItem.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            mCheckItem.setChecked(true);
            mCheckItem.setPaintFlags(mCheckItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
