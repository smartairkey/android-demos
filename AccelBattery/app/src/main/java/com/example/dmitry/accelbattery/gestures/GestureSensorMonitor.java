package com.example.dmitry.accelbattery.gestures;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

import com.example.dmitry.accelbattery.gestures.strategies.GestureStrategy;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by dmitry on 17.01.16.
 */
public class GestureSensorMonitor implements SensorEventListener {
    //    private List<GestureStrategy> strategies = new ArrayList<>();
    private AtomicReference<ArrayList<GestureStrategy>> strategiesA = new AtomicReference<>(new ArrayList<GestureStrategy>());
    private SensorManager sensorManager;
    private Sensor sensor;
    private PowerManager.WakeLock lock;
    private int samplingPeriodsInMs = 100;


    public GestureSensorMonitor(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead");
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start() {
        sensorManager.registerListener(this, sensor, samplingPeriodsInMs * 1000);
    }

    public void stop() {
        sensorManager.unregisterListener(this, sensor);
    }

    public void register(GestureStrategy strategy) {
        ArrayList<GestureStrategy> gestureStrategies = new ArrayList<>(strategiesA.get());
        gestureStrategies.add(strategy);
        strategiesA.set(gestureStrategies);
    }

    public void unregister(GestureStrategy strategy) {
        ArrayList<GestureStrategy> gestureStrategies = new ArrayList<>(strategiesA.get());
        gestureStrategies.remove(strategy);
        strategiesA.set(gestureStrategies);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ArrayList<GestureStrategy> gestureStrategies = new ArrayList<>(strategiesA.get());
        for (GestureStrategy strategy : gestureStrategies) {
            strategy.processEvent(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public ITotalIntervalScheduler schedule() {
        return new Scheduler(this);
    }

    public interface ITotalIntervalScheduler {
        IWorkingIntervalScheduler every(int milliseconds);
    }

    public interface IWorkingIntervalScheduler {
        IScheduleRunner work(int milliseconds);
    }

    public interface IScheduleRunner {
        ISamplingPeriod setSamplingPeriod(final int milliseconds);
    }

    public interface ISamplingPeriod {
        Subscription start();
    }

    public final class Scheduler implements ITotalIntervalScheduler, IWorkingIntervalScheduler, ISamplingPeriod, IScheduleRunner {
        private final GestureSensorMonitor monitor;
        public int workingIntervalInMs;
        public int totalIntervalInMs;

        public Scheduler(GestureSensorMonitor monitor) {
            this.monitor = monitor;
        }

        public Scheduler every(int milliseconds) {
            totalIntervalInMs = milliseconds;
            return this;
        }

        public Scheduler work(int milliseconds) {
            if (totalIntervalInMs <= milliseconds) {
                throw new IllegalArgumentException("total interval must be greater then working");
            }

            workingIntervalInMs = milliseconds;
            return this;
        }

        public Scheduler setSamplingPeriod(int milliseconds) {
            samplingPeriodsInMs = milliseconds;
            return this;
        }

        public Subscription start() {
            rx.Observable<Long> workingPeriod = rx.Observable.interval(0, totalIntervalInMs, TimeUnit.MILLISECONDS);
            Subscription startingScanner = workingPeriod.subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    lock.acquire();

                    monitor.start();

                }
            });

            Subscription stoppingScanner = workingPeriod.delay(workingIntervalInMs, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            monitor.stop();
                            lock.release();
                        }
                    });

            return new CompositeSubscription(startingScanner, stoppingScanner);
        }

    }
}
