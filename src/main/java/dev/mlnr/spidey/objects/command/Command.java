package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.objects.command.category.ICategory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public abstract class Command extends CommandDataImpl {
	private final ICategory category;
	private final Permission requiredPermission;
	private final int cooldown;
	private final Command.Flags flags = new Command.Flags();

	protected Command(String name, String description, ICategory category, Permission requiredPermission, int cooldown, OptionData... options) {
		super(name, description);
		this.category = category;
		this.requiredPermission = requiredPermission;
		this.cooldown = cooldown;

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

	public void withFlags(long flags) {
		this.flags.setFlags(flags);
	}

	public boolean shouldHideResponse() {
		return flags.shouldShowResponse(); // counterintuitive but I can live with it for the sake of "backwards compatibility"
	}

	public boolean isDevOnly() {
		return flags.isDevOnly();
	}

	public boolean supportsThreads() {
		return flags.supportsThreads();
	}

	public static class Flags {
		public static final long SHOW_RESPONSE = 1 << 0;
		public static final long DEV_ONLY      = 1 << 1;
		public static final long NO_THREADS    = 1 << 2;

		private long flags;

		public void setFlags(long flags) {
			this.flags = flags;
		}

		public boolean shouldShowResponse() {
			return hasFlag(SHOW_RESPONSE);
		}

		public boolean isDevOnly() {
			return hasFlag(DEV_ONLY);
		}

		public boolean supportsThreads() {
			return !hasFlag(NO_THREADS);
		}

		private boolean hasFlag(long flag) {
			return (flags & flag) == flag;
		}
	}
}