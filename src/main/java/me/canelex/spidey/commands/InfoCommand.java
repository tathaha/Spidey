package me.canelex.spidey.commands;

import me.canelex.spidey.objects.category.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class InfoCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final JDA jda = e.getJDA();
		final User author = e.getAuthor();
		final TextChannel msgCh = e.getChannel();

		final User dev = jda.retrieveApplicationInfo().complete().getOwner();

		final long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		final long duration = ManagementFactory.getRuntimeMXBean().getUptime();

		final long years = duration / 31104000000L;
		final long months = duration / 2592000000L % 12;
		final long days = duration / 86400000L % 30;
		final long hours = duration / 3600000L % 24;
		final long minutes = duration / 60000L % 60;
		final long seconds = duration / 1000L % 60;

		String uptime = (years == 0 ? "" : "**" + years + "**y, ") + (months == 0 ? "" : "**" + months + "**mo, ") + (days == 0 ? "" : "**" + days + "**d, ") + (hours == 0 ? "" : "**" + hours + "**h, ") + (minutes == 0 ? "" : "**" + minutes + "**m, ") + (seconds == 0 ? "" : "**" + seconds + "**s, ");

		uptime = Utils.replaceLast(uptime, ", ", "");
		uptime = Utils.replaceLast(uptime, ",", " and");

		final EmbedBuilder eb = Utils.createEmbedBuilder(author);
		eb.setAuthor("About me", "https://canelex.ymastersk.net", jda.getSelfUser().getEffectiveAvatarUrl());
		eb.setColor(Color.WHITE);
		eb.addField("Developer", dev.getAsMention(), false);
		eb.addField("Ping", "**" + e.getJDA().getGatewayPing() + "**, **" + e.getJDA().getRestPing().complete() + "** ms", false);
		eb.addField("Used memory", "**" + (memory / 1000000) + "**MB", false);
		eb.addField("Uptime", uptime, false);
		Utils.sendMessage(msgCh, eb.build());

	}

	@Override
	public final String getDescription() { return "Shows you info about me"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "info"; }
	@Override
	public final Category getCategory() { return Category.INFORMATIVE; }
	@Override
	public final String getUsage() { return "s!info"; }

}