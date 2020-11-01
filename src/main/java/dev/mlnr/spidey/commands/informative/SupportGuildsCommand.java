package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

@SuppressWarnings("unused")
public class SupportGuildsCommand extends Command
{
	public SupportGuildsCommand()
	{
		super("sguilds", new String[]{}, "Shows you the (support) guilds of Spidey", "guilds", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(final String[] args, final Message msg)
	{
		final var eb = Utils.createEmbedBuilder(msg.getAuthor());
		eb.setAuthor("Guilds of Spidey", "https://discord.gg/VQk2BUCSqM", msg.getJDA().getSelfUser().getEffectiveAvatarUrl());
		eb.addField("Spidey's Guild", "[Click to join](https://discord.gg/VQk2BUCSqM)", true);
		eb.setColor(Color.BLACK);
		Utils.sendMessage(msg.getTextChannel(), eb.build());
	}
}