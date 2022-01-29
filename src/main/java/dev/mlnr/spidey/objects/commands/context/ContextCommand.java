package dev.mlnr.spidey.objects.commands.context;

import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public abstract class ContextCommand<T> extends CommandDataImpl {
	protected ContextCommand(Type type, String name) {
		super(type, name);
	}

	public abstract <C extends ContextCommandContext<T>> boolean execute(C ctx);
}