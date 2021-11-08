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
	private final boolean devOnly;

	protected Command(String name, String description, ICategory category, Permission requiredPermission, int cooldown, OptionData... options) {
		this(name, description, category, requiredPermission, cooldown, true, false, options);
	}

	protected Command(String name, String description, ICategory category, Permission requiredPermission, int cooldown, boolean hideResponse,
	                  OptionData... options) {
		this(name, description, category, requiredPermission, cooldown, hideResponse, false, options);
	}

	protected Command(String name, String description, ICategory category, Permission requiredPermission, int cooldown, boolean hideResponse,
	                  boolean devOnly, OptionData... options) {
		super(name, description);
		this.category = category;
		this.requiredPermission = requiredPermission;
		this.cooldown = cooldown;
		this.hideResponse = hideResponse;
		this.devOnly = devOnly;

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

	public boolean isDevOnly() {
		return this.devOnly;
	}
}