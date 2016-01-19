package com.example.dmitry.accelbattery.gestures.strategies;

import com.example.dmitry.accelbattery.utils.CircularBuffer;

/**
 * Created by dmitry on 17.01.16.
 */
public class UnfreezeStrategy extends FreezingGestureStrategy {
    private int movements;

    protected UnfreezeStrategy() {
        threshold = 0.5;
    }

    @Override
    protected boolean criteriaSucceeded() {
        return movesCounter >= movements;
    }

    public final static class Builder {
        private int afterInMs;
        private int movements;
        private int outOf;

        public UnfreezeStrategy.Builder after(int milliseconds) {
            afterInMs = milliseconds;
            return this;
        }

        public UnfreezeStrategy.Builder hasAtLeast(int movements, int outOf) {
            this.movements = movements;
            this.outOf = outOf;
            return this;
        }

        public UnfreezeStrategy create() {
            UnfreezeStrategy strategy = new UnfreezeStrategy();
            strategy.delayInMs = afterInMs / outOf;
            strategy.buffer = new CircularBuffer<>(outOf);
            strategy.movements = movements;
            return strategy;
        }
    }
}
