package com.example.dmitry.accelbattery;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.dmitry.accelbattery.battery_drain.WifiScanner;
import com.example.dmitry.accelbattery.utils.Events;
import com.example.dmitry.accelbattery.gestures.strategies.FreezeStrategy;
import com.example.dmitry.accelbattery.gestures.GestureSensorMonitor;
import com.example.dmitry.accelbattery.gestures.strategies.GestureStrategy;
import com.example.dmitry.accelbattery.gestures.strategies.UnfreezeStrategy;

import de.greenrobot.event.EventBus;
import rx.Subscription;

import static com.example.dmitry.accelbattery.utils.Events.*;

/**
 * Created by dmitry on 17.01.16.
 */
public class BatterySavingService extends IntentService {

    public BatterySavingService(String name) {
        super(name);
    }

    public BatterySavingService() {
        super(Name);
    }

    public static final String Name = "AccelBattery.BatterySavingService";

    private GestureSensorMonitor gestureSensorMonitor;
    private UnfreezeStrategy unfreezeStrategy;
    private FreezeStrategy freezeStrategy;
    private Subscription monitoringSchedule;
    private WifiScanner wifiScanner;

    public void onEvent(Events.PhoneSleptEvent event) {
        showNotification(true);
        restartGestureMonitoringSleepingMode(unfreezeStrategy);
        wifiScanner.stop();
    }
    public void onEvent(Events.PhoneActivatedEvent event) {
        showNotification(false);
        restartGestureMonitoringActiveMode(freezeStrategy);
        wifiScanner.start();
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        makeForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().registerSticky(this);
        makeForeground();

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        initStrategies();
        gestureSensorMonitor = new GestureSensorMonitor(this);
        wifiScanner = new WifiScanner(this);
        restartGestureMonitoringActiveMode(freezeStrategy);
        wifiScanner.start();
    }

    private void makeForeground() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Active");

        startForeground(1, builder.build());
        EventBus.getDefault().postSticky(new Events.PhoneActivatedEvent());
    }

    private void showNotification(boolean isSleeping) {
        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(isSleeping
                                ? R.drawable.sleep
                                : R.drawable.ic_launcher)
                        .setContentTitle("Sleeping")
                        .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void restartGestureMonitoringSleepingMode(final GestureStrategy... strategies) {
        stopGestureMonitoring();
        for (GestureStrategy strategy : strategies) {
            gestureSensorMonitor.register(strategy);
        }
        Subscription monitoringSchedule = gestureSensorMonitor.schedule()
                .every(60000)
                .work(1500)
                .setSamplingPeriod(100)
                .start();
        this.monitoringSchedule = monitoringSchedule;
    }

    private void restartGestureMonitoringActiveMode(final GestureStrategy... strategies) {
        stopGestureMonitoring();
        for (GestureStrategy strategy : strategies) {
            gestureSensorMonitor.register(strategy);
        }
        Subscription monitoringSchedule = gestureSensorMonitor.schedule()
                .every(10000)
                .work(9500)
                .setSamplingPeriod(100)
                .start();
        this.monitoringSchedule = monitoringSchedule;
    }


    private void initStrategies() {
        freezeStrategy = new FreezeStrategy.Builder()
                .hasNotMoreFrom(50, 500)
                .after(3000)
                .create();
        freezeStrategy.subscribe(new GestureStrategy.Handler() {
            @Override
            public void onOccurred() {
                setSlept();
            }
        });

        unfreezeStrategy = new UnfreezeStrategy.Builder()
                .hasAtLeast(3, 10)
                .after(100)
                .create();
        unfreezeStrategy.subscribe(new GestureStrategy.Handler() {
            @Override
            public void onOccurred() {
                setActive();
            }
        });
    }

    private void setSlept() {
        EventBus.getDefault().postSticky(new Events.PhoneSleptEvent());
    }

    private void setActive() {
        EventBus.getDefault().postSticky(new Events.PhoneActivatedEvent());
    }

    private void stopGestureMonitoring() {
        if (monitoringSchedule != null) {
            monitoringSchedule.unsubscribe();
            monitoringSchedule = null;
        }

        if (gestureSensorMonitor != null) {
            gestureSensorMonitor.stop();
            if (unfreezeStrategy != null) {
                unfreezeStrategy.clear();
                gestureSensorMonitor.unregister(unfreezeStrategy);
            }
            if (freezeStrategy != null) {
                freezeStrategy.clear();
                gestureSensorMonitor.unregister(freezeStrategy);
            }
        }
    }

}
