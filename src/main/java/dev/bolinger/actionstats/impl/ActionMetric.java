package dev.bolinger.actionstats.impl;

import java.util.concurrent.atomic.LongAdder;

/**
 * Internal class for concurrent access to a running average.
 * See the LongAdder class description for usage with ConcurrentHashMap.
 */
public class ActionMetric {
    private LongAdder count = new LongAdder();
    private LongAdder sum = new LongAdder();

    public ActionMetric() {
    }

    public void add(long x) {
        count.increment();
        sum.add(x);
    }

    public long getAverage() {
        return sum.longValue() / Math.max(1L, count.longValue());
    }
}
