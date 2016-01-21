package com.smartairkey.demos.accelbattery;

import android.content.Context;
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

import static org.junit.Assert.assertEquals;

/**
 * Created by dmitry on 21.01.16.
 */

    @RunWith(AndroidJUnit4.class)
//    @Config(constants = BuildConfig.class, sdk = 21)
    public class HelloWorldTest {
        boolean result = false;

        @Test
        public void helloWorld() throws Exception {
            String hello = "Hello";
            String world = "World";
            assertEquals(hello+world, "HelloWorld");
        }
    }

