
package com.leven.batterywidget;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

public class BatteryMonitorActivity extends Activity {

    private SharedPreferences mPreferences;
    private TextView tvBatteryStatus;
    private TextView tvBatteryPlug;
    private TextView tvBatteryLevel;
    private TextView tvBatteryHealth;
    private TextView tvBatteryTemperature;
    private TextView tvBatteryVoltage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        initView();
        
        //update battery info's broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.leven.batterywidget.UPDATE_BATTERY_INFO");
        registerReceiver(mBatteryReceiver, filter);
        
        updateBatteryInfo();
    }

    private void initView() {
        tvBatteryStatus=(TextView)findViewById(R.id.tvBatteryStatus);
        tvBatteryPlug = (TextView) findViewById(R.id.tvBatteryPlug);
        tvBatteryLevel=(TextView)findViewById(R.id.tvBatteryLevel);
        tvBatteryHealth=(TextView)findViewById(R.id.tvBatteryHealth);
        tvBatteryTemperature=(TextView)findViewById(R.id.tvBatteryTemperature);
        tvBatteryVoltage=(TextView)findViewById(R.id.tvBatteryVoltage);
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.leven.batterywidget.UPDATE_BATTERY_INFO")) {
                updateBatteryInfo();
            }
        }

    };
    
    private void updateBatteryInfo() {
        mPreferences = getSharedPreferences(Constants.BATTERY_INFO, MODE_PRIVATE);
        tvBatteryStatus.setText(getBatteryStatus());
        tvBatteryPlug.setText(getBatteryPlug());
        tvBatteryHealth.setText(getBatteryHealth());
        tvBatteryTemperature.setText(getBatteryTemperature());
        tvBatteryLevel.setText(getBatteryLevel());
        tvBatteryVoltage.setText(getBatteryVoltage());
    }
    
    private String getBatteryVoltage() {
        int batteryVoltage = mPreferences.getInt(Constants.BATTERY_VOLTAGE, 0);
        return batteryVoltage + getString(R.string.batteryVoltSymbol);
    }

    private String getBatteryLevel() {
        int batteryLevel = mPreferences.getInt(Constants.BATTERY_LEVEL, 0);
        return batteryLevel + getString(R.string.batteryLevelSymbol);
    }

    private String getBatteryTemperature() {
        int iTemperature = mPreferences.getInt(Constants.BATTERY_TEMPERATURE, 0);
        String tempUnit = mPreferences.getString(Constants.TEMEPERATURE_UNIT, Constants.DEFAULT_TEMEPERATURE);
        if(tempUnit.equalsIgnoreCase(getString(R.string.batteryFahrenheitSymbol))) {
            iTemperature = iTemperature * 9 / 5 + 320;
        }
        
        float fTemperature = iTemperature / (float) 10;
        return fTemperature + mPreferences.getString(Constants.TEMEPERATURE_UNIT, 
                Constants.DEFAULT_TEMEPERATURE);
    }

    private int getBatteryHealth() {
        int batteryHealth = mPreferences.getInt(Constants.BATTERY_HEALTH, 0);
        switch (batteryHealth) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return R.string.batteryHealthDead;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return R.string.batteryHealthGood;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return R.string.batteryHealthOverVoltage;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return R.string.batteryHealthOverheat;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return R.string.batteryHealthUnspecifiedFailure;
            default:
                return R.string.Unknown;
        }
    }

    private int getBatteryPlug() {
        int batteryPlug = mPreferences.getInt(Constants.BATTERY_PLUG, 0);
        switch (batteryPlug) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return R.string.batteryPluggedAC;
            case BatteryManager.BATTERY_PLUGGED_USB:
                return R.string.batteryPluggedUSB;
            default:
                return R.string.batteryPlugNone;
        }
    }

    private int getBatteryStatus() {
        int batteryStatus = mPreferences.getInt(Constants.BATTERY_STATUS, 0);
        switch (batteryStatus) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                return R.string.Unknown;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return R.string.batteryStatusCharging;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return R.string.batteryStatusDischarging;
            case BatteryManager.BATTERY_STATUS_FULL:
                return R.string.batteryStatusFull;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return R.string.batteryStatusNotCharging;
            default:
                return R.string.Unknown;
        }
    }

    /** 点击屏幕任意位置，关闭电池信息Activity */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatteryReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
