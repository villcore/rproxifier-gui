package com.luoboduner.moo.info.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);
    private static final Scheduler INSTANCE = new Scheduler();

    private final ScheduledExecutorService scheduledExecutorService;

    private Scheduler() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(5,
                new ThreadFactory() {
                    private final AtomicLong seq = new AtomicLong(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("scheduler-" + seq.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                        LOG.warn("Reject execute runnable, current caller thead invoke runnable");
                        super.rejectedExecution(r, e);
                    }
                });
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                         long initialDelay,
                                                         long period,
                                                         TimeUnit unit) {
        return INSTANCE.scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
}
