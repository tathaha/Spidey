package dev.mlnr.spidey.utils.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ConcurrentUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentUtils.class);
    private static final ThreadFactoryBuilder THREAD_FACTORY = new ThreadFactoryBuilder().setUncaughtExceptionHandler((t, e) -> LOG.error("There was an exception in thread {}: {}", t.getName(), e.getMessage()));

    private ConcurrentUtils()
    {
        super();
    }

    public static ScheduledExecutorService createScheduledThread(final String name)
    {
        return Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY.setName(name).build());
    }

    public static ExecutorService createThread(final String name)
    {
        return Executors.newSingleThreadExecutor(THREAD_FACTORY.setName(name).build());
    }
}