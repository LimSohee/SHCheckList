package com.sh.shchecklist.main;

import com.sh.shchecklist.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MemoListItemView extends LinearLayout {

    TextView mTitle;
    TextView mCount;
    ImageButton mPriority;
    ProgressBar mProgressbar;

    public MemoListItemView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem_memo, this, true);

        mTitle = (TextView) findViewById(R.id.textView_memoitem_title);
        mCount = (TextView) findViewById(R.id.textView_memoitem_count);
        mPriority = (ImageButton) findViewById(R.id.imageButton_priority);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar_memoitem);
    }

    public void setTitle(String title){
        mTitle.setText(title);
    }

    public void setCount(String count){
        mCount.setText(count);
    }

    public void setPriority(int priority){
        if(priority == 1)
            mPriority.setBackgroundResource(android.R.drawable.btn_star_big_on);
        else
            mPriority.setBackgroundResource(android.R.drawable.btn_star_big_off);
    }

    public void setProgressbar(int totalCount, int checkCount){
        mProgressbar.setMax(totalCount);
        mProgressbar.setProgress(checkCount);
    }

}
