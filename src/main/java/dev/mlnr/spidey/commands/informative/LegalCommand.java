package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class LegalCommand extends Command
{
	public LegalCommand()
	{
		super("legal", new String[]{"privacy", "tos", "policy", "privacypolicy"}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx)
	{
		ctx.reply(ctx.getI18n().get("commands.legal.other.text"));
	}
}