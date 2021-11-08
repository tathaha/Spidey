package dev.mlnr.spidey.utils;

import net.dv8tion.jda.internal.utils.config.ThreadingConfig;

import java.util.concurrent.ScheduledExecutorService;

public class ConcurrentUtils {
	private static final ScheduledExecutorService SCHEDULER = ThreadingConfig.newScheduler(2, () -> "Spidey", "Executor");

	private ConcurrentUtils() {}

	public static ScheduledExecutorService getScheduler() {
		return SCHEDULER;
	}
}