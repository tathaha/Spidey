package me.canelex.Spidey.utils;

import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.canelex.Spidey.Core;

public class MiscUtils {
	
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(Core.class);	
	
    public static ThreadFactory newThreadFactory(String threadName) {
    	
        return newThreadFactory(threadName, LOG);
        
    }

    public static ThreadFactory newThreadFactory(String threadName, boolean isDaemon) {
    	
        return newThreadFactory(threadName, LOG, isDaemon);
        
    }

    public static ThreadFactory newThreadFactory(String threadName, Logger logger) {
    	
        return newThreadFactory(threadName, logger, true);
        
    }

    public static ThreadFactory newThreadFactory(String threadName, Logger logger, boolean isdaemon) {
    	
        return (r) -> {
        	
            Thread t = new Thread(r, threadName);
            t.setDaemon(isdaemon);
            
            t.setUncaughtExceptionHandler((final Thread thread, final Throwable throwable) ->
                    logger.error("There was a uncaught exception in the {} threadpool", thread.getName(), throwable));
            
            return t;
            
        };
        
    }	

}