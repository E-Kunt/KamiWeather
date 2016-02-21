package com.ekunt.kamiweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.ekunt.kamiweather.receiver.AutoUpdateReceiver;
import com.ekunt.kamiweather.util.HttpCallbackListener;
import com.ekunt.kamiweather.util.HttpUtil;
import com.ekunt.kamiweather.util.Utility;

/**
 * 服务：更新天气并设置定时更新天气任务
 * Created by E-Kunt on 2016/2/21.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        //定时任务。设置8小时后启动AutoUpdateReceiver。（AutoUpdateReceiver又会启动此服务更新天气并继续设置定时任务）
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int timeSpan = 8 * 60 * 60 * 1000; //8小时间隔
        long triggerAtTime = SystemClock.elapsedRealtime() + timeSpan;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
