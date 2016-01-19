package com.example.dmitry.accelbattery.gestures.strategies;

import android.hardware.SensorEvent;

import com.example.dmitry.accelbattery.utils.CircularBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitry on 17.01.16.
 */
public class GestureStrategy {
    private GestureHandlerSubscriber subscriber = new GestureHandlerSubscriber();

    public CircularBuffer<ProcessEventResult> buffer = new CircularBuffer<>(3);
    public ProcessEventResult result;
    public ProcessEventResult last;


    public void subscribe(Handler handler) {
        subscriber.subscribe(handler);
    }

    public void processEvent(SensorEvent event) {
        if (processEventCore(event)) {
            subscriber.onOccurred();
        }
    }

    protected boolean processEventCore(SensorEvent event) {
        return false;
    }

    public interface Handler {
        void onOccurred();
    }

    private class GestureHandlerSubscriber implements Handler {
        private List<Handler> handlers = new ArrayList<>();

        @Override
        public void onOccurred() {
            for (Handler handler : handlers) {
                handler.onOccurred();
            }
        }

        public void subscribe(Handler handler) {
            handlers.add(handler);
        }
    }

    public void clear() {
        buffer.clear();
    }


    private class ProcessEventResult {
        public final long timestamp;

        public ProcessEventResult(long timestamp) {
            this.timestamp = timestamp;
        }
    }

}