package dev.mlnr.spidey.handlers.command;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;

import java.util.HashMap;
import java.util.Map;

public class CooldownHandler {
	private static final Map<SlashCommand, Map<Long, Long>> COOLDOWN_MAP = new HashMap<>(); // K = SlashCommand, V = Map<userId, Timestamp>

	private CooldownHandler() {}

	public static void cooldown(long userId, SlashCommand slashCommand, boolean vip) {
		var cooldown = slashCommand.getCooldown();
		if (cooldown != 0) {
			var adjustedCooldown = adjustCooldown(cooldown, vip) * 1000L;
			COOLDOWN_MAP.computeIfAbsent(slashCommand, k -> new HashMap<>()).put(userId, System.currentTimeMillis() + adjustedCooldown);
		}
	}

	public static boolean isOnCooldown(long userId, SlashCommand slashCommand) {
		var entry = COOLDOWN_MAP.get(slashCommand);
		if (entry == null) {
			return false;
		}
		var lastUsed = entry.get(userId);
		if (lastUsed == null) {
			return false;
		}
		if (System.currentTimeMillis() > lastUsed) {
			entry.remove(userId);
			return false;
		}
		return true;
	}

	public static int adjustCooldown(int cooldown, boolean vip) {
		return vip ? cooldown / 2 : cooldown;
	}
}