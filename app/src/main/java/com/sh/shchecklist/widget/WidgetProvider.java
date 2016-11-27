package com.sh.shchecklist.widget;

import com.sh.shchecklist.main.MainActivity;
import com.sh.shchecklist.R;
import com.sh.shchecklist.check.CheckActivity;
import com.sh.shchecklist.common.BasicInfo;
import com.sh.shchecklist.dialog.DescriptionDialog;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    public static final String TAG = "WidgetProvider";

    public static Context mContext;

    private int mMemoId = -1;
    private String mMemoTitle = "SHCheckList";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i(TAG, "======================= onEnabled() =======================");
        mContext = context;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "======================= onUpdate() =======================");

        for (int appWidgetId : appWidgetIds) {

            // Widget의 View를 구성
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            Intent activityIntent = new Intent(BasicInfo.ACTION_CALL_APPLICATION);
            Intent dialogIntent = new Intent(BasicInfo.ACTION_CREATE_MEMO_DIALOG);

            PendingIntent activityPIntent = PendingIntent.getBroadcast(context, 0, activityIntent, 0);
            PendingIntent dialogPIntent = PendingIntent.getBroadcast(context, 0, dialogIntent, 0);

            remoteViews.setOnClickPendingIntent(R.id.btn_open_memo_activity, activityPIntent);
            remoteViews.setOnClickPendingIntent(R.id.btn_open_memo_dialog, dialogPIntent);

            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            remoteViews.setRemoteAdapter(appWidgetId, R.id.listView_widget_checklist, svcIntent);
            remoteViews.setEmptyView(R.id.listView_widget_checklist, R.id.textView_widget_checklist_empty);
            remoteViews.setTextViewText(R.id.textView_wiget_checklist_title, mMemoTitle);

            // Widget의 List 업데이트
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView_widget_checklist);
            // widget 업데이트
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "======================= onDeleted() =======================");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "======================= onDisabled() =======================");
        super.onDisabled(context);
    }

    // Receiver 수신
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG, "======================= onReceive() =======================");

        String action = intent.getAction();
        Log.d(TAG, "action = " + action);

        // Default Recevier
        if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {

        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

        } else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {

        } else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {

        }

        // Custom Recevier
        else if (BasicInfo.ACTION_CALL_APPLICATION.equals(action)) {
            callApplication(context);
        } else if (BasicInfo.ACTION_CREATE_MEMO_DIALOG.equals(action)) {
            createMemoDialog(context);
        } else if (BasicInfo.ACTION_CREATE_DESCRIPTION_DIALOG.equals(action)) {
            createDescriptionDialog(context);
        }
        else if(BasicInfo.ACTION_WIDGET_CHANGE_LIST.equals(action)) {
            changeWidgetList(context);
        }
        else if(BasicInfo.ACTION_WIDGET_UPDATE_LIST.equals(action)) {
            updateWidgetList(context);
        }
    }

    // Call Activity버튼 클릭 시, SH CheckList APP 실행
    private void callApplication(Context context) {
        Log.i(TAG, "======================= callApplication() =======================");

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Create Dialog버튼 클릭시, WidgetMemoDialog 생성
    private void createMemoDialog(Context context) {
        Log.i(TAG, "======================= createMemoDialog() =======================");

        Intent intent = new Intent(context, WidgetMemoDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Description버튼 클릭 시, DescriptionDialog 실행
    private void createDescriptionDialog(Context context) {
        Log.i(TAG, "======================= createDescriptionDialog() =======================");

        Intent intent = new Intent(context, DescriptionDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // WidgetMemoDialog에서 ListItem클릭 했을 때, Widget의 List를 선택한 Memo의 List로 업데이트해주는 함수
    private void changeWidgetList(Context context) {
        Log.i(TAG, "======================= changeWidgetList() =======================");

        CheckActivity.widgetChangeType = BasicInfo.WIDGET_UPDATE_DONE;

        // memoId 가져오기
        SharedPreferences pref = context.getSharedPreferences(BasicInfo.FILENAME, Activity.MODE_PRIVATE);
        mMemoId = pref.getInt(BasicInfo.KEY_MEMO_ID, 0);
        mMemoTitle = pref.getString(BasicInfo.KEY_MEMO_TITLE, "");
        Log.d(TAG, "mMemoId = " + mMemoId + ", mMemoTitle = " + mMemoTitle);

        // 위젯 업데이트
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        this.onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())));
    }

    // Application에서 Data를 수정했을 때, Widget의 List를 업데이트해주는 함수
    private void updateWidgetList(Context context) {
        Log.i(TAG, "======================= updateWidgetOnlyList() =======================");

        CheckActivity.widgetChangeType = BasicInfo.WIDGET_UPDATE_DONE;

        // memoId와 memoTitle 가져오기
        SharedPreferences pref = context.getSharedPreferences(BasicInfo.FILENAME, Activity.MODE_PRIVATE);
        mMemoId = pref.getInt(BasicInfo.KEY_MEMO_ID, 0);
        mMemoTitle = pref.getString(BasicInfo.KEY_MEMO_TITLE, "");
        Log.d(TAG, "mMemoId = " + mMemoId + ", mMemoTitle = " + mMemoTitle);

        // 위젯의 List 업데이트
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())), R.id.listView_widget_checklist);
    }
}