package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class InfoCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final var jda = e.getJDA();
		final var author = e.getAuthor();
		final var msgCh = e.getChannel();

		final var dev = jda.retrieveApplicationInfo().complete().getOwner();

		final var memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		final var duration = ManagementFactory.getRuntimeMXBean().getUptime();

		final var years = duration / 31104000000L;
		final var months = duration / 2592000000L % 12;
		final var days = duration / 86400000L % 30;
		final var hours = duration / 3600000L % 24;
		final var minutes = duration / 60000L % 60;
		final var seconds = duration / 1000L % 60;

		var uptime = (years == 0 ? "" : "**" + years + "**y, ") + (months == 0 ? "" : "**" + months + "**mo, ") + (days == 0 ? "" : "**" + days + "**d, ") + (hours == 0 ? "" : "**" + hours + "**h, ") + (minutes == 0 ? "" : "**" + minutes + "**m, ") + (seconds == 0 ? "" : "**" + seconds + "**s, ");

		uptime = Utils.replaceLast(uptime, ", ", "");
		uptime = Utils.replaceLast(uptime, ",", " and");

		final var eb = Utils.createEmbedBuilder(author);
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