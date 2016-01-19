package com.example.dmitry.accelbattery.battery_drain;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

/**
 * Created by dmitry on 18.01.16.
 */


public final class WifiScanner {
    private final WifiManager wifi;
    private final Context context;
    private long lastScan = 0;
    private Thread workingThread;
    private BroadcastReceiver broadcastReceiver;

    public WifiScanner(Context context) {
        this.context = context;
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean isStarted() {
        return broadcastReceiver != null;
    }

    public void start() {
        if (isStarted()) {
            return;
        }
        registerBroadcastReceiver();
        startScan();
    }

    public void stop() {
        if (!isStarted()) {
            return;
        }
        if (workingThread != null) {
            workingThread.interrupt();
            workingThread = null;
        }

        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void registerBroadcastReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(receiver, intentFilter);
        broadcastReceiver = receiver;
    }

    private void startScan() {
        wifi.startScan();
        lastScan = System.currentTimeMillis();
    }

    private class BroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isStarted()) {
                return;
            }

            switch (intent.getAction()) {
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    startScan();
                    break;
            }
        }
    }
}
