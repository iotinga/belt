package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import io.tinga.belt.output.GadgetLogRecord;
import io.tinga.belt.output.GadgetSink;

import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BeltLoggerFactory implements ILoggerFactory {

    private static final Integer BUFFER_CAPACITY = 1000;
    private static LinkedBlockingDeque<GadgetLogRecord> records = new LinkedBlockingDeque<GadgetLogRecord>(
            BUFFER_CAPACITY);

    private static GadgetSink sink;

    private static Future<?> dequeueTask;

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public BeltLoggerFactory() {
    }

    public synchronized void add(GadgetLogRecord record) {
        if (record == null) {
            return;
        }
        if (records.remainingCapacity() == 0) {
            records.pollFirst();
        }
        try {
            records.offerLast(record, Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public Logger getLogger(String name) {
        return new BeltLogger(name, this);
    }

    public synchronized static void setOutputSink(GadgetSink sink) {
        BeltLoggerFactory.sink = sink;
        if (BeltLoggerFactory.dequeueTask == null) {
            BeltLoggerFactory.dequeueTask = executor.submit(() -> {
                while (true) {
                    GadgetLogRecord record = records.pollFirst(5000, TimeUnit.MILLISECONDS);
                    if(record != null) {
                        BeltLoggerFactory.sink.put(record);
                    }
                }
            });
        }
    }

}
