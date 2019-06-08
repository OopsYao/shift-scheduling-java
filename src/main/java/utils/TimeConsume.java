package utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeConsume {

    private long start;

    private long last;

    private int ary;

    public TimeConsume() {
        start = System.nanoTime();
        last = start;
        ary = 1000000000;
    }

    public void printTimeConsumed() {
        long l = System.nanoTime();
        System.out.println((l - last) + "ns");
        last = l;
    }

    public void printTimeConsumed(TimeUnit timeUnit) {
    }

    public long getTimeGap() {
        long l = System.nanoTime();
        long gap = l - last;
        last = l;
        return gap;
    }
}
