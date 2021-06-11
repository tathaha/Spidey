package dev.mlnr.spidey.handlers.command;

import dev.mlnr.spidey.objects.command.CommandBase;

import java.util.HashMap;
import java.util.Map;

public class CooldownHandler {
	private static final Map<CommandBase, Map<Long, Long>> COOLDOWN_MAP = new HashMap<>(); // K = CommandBase, V = Map<userId, Timestamp>

	private CooldownHandler() {}

	public static void cooldown(long userId, CommandBase command, boolean vip) {
		var cooldown = command.getCooldown();
		if (cooldown != 0) {
			var adjustedCooldown = adjustCooldown(cooldown, vip) * 1000L;
			COOLDOWN_MAP.computeIfAbsent(command, k -> new HashMap<>()).put(userId, System.currentTimeMillis() + adjustedCooldown);
		}
	}

	public static boolean isOnCooldown(long userId, CommandBase command) {
		var entry = COOLDOWN_MAP.get(command);
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