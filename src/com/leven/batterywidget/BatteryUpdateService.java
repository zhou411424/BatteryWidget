package com.leven.batterywidget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class BatteryUpdateService extends Service {

    private static final String TAG = "BatteryUpdateService";
    protected static final int UPDATE_WIDGET = 1;
    private BatteryStatusReceiver mBatteryReceiver;
    private int mBatteryLevel;
    private int mBatteryStatus;
    private String mTextColor;
    private SharedPreferences mPrefs;
    private RemoteViews mWidgetView;
    private Timer mTimer;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_WIDGET:
                    updateBatteryWidgetView();
                    break;
            }
        }
        
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind...");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate...");
        
        mBatteryReceiver = new BatteryStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        registerReceiver(mBatteryReceiver, filter);
        
        mWidgetView = new RemoteViews(getPackageName(), R.layout.widget_view);
        // first, update widget once
        updateBatteryWidgetView();
        
        // click widget,enter activity
        Intent intent = new Intent(this, BatteryMonitorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mWidgetView.setOnClickPendingIntent(R.id.widget_view, pendingIntent);
        
        // set a timer for updating widget every 30 seconds
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            
            @Override
            public void run() {
               mHandler.sendEmptyMessage(UPDATE_WIDGET);
            }
        }, 1, 30 * 1000);
    }

    private void updateBatteryWidgetView() {
        mPrefs = getSharedPreferences(Constants.BATTERY_INFO, Context.MODE_PRIVATE);
        mBatteryLevel = mPrefs.getInt(Constants.BATTERY_LEVEL, 0);
        mBatteryStatus = mPrefs.getInt(Constants.BATTERY_STATUS, 0);
        mTextColor = mPrefs.getString(Constants.TEXT_COLOR_SETTINGS, Constants.DEFAULT_COLOR);
        boolean isCharge = (mBatteryStatus == BatteryManager.BATTERY_STATUS_CHARGING);
        Log.d(TAG, "mBatteryLevel="+mBatteryLevel+", mBatteryStatus="+mBatteryStatus+", mTextColor"+mTextColor);
        Log.d(TAG, "isCharge="+isCharge);
        
        mWidgetView.setImageViewResource(R.id.battery_view, R.drawable.battery);
        mWidgetView.setImageViewResource(R.id.percent_view, getPercentViewId(mBatteryLevel));
        mWidgetView.setViewVisibility(R.id.batterytext, View.VISIBLE);
        mWidgetView.setTextColor(R.id.batterytext, Color.parseColor(mTextColor));
        mWidgetView.setTextViewText(R.id.batterytext, String.valueOf(mBatteryLevel)+"%");
        mWidgetView.setViewVisibility(R.id.charge_view, isCharge ? View.VISIBLE : View.INVISIBLE);
        
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, BatteryWidget.class);
        manager.updateAppWidget(componentName, mWidgetView);
    }

    private int getPercentViewId(int batteryLevel) {
        if(batteryLevel <=100 && batteryLevel > 90) {
            return R.drawable.lic_100;
        } else if(batteryLevel <= 90 && batteryLevel > 80) {
            return R.drawable.lic_90;
        } else if(batteryLevel <=80 && batteryLevel > 70) {
            return R.drawable.lic_80;
        } else if(batteryLevel <=70 && batteryLevel > 60) {
            return R.drawable.lic_70;
        } else if(batteryLevel <=60 && batteryLevel > 50) {
            return R.drawable.lic_60;
        } else if(batteryLevel <=50 && batteryLevel > 40) {
            return R.drawable.lic_50;
        } else if(batteryLevel <= 40 && batteryLevel > 30) {
            return R.drawable.lic_40;
        } else if(batteryLevel <= 30 && batteryLevel > 20) {
            return R.drawable.lic_30;
        } else if(batteryLevel <= 20 && batteryLevel > 10) {
            return R.drawable.lic_20;
        } else if(batteryLevel <= 10 && batteryLevel > 0) {
            return R.drawable.lic_10;
        } else {
            return R.drawable.battery;
        }
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand...");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy...");
        unregisterReceiver(mBatteryReceiver);
        
        // if the service is killed by android os,then restart the service
//        Intent service = new Intent(this, BatteryUpdateService.class);
//        startService(service);
        super.onDestroy();
    }

}
