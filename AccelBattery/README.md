# AccelBattery
A sample project to show how to save android battery with an accelerometer.

```java

Subscription monitoringSchedule = gestureSensorMonitor.schedule()
                .every(60000)
                .work(1500)
                .samplingPeriod(100)
                .start();

```