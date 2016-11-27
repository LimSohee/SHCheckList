package com.sh.shchecklist.common;

public class BasicInfo {

    // table name
    public static String TABLE_MEMO = "MEMO";
    public static String TABLE_CHECK = "CHECKLIST";

    // 데이터베이스 이름
    public static String DATABASE_NAME = "shmemo.db";

    // 외장 메모리 패스 체크 여부
    public static boolean ExternalChecked = false;

    //========== 인텐트 부가정보 전달을 위한 키값 ==========//
    public static final String KEY_CHECKLIST_MODE		 = "CHECKLIST_MODE";
    public static final String KEY_MEMO_ID				 = "MEMO_ID";
    public static final String KEY_MEMO_TITLE 			 = "MEMO_TITLE";
    public static final String KEY_CHECK_COUNT			 = "CHECK_COUNT";
    public static final String KEY_TOTAL_COUNT			 = "TOTAL_COUNT";
    public static final String KEY_DESCRIPTION			 = "DESCRIPTION";
    public static final String KEY_SORT_STANDARD		 = "SORT_STANDARD";
    public static final String KEY_SORT_DIRECTION 		 = "SORT_DIRECTION";
    public static final String KEY_CHECKITEM_POSITION 	 = "CHECKITEM_POSITION";
    public static final String KEY_FILEPATH				 = "FILEPATH";
    public static final String KEY_FILENAME 			 = "FILENAME";
    public static final String KEY_CHECK_ID 			 = "CHECK_ID";

    //========== 메모 모드 상수 ==========//
    public static final int MODE_INSERT   = 1;
    public static final int MODE_VIEW     = 2;
    public static final int MODE_MODIFY   = 3;
    public static final int MODE_DELETE   = 4;
    public static final int MODE_EXPORT   = 5;
    public static final int MODE_IMPORT   = 6;
    public static final int MODE_PRIORITY = 7;


    //========== 액티비티 요청 코드  ==========//
    public static final int REQ_VIEW_ACTIVITY 		 = 1001;
    public static final int REQ_INSERT_ACTIVITY		 = 1002;
    public static final int REQ_SORT_DIALOG 		 = 1003;
    public static final int REQ_DESCRIPTION_DIALOG   = 1004;
    public static final int REQ_EXPLORER_ACTIVITY    = 1005;


    //========== CheckListAdapter의 Type  ==========//
    public static final int ADAPTERTYPE_CHECKITEM    = 1;
    public static final int ADAPTERTYPE_SEARCHITEM   = 2;
    public static final int ADAPTERTYPE_MODIFYITEM   = 3;

    //========== BroadCastReceiver Filter  ==========//
    public static final String ACTION_CALL_APPLICATION 			 = "android.appwidget.action.ACTION_CALL_APPLICATION";
    public static final String ACTION_CREATE_MEMO_DIALOG 		 = "android.appwidget.action.ACTION_CREATE_MEMO_DIALOG";
    public static final String ACTION_CREATE_DESCRIPTION_DIALOG  = "android.appwidget.action.ACTION_CREATE_DESCRIPTION_DIALOG";
    public static final String ACTION_WIDGET_CHANGE_LIST		 = "android.appwidget.action.ACTION_WIDGET_CHANGE_LIST";
    public static final String ACTION_WIDGET_UPDATE_LIST		 = "android.appwidget.action.ACTION_WIDGET_UPDATE_LIST";

    //========== Widget Change Type  ==========//
    public static final int WIDGET_UPDATE_DONE		 		= 0;
    public static final int WIDGET_UPDATE_ONLY_TITLE 		= 1;
    public static final int WIDGET_UPDATE_ONLY_LIST 		= 2;
    public static final int WIDGET_UPDATE_TITLE_AND_LIST 	= 3;

    public static final String FILENAME = "widgetMemoDailog_Result";
}
