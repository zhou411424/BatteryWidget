package com.leven.batterywidget;


import com.leven.batterywidget.db.SQLiteDataBaseUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

public class BatteryStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "BatteryStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            Log.d(TAG, "battery changed");
            SharedPreferences prefs = 
                    context.getSharedPreferences(Constants.BATTERY_INFO, Context.MODE_PRIVATE);
            if(intent.getIntExtra(Constants.BATTERY_LEVEL, 0) != prefs.getInt(Constants.BATTERY_LEVEL, 0)) {
                Log.d(TAG, "insert battery level");
                SQLiteDataBaseUtils.Entry entry = 
                        new SQLiteDataBaseUtils.Entry(intent.getIntExtra(Constants.BATTERY_LEVEL, 0));
                SQLiteDataBaseUtils dbUtils = new SQLiteDataBaseUtils(context);
                dbUtils.openWrite();
                dbUtils.insertEntry(entry);
                dbUtils.close();
            }
            
            Editor editor = prefs.edit();
            editor.putInt(Constants.BATTERY_STATUS, intent.getIntExtra(Constants.BATTERY_STATUS, 0));
            editor.putInt(Constants.BATTERY_HEALTH, intent.getIntExtra(Constants.BATTERY_HEALTH, 0));
            editor.putInt(Constants.BATTERY_PLUG, intent.getIntExtra(Constants.BATTERY_PLUG, 0));
            editor.putInt(Constants.BATTERY_LEVEL, intent.getIntExtra(Constants.BATTERY_LEVEL, 0));
            editor.putInt(Constants.BATTERY_TEMPERATURE, intent.getIntExtra(Constants.BATTERY_TEMPERATURE, 0));
            editor.putInt(Constants.BATTERY_VOLTAGE, intent.getIntExtra(Constants.BATTERY_VOLTAGE, 0));
            editor.commit();
            
            Intent service = new Intent(context, BatteryUpdateService.class);
            context.startService(service);
        } 
        
        if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            Log.d(TAG, "battery low");
            SharedPreferences prefs = 
                    context.getSharedPreferences(Constants.BATTERY_INFO, Context.MODE_PRIVATE);
            if(prefs.getBoolean(Constants.VIBRATION_SETTINGS, false)) {
                final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        vibrator.vibrate(1000);
                    }
                    
                }.start();
            }
            
            if(prefs.getBoolean(Constants.SOUND_SETTINGS, false)) {
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.low_battery);
                new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        mp.start();
                    }
                    
                }.start();
            }
        }
    }

}
