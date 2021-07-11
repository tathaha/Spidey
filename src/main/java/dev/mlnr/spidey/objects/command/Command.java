package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.objects.command.category.ICategory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class Command extends CommandData {
	private final ICategory category;
	private final Permission requiredPermission;
	private final int cooldown;
	private final boolean hideResponse;

	protected Command(String name, String description, ICategory category, Permission requiredPermission, int cooldown, OptionData... options) {
		this(name, description, category, requiredPermission, cooldown, true, options);
	}

	protected Command(String name, String description, ICategory category, Permission requiredPermission, int cooldown, boolean hideResponse,
	                  OptionData... options) {
		super(name, description);
		this.category = category;
		this.requiredPermission = requiredPermission;
		this.cooldown = cooldown;
		this.hideResponse = hideResponse;

		addOptions(options);
	}

	public abstract boolean execute(CommandContext ctx);

	public ICategory getCategory() {
		return this.category;
	}

	public Permission getRequiredPermission() {
		return this.requiredPermission;
	}

	public int getCooldown() {
		return this.cooldown;
	}

	public boolean shouldHideResponse() {
		return this.hideResponse;
	}
}