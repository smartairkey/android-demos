package com.example.dmitry.accelbattery;

import android.app.Application;
import android.content.Intent;

/**
 * Created by dmitry on 18.01.16.
 */
public class AccelBatteryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.startService(new Intent(this, BatterySavingService.class));
    }
}
