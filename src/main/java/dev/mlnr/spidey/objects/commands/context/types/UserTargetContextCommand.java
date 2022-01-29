package dev.mlnr.spidey.objects.commands.context.types;

import dev.mlnr.spidey.objects.commands.context.ContextCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command.Type;

public abstract class UserTargetContextCommand extends ContextCommand<User> {
	protected UserTargetContextCommand(String name) {
		super(Type.USER, name);
	}
}