package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.objects.command.category.ICategory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class Command {
	private final String invoke;
	private final String description;
	private final ICategory category;
	private final Permission requiredPermission;
	private final int cooldown;
	private final boolean hideResponse;
	private final OptionData[] options;

	protected Command(String invoke, String description, ICategory category, Permission requiredPermission, int cooldown, OptionData... options) {
		this(invoke, description, category, requiredPermission, cooldown, true, options);
	}

	protected Command(String invoke, String description, ICategory category, Permission requiredPermission, int cooldown, boolean hideResponse,
	                  OptionData... options) {
		this.invoke = invoke;
		this.description = description;
		this.category = category;
		this.requiredPermission = requiredPermission;
		this.cooldown = cooldown;
		this.hideResponse = hideResponse;
		this.options = options;
	}

	public abstract boolean execute(CommandContext ctx);

	public String getInvoke() {
		return this.invoke;
	}

	public String getDescription() {
		return this.description;
	}

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

	public OptionData[] getOptions() {
		return this.options;
	}
}