package dev.mlnr.spidey.commands.context;

import dev.mlnr.spidey.objects.commands.CommandImplSubstitute;
import dev.mlnr.spidey.objects.commands.context.ContextCommandContext;
import dev.mlnr.spidey.objects.commands.context.types.UserTargetContextCommand;
import net.dv8tion.jda.api.entities.User;

@SuppressWarnings("unused")
public class UserContextCommand extends UserTargetContextCommand {
	public UserContextCommand() {
		super("View user");
	}

	@Override
	public <C extends ContextCommandContext<User>> boolean execute(C ctx) {
		CommandImplSubstitute.user(ctx.getTarget(), ctx.getEventAsUserContext().getTargetMember(), ctx);
		return true;
	}
}