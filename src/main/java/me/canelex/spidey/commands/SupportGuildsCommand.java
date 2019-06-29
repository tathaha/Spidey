package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

@SuppressWarnings("unused")
public class SupportGuildsCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final EmbedBuilder eb = Utils.createEmbedBuilder(e.getAuthor());
		eb.setAuthor("Guilds of Spidey", "https://discord.gg/cnAgKrv", e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		eb.addField("Spidey's Guild", "[Click to join](https://discord.gg/cnAgKrv)", true);
		eb.addField("Spidey's Test Builds Guild", "[Click to join](https://discord.gg/sR4ygqU)", true);
		eb.setColor(Color.BLACK);
		Utils.sendMessage(e.getChannel(), eb.build());

	}

	@Override
	public final String getDescription() { return "Shows you (support) guilds of Spidey"; }
	@Override
	public final boolean isAdmin() { return true; }
	@Override
	public final String getInvoke() { return "sguilds"; }
	@Override
	public final Category getCategory() { return Category.INFORMATIVE; }
	@Override
	public final String getUsage() { return "s!sguilds"; }

}