package com.ekunt.kamiweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ekunt.kamiweather.service.AutoUpdateService;

/**
 * 启动更新天气的服务
 * Created by E-Kunt on 2016/2/21.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startActivity(i);
    }
}
