package com.sh.shchecklist.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileManager {

    public static final String TAG = "FileManager";

    // 파일 쓰기
    public static void WriteFile(Context context, String memoTitle, String fileContent) throws IOException {
        String path;
        String externalState = Environment.getExternalStorageState();
        if (externalState.equals(Environment.MEDIA_MOUNTED))
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            path = Environment.MEDIA_UNMOUNTED;

        // 디렉토리 생성
        File dirFile = new File(path + "/SHNote");
        dirFile.mkdir();

        // 파일 쓰기
        File file = new File(path + "/SHNote/" + memoTitle + ".txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileContent.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("내보내기 실패:", e.getMessage());
        }
    }

    // 파일 읽기
    public static String ReadFile(Context context, String filepath) throws IOException {
        String readStr = "";

        try {
            // 파일 읽기
            File file = new File(filepath);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            readStr = new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readStr;
    }
}
