package com.sh.shchecklist.dialog;

import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;


public class SortDialog extends Activity
{
    public static final String TAG = "SortDialog";

    private static Spinner standardSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sort);

        ArrayAdapter<CharSequence> standardAdapter = ArrayAdapter.createFromResource(this, R.array.sort_standard_array, android.R.layout.simple_spinner_item);
        standardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standardSpinner = (Spinner) findViewById(R.id.spinner_sort_standard);
        standardSpinner.setAdapter(standardAdapter);


        Button okBtn = (Button) findViewById(R.id.button_sort_ok);
        okBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "확인 버튼 Click");

                RadioButton ascRadioButton = (RadioButton) findViewById(R.id.radioButton_asc);
                int direction = 0;
                if(ascRadioButton.isChecked())
                    direction = 1;
                else
                    direction = 0;

                String standard = "";
                int standardId = (int) standardSpinner.getSelectedItemId();
                if(standardId == 0)
                    standard = "CheckId";
                else if(standardId == 1)
                    standard = "CheckBoxTEXT";
                else if(standardId == 2)
                    standard = "IsChecked";

                Intent closeItent = new Intent();
                closeItent.putExtra(BasicInfo.KEY_SORT_STANDARD, standard);
                closeItent.putExtra(BasicInfo.KEY_SORT_DIRECTION, direction);
                setResult(RESULT_OK, closeItent);
                finish();
            }
        });

        Button cancelBtn = (Button) findViewById(R.id.button_sort_cancel);
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "취소 버튼 Click");

                Intent closeItent = new Intent();
                setResult(RESULT_CANCELED, closeItent);
                finish();
            }
        });
    }
}
