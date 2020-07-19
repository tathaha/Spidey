package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class LegalCommand extends Command
{
	public LegalCommand()
	{
		super("legal", new String[]{"privacy", "tos", "policy", "privacypolicy"}, "Shows you the ToS and Privacy Policy", "legal", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		Utils.sendMessage(message.getTextChannel(), "To see the Terms of Service and Privacy Policy, please visit the support guild: discord.gg/cnAgKrv");
	}
}