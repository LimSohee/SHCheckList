package com.sh.shchecklist.dialog;

import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DescriptionDialog extends Activity {
    public static final String TAG = "DesciptionDialog";

    TextView mDescriptionText;
    EditText mDescriptionEdit;

    private static int mCheckListMode;
    private static String mDescription;
    private static int mCheckId;
    private static int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_description);

        Button mOkBtn;
        Button mCancelBtn;
        Button mCopyBtn;

        mDescriptionText = (TextView) findViewById(R.id.textView_description);
        mDescriptionEdit = (EditText) findViewById(R.id.editText_description);

        // CheckActivity에서 보낸 데이터 가져오기
        Intent receivedItent = getIntent();
        mCheckListMode = receivedItent.getIntExtra(BasicInfo.KEY_CHECKLIST_MODE, -1);
        mDescription = receivedItent.getStringExtra(BasicInfo.KEY_DESCRIPTION);
        mCheckId = receivedItent.getIntExtra(BasicInfo.KEY_CHECK_ID, -1);
        mPosition = receivedItent.getIntExtra(BasicInfo.KEY_CHECKITEM_POSITION, -1);

        mOkBtn = (Button) findViewById(R.id.button_description_ok);
        mOkBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "확인 버튼 Click");

                if(mCheckListMode == BasicInfo.MODE_VIEW){
                    Intent closeItent = new Intent();
                    setResult(RESULT_CANCELED, closeItent);
                    finish();
                }
                else if(mCheckListMode == BasicInfo.MODE_MODIFY || mCheckListMode == BasicInfo.MODE_INSERT){
                    Intent closeItent = new Intent();
                    closeItent.putExtra(BasicInfo.KEY_DESCRIPTION, mDescriptionEdit.getText().toString());
                    closeItent.putExtra(BasicInfo.KEY_CHECK_ID, mCheckId);
                    closeItent.putExtra(BasicInfo.KEY_CHECKITEM_POSITION, mPosition);
                    setResult(RESULT_OK, closeItent);
                    finish();
                }
            }
        });

        mCancelBtn = (Button) findViewById(R.id.button_description_cancel);
        mCancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "취소 버튼 Click");

                Intent closeItent = new Intent();
                setResult(RESULT_CANCELED, closeItent);
                finish();
            }
        });

        mCopyBtn = (Button) findViewById(R.id.button_copy_clipboard);
        mCopyBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "클립보드에 복사 버튼 Click");

                if(Build.VERSION.SDK_INT >= 11){
                    android.content.ClipboardManager c = (android.content.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData data = android.content.ClipData.newPlainText("description", mDescriptionText.getText().toString());
                    c.setPrimaryClip(data);
                }
                else{
                    android.text.ClipboardManager c = (android.text.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    c.setText(mDescriptionText.getText().toString());
                }
                Toast.makeText(getApplicationContext(), R.string.completed_copy_to_clipboard, Toast.LENGTH_LONG).show();
            }
        });

        if(mCheckListMode == BasicInfo.MODE_VIEW){
            mCancelBtn.setVisibility(View.GONE);
            mCopyBtn.setVisibility(View.VISIBLE);

            mDescriptionText.setVisibility(View.VISIBLE);
            mDescriptionEdit.setVisibility(View.GONE);
            mDescriptionText.setText(mDescription);
        }
        else if(mCheckListMode == BasicInfo.MODE_MODIFY){
            mCancelBtn.setVisibility(View.VISIBLE);
            mCopyBtn.setVisibility(View.GONE);

            mDescriptionText.setVisibility(View.GONE);
            mDescriptionEdit.setVisibility(View.VISIBLE);
            mDescriptionEdit.setText(mDescription);
        }
        else if(mCheckListMode == BasicInfo.MODE_INSERT){
            mCancelBtn.setVisibility(View.VISIBLE);
            mCopyBtn.setVisibility(View.GONE);

            mDescriptionText.setVisibility(View.GONE);
            mDescriptionEdit.setVisibility(View.VISIBLE);
            mDescriptionEdit.setText(mDescription);
        }
    }
}
