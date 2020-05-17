package me.canelex.spidey.commands.informative;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class InfoCommand extends Command
{
	public InfoCommand()
	{
		super("info", new String[]{}, "Shows you info about me", "info", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var author = message.getAuthor();
		final var msgCh = message.getChannel();
		final var jda = message.getJDA();
		final var runtime = Runtime.getRuntime();
		final var memory = runtime.totalMemory() - runtime.freeMemory();
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

		final var finalUptime = uptime;
		jda.retrieveApplicationInfo().queue(info ->
			jda.getRestPing().queue(ping ->
			{
				final var eb = Utils.createEmbedBuilder(author);
				eb.setAuthor("About me", "https://paypal.me/canelex", jda.getSelfUser().getAvatarUrl());
				eb.setColor(Color.WHITE);
				eb.addField("Developer", info.getOwner().getAsMention(), false);
				eb.addField("Ping", "**" + jda.getGatewayPing() + "**, **" + ping + "** ms", false);
				eb.addField("Used memory", "**" + (memory / 1000000) + "**MB", false);
				eb.addField("Uptime", finalUptime, false);
				eb.addField("Build date", Utils.getBuildDate(), false);
				Utils.sendMessage(msgCh, eb.build());
			}));
	}
}