package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.objects.command.category.ICategory;
import net.dv8tion.jda.api.Permission;

public abstract class Command {

	private final String invoke;
	private final String[] aliases;
	private final ICategory category;
	private final Permission requiredPermission;
	private final int maxArgs;
	private final int cooldown;

	protected Command(String invoke, String[] aliases, ICategory category, Permission requiredPermission, int maxArgs, int cooldown) {
		this.invoke = invoke;
		this.aliases = aliases;
		this.category = category;
		this.requiredPermission = requiredPermission;
		this.maxArgs = maxArgs;
		this.cooldown = cooldown;
	}

	public abstract boolean execute(String[] args, CommandContext ctx);

	public String getInvoke() {
		return this.invoke;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public ICategory getCategory() {
		return this.category;
	}

	public Permission getRequiredPermission() {
		return this.requiredPermission;
	}

	public int getMaxArgs() {
		return this.maxArgs;
	}

	public int getCooldown() {
		return this.cooldown;
	}
}