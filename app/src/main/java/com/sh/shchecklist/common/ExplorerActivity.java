package com.sh.shchecklist.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sh.shchecklist.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ExplorerActivity extends ListActivity {

    private List<String> item = null;
    private List<String> path = null;

    private static String root="/storage/emulated/0/";
    private static String mDirPath="/storage/emulated/0/";

    private static TextView currentPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        currentPath = (TextView)findViewById(R.id.textView_currentpath);
        currentPath.setSelected(true);

        getDir(root);
    }

    // 주어진 argument(파일 경로)에 대한 탐색기 뷰 생성
    private void getDir(String dirPath)
    {
        mDirPath = dirPath;
        currentPath.setText("Location: " + dirPath);

        item = new ArrayList<String>();
        path = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if(!dirPath.equals(root)){
            item.add("../");
            path.add(f.getParent());
        }

        for(int i=0; i < files.length; i++){
            File file = files[i];
            path.add(file.getPath());

            if(file.isDirectory())
                item.add("[" + file.getName() + "]");
            else
                item.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row, item);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // 현재 뷰가 root 디렉토리가 아닌 경우,
        // path.get(0) = root 경로
        // path.get(1) = 상위 디렉토리 경로
        // path.get(2) = getDir에서 저장된 하위 디렉토리/파일 경로
        File file = new File(path.get(position));

        // 만약 디렉토리를 클릭한 거라면
        if (file.isDirectory())
        {
            if(file.canRead())
                getDir(path.get(position));
            else{
                Toast.makeText(getApplicationContext(), "[" + file.getName() + "] " + R.string.folder_read_not, Toast.LENGTH_LONG).show();
            }
        }
        // 만약 디렉토리가 아닌 파일을 클릭한 거라면
        else{
            if(file.getName().endsWith(".txt"))
            {
                Intent closeItent = new Intent();
                closeItent.putExtra(BasicInfo.KEY_FILEPATH, mDirPath + "/" + file.getName());
                closeItent.putExtra(BasicInfo.KEY_FILENAME, file.getName());
                setResult(RESULT_OK, closeItent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.extension_not, Toast.LENGTH_LONG).show();
            }

        }
    }
}