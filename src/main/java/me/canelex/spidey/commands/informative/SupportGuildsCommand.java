package me.canelex.spidey.commands.informative;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;

import java.awt.*;

@SuppressWarnings("unused")
public class SupportGuildsCommand extends Command
{
	public SupportGuildsCommand()
	{
		super("sguilds", new String[]{}, "Shows you the (support) guilds of Spidey", "guilds", Category.INFORMATIVE,
				Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		eb.setAuthor("Guilds of Spidey", "https://discord.gg/cnAgKrv", message.getJDA().getSelfUser().getAvatarUrl());
		eb.addField("Spidey's Guild", "[Click to join](https://discord.gg/cnAgKrv)", true);
		eb.setColor(Color.BLACK);
		Utils.sendMessage(message.getChannel(), eb.build());
	}
}