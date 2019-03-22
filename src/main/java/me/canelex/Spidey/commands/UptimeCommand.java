package me.canelex.Spidey.commands;

import java.lang.management.ManagementFactory;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UptimeCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final long duration = ManagementFactory.getRuntimeMXBean().getUptime();

        final long years = duration / 31104000000L;
        final long months = duration / 2592000000L % 12;
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;

        String uptime = (years == 0 ? "" : "**" + years + "**y, ") + (months == 0 ? "" : "**" + months + "**mo, ") + (days == 0 ? "" : "**" + days + "**d, ") + (hours == 0 ? "" : "**" + hours + "**h, ") + (minutes == 0 ? "" : "**" + minutes + "**m, ") + (seconds == 0 ? "" : "**" + seconds + "**s, ");

        uptime = API.replaceLast(uptime, ", ", "");
        uptime = API.replaceLast(uptime, ",", " and");

        API.sendMessage(e.getChannel(), "Uptime: " + uptime + "", false);		
		
	}

	@Override
	public final String help() {
		
		return "Shows the uptime of the bot";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {
		
		return;
		
	}

}