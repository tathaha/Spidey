package dev.mlnr.spidey.commands.context;

import dev.mlnr.spidey.objects.commands.CommandImplSubstitute;
import dev.mlnr.spidey.objects.commands.context.ContextCommandContext;
import dev.mlnr.spidey.objects.commands.context.types.UserTargetContextCommand;
import net.dv8tion.jda.api.entities.User;

@SuppressWarnings("unused")
public class AvatarContextCommand extends UserTargetContextCommand {
	public AvatarContextCommand() {
		super("View avatar");
	}

	@Override
	public <C extends ContextCommandContext<User>> boolean execute(C ctx) {
		var target = ctx.getTarget();
		CommandImplSubstitute.avatar(target, target.getEffectiveAvatarUrl(), ctx);
		return true;
	}
}