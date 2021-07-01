package dev.mlnr.spidey.utils;

import net.dv8tion.jda.internal.utils.config.ThreadingConfig;

import java.util.concurrent.ScheduledExecutorService;

public class ConcurrentUtils {
	private static final ScheduledExecutorService SCHEDULER = ThreadingConfig.newScheduler(1, () -> "Spidey", "Scheduler");

	private ConcurrentUtils() {}

	public static ScheduledExecutorService getScheduler() {
		return SCHEDULER;
	}
}