package com.example.dmitry.accelbattery.gestures.strategies;

import android.hardware.SensorEvent;

import com.example.dmitry.accelbattery.utils.CircularBuffer;

/**
 * Created by dmitry on 17.01.16.
 */
public abstract class FreezingGestureStrategy extends GestureStrategy {
    CircularBuffer<Double> buffer;
    protected long lastEventTimestamp;
    protected int delayInMs;
    protected double threshold;
    protected int movesCounter;


    @Override
    protected boolean processEventCore(SensorEvent event) {

        if (event.timestamp - lastEventTimestamp <= delayInMs * 1000 * 1000L) {
            return false;
        }

        movesCounter = 0;
        lastEventTimestamp = event.timestamp;
        double currentValue = getDiffValue(event);
        buffer.add(currentValue);

        if (!buffer.isFull()) {
            return false;
        }

        countNumberOfMoves();

        if (criteriaSucceeded()) {
            clear();
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (buffer != null) {
            buffer.clear();
        }
    }

    protected abstract boolean criteriaSucceeded();

    private int countNumberOfMoves() {

        for (int i = 1; i < buffer.getSize(); i++) {
            if ((Math.abs(buffer.get(i) - buffer.get(i - 1)) >= threshold)) {
                movesCounter++;
            }
        }
        return movesCounter;
    }

    private double getDiffValue(SensorEvent event) {
        float absX = Math.abs(event.values[0]);
        float absY = Math.abs(event.values[1]);
        float absZ = Math.abs(event.values[2]);

        return Math.sqrt(absX + absY + absZ);
    }
}
