package com.sh.shchecklist.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    public static final String TAG = "WidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(TAG, "======================= onGetViewFactory() =======================");

        return (new ListProvider(this.getApplicationContext(), intent));
    }
}
