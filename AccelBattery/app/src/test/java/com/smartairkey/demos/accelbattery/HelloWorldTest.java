package com.smartairkey.demos.accelbattery;

import com.smartairkey.demos.accelbattery.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by dmitry on 21.01.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
    public class HelloWorldTest {
        boolean result = false;

        @Test
        public void helloWorld() throws Exception {
            String hello = "Hello";
            String world = "World";
            assertEquals(hello+world, "HelloWorld");
        }
    }
