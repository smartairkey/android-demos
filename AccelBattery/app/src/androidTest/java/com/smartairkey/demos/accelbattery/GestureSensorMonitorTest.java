package com.smartairkey.demos.accelbattery;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.smartairkey.demos.accelbattery.gestures.GestureSensorMonitor;
import com.smartairkey.demos.accelbattery.gestures.strategies.FreezeStrategy;
import com.smartairkey.demos.accelbattery.gestures.strategies.GestureStrategy;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import static org.junit.Assert.*;
/**
 * Created by dmitry on 20.01.16.
 */
@RunWith(AndroidJUnit4.class)
//@Config(constants = BuildConfig.class, sdk = 21)
public class GestureSensorMonitorTest {
    boolean result = false;

    @Test
    public void register_freezingStrategy_isRegistered() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Context context = InstrumentationRegistry.getContext();
        GestureSensorMonitor gestureSensorMonitor = new GestureSensorMonitor(context);

        FreezeStrategy freezeStrategy = new FreezeStrategy.Builder()
                .hasNotMoreFrom(5, 50)
                .after(300)
                .create();

        freezeStrategy.subscribe(new GestureStrategy.Handler() {
            @Override
            public void onOccurred() {
                result = true;
                latch.countDown();
            }
        });

        gestureSensorMonitor.register(freezeStrategy);
        Subscription monitoringSchedule = gestureSensorMonitor.schedule()
                .every(2000)
                .work(1500)
                .samplingPeriod(100)
                .start();

        latch.await(16000, TimeUnit.MILLISECONDS);
        assertEquals(true, result);
    }
}