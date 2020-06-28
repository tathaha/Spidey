package dev.mlnr.spidey.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadFactoryBuilder
{
    private String name = null;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;

    public ThreadFactoryBuilder setName(final String name)
    {
        this.name = name;
        return this;
    }

    public ThreadFactoryBuilder setUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    public ThreadFactory build()
    {
        final var defaultThreadFactory = Executors.defaultThreadFactory();
        return runnable ->
        {
            final var thread = defaultThreadFactory.newThread(runnable);
            thread.setName(name);
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            return thread;
        };
    }
}