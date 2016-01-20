package com.smartairkey.demos.accelbattery;

import com.smartairkey.demos.accelbattery.gestures.GestureSensorMonitor;
import com.smartairkey.demos.accelbattery.gestures.strategies.FreezeStrategy;
import com.smartairkey.demos.accelbattery.gestures.strategies.GestureStrategy;
import com.smartairkey.demos.accelbattery.gestures.strategies.UnfreezeStrategy;

import org.junit.Test;

import rx.Subscription;

import static org.junit.Assert.*;

/**
 * Created by dmitry on 19.01.16.
 */
public class BatterySavingServiceTest {

    FreezeStrategy freezeStrategy;
    UnfreezeStrategy unfreezeStrategy;
    GestureSensorMonitor gestureSensorMonitor;
    Subscription monitoringSchedule;

    @Test
    public void testOnStartCommand() throws Exception {
        freezeStrategy = new FreezeStrategy.Builder()
                .hasNotMoreFrom(50, 500)
                .after(3000)
                .create();
        freezeStrategy.subscribe(new GestureStrategy.Handler() {
            @Override
            public void onOccurred() {
                assert true;
            }
        });

        gestureSensorMonitor.register(freezeStrategy);

        Subscription monitoringSchedule = gestureSensorMonitor.schedule()
                .every(6000)
                .work(1500)
                .samplingPeriod(100)
                .start();
        this.monitoringSchedule = monitoringSchedule;
    }
}