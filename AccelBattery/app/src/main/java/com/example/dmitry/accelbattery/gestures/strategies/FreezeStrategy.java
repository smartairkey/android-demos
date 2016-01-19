package com.example.dmitry.accelbattery.gestures.strategies;

import com.example.dmitry.accelbattery.utils.CircularBuffer;

/**
 * Created by dmitry on 17.01.16.
 */
public class FreezeStrategy extends FreezingGestureStrategy {
    private int movements;

    protected FreezeStrategy() {
        threshold = 0.4;
    }

    @Override
    protected boolean criteriaSucceeded() {
        return movesCounter <= movements;
    }

    public final static class Builder {
        private int afterInMs;
        private int movements;
        private int outOf;

        public FreezeStrategy.Builder after(int milliseconds) {
            afterInMs = milliseconds;
            return this;
        }

        public FreezeStrategy.Builder hasNotMoreFrom(int movements, int outOf) {
            this.movements = movements;
            this.outOf = outOf;
            return this;
        }

        public FreezeStrategy create() {
            FreezeStrategy strategy = new FreezeStrategy();
            strategy.delayInMs = afterInMs / outOf;
            strategy.buffer = new CircularBuffer<>(outOf);
            strategy.movements = movements;
            return strategy;
        }
    }
}
